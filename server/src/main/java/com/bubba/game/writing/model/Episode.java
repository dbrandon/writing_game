package com.bubba.game.writing.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.bubba.game.writing.rest.types.BasicResponse;
import com.bubba.game.writing.rest.types.EpisodeArchive;
import com.bubba.game.writing.rest.types.EpisodeArchiveResponse;
import com.bubba.game.writing.rest.types.FinishedStoryResponse;
import com.bubba.game.writing.rest.types.FragmentUpdate;
import com.bubba.game.writing.rest.types.RoomStatus;
import com.bubba.game.writing.rest.types.RoomStatusResponse;
import com.bubba.game.writing.rest.types.WriterStatus;

/**
 * Group of stories that were rotated between authors
 */
public class Episode {
  private Logger log = LogManager.getLogManager().getLogger(Episode.class.getSimpleName());
  
  private final static int MAX_ROUNDS = 5;
  /** period to wait in millis before moving to next round */
  private final static int FINISH_GRACE_MILLIS = 3000;
  
  /** time to wait to move on to the next episode */
  private final static int EPISODE_TIMER_MILLIS = 10000;
  
  private String roomName;
  
  private List<Story> storyList;
  private Story selectedStory;
  private long createDate;
  private int roundNumber;
  private int maxRounds;
  private boolean finished;
  
  private long roundTimeout;
  private long episodeTimeout;

  public static Episode create(String roomName) {
    Episode episode = new Episode();
    
    episode.setCreateDate(System.currentTimeMillis());
    episode.setMaxRounds(MAX_ROUNDS);
    episode.setRoundNumber(1);
    episode.roomName = roomName;
    
    return episode;
  }
  
  Story addAuthor(Session session) {
    Story story = getStoryList().stream().reduce(null, (result, element) -> {
      return element.getWorkingFragment().getAuthorSessionId().equals(session.getSessionId()) ? element : result;
    });
    
    if(story == null) {
      log.log(Level.INFO, "Creating new story for author " + session.getName()); 
      story = createStory(session.getSessionId());
      getStoryList().add(story);
    }
    else {
      log.log(Level.INFO, "Author " + session.getName() + " returning with existing story");
    }
    
    return story;
  }
  
  private Story createStory(String creatorId) {
    Story story = new Story();
    story.setCreatorSessionId(creatorId);
    return story;
  }
  
  public long getCreateDate() {
    return createDate;
  }
  
  public void setCreateDate(long createDate) {
    this.createDate = createDate;
  }
  
  public boolean isFinished() {
    return finished;
  }
  
  public void setFinished(boolean finished) {
    this.finished = finished;
  }
  
  public boolean isEpisodeTimerFinished() {
    return episodeTimeout != 0 && (System.currentTimeMillis() >= episodeTimeout);
  }
  
  public int getMaxRounds() {
    return maxRounds;
  }
  
  public void setMaxRounds(int maxRounds) {
    this.maxRounds = maxRounds;
  }
  
  public int getRoundNumber() {
    return roundNumber;
  }
  
  public void setRoundNumber(int roundNumber) {
    this.roundNumber = roundNumber;
  }
  
  public List<Story> getStoryList() {
    if(storyList == null) {
      storyList = new ArrayList<Story>();
    }
    return storyList;
  }
  
  public void setStoryList(List<Story> storyList) {
    this.storyList = storyList;
  }
  
  RoomStatusResponse applyUpdate(FragmentUpdate update, Session session, Map<String,Session> sessionMap, Session roomLeader) {
    if(isFinished()) {
      return RoomStatusResponse.createFailure("Stories are finished and cannot be updated");
    }
    
    Story story = getStoryList().stream().reduce(null, (r,e) -> {
      return e.getWorkingFragment().getAuthorSessionId().equals(update.getSessionId()) ? e : r;
    });
    
    if(story == null) {
      story = addAuthor(session);
    }
    
    story.applyUpdate(update);
    updateFragmentFinishedTimer(sessionMap);
    
    return RoomStatusResponse.createSuccess(getRoomStatus(session.getSessionId(), sessionMap, roomLeader));
  }
  
  BasicResponse handleUpdateFragmentLimit(int limit) {
    if(isFinished()) {
      return BasicResponse.createFailure("Cannot change limit after stories are written.");
    }
    
    if(limit < roundNumber) {
      return BasicResponse.createFailure("Cannot set the limit below the current round");
    }
    
    this.maxRounds = limit;
    return BasicResponse.createSuccess();
  }
  
  void updateFragmentFinishedTimer(Map<String, Session> sessionMap) {
    boolean allStoriesReady = true;
    boolean haveUnexpiredAuthor = false;
      
    for(Story s : getStoryList()) {
      StoryFragment fragment = s.getWorkingFragment();
      Session session = sessionMap.get(fragment.getAuthorSessionId());
      boolean authorExpired = session == null || session.isExpired();
      boolean reportedAfk = session != null && session.isReportedAfk();
      
      if(!authorExpired && !reportedAfk) {
        haveUnexpiredAuthor = true;
      }
      
      if(!fragment.isFinished() && !authorExpired && !reportedAfk) {
        allStoriesReady = false;
        break;
      }
    }
      
    if(roundTimeout == 0 && allStoriesReady && haveUnexpiredAuthor) {
      roundTimeout = System.currentTimeMillis() + FINISH_GRACE_MILLIS;
      log.log(Level.INFO, "Starting 10 second timer for round to finish!");
    }
    else if(roundTimeout != 0 && (!allStoriesReady || !haveUnexpiredAuthor)) {
      log.log(Level.INFO, "Canceling round finish timer!");
      roundTimeout = 0;
    }
  }
  
  String startNewEpisodeTimer() {
    if(!isFinished()) {
      return "episode not finished";
    }
    else if(episodeTimeout == 0) {
      episodeTimeout = System.currentTimeMillis() + EPISODE_TIMER_MILLIS;
    }
    return "OK";
  }
  
  String cancelNewEpisodeTimer() {
    episodeTimeout = 0;
    return "OK";
  }
  
  void pollStatus(Map<String,Session> sessionMap) {
    if(roundTimeout != 0 && System.currentTimeMillis() >= roundTimeout) {
      roundTimeout = 0;
      if(roundNumber == maxRounds) {
        log.log(Level.INFO, "Episode is finished!");
        getStoryList().forEach(story -> story.finishStory());
        finished = true;
      }
      else {
        log.log(Level.INFO, "Need to advance to next round!");
        roundNumber++;
        rotateStoriesLeft(sessionMap);
      }
    }
  }
  
  private void rotateStoriesLeft(Map<String,Session> sessionMap) {
    Map<Story, StoryFragment> fragmentMap = new HashMap<Story,StoryFragment>();
    List<Story> filteredList = new ArrayList<Story>();
    int size;
    
    // filter out all stories where people were not present
    getStoryList().forEach(story -> {
      Session session = sessionMap.get(story.getWorkingFragment().getAuthorSessionId());
      boolean include = session != null && !session.isExpired() && !session.isReportedAfk();
      
      if(include) {
        filteredList.add(story);
      }
    });
    
    size = filteredList.size();
    
    for(int i = 0; i < size; i++) {
      int from = i + 1;
      if(from >= size) {
        from -= size;
      }
      
      fragmentMap.put(filteredList.get(i), filteredList.get(from).getWorkingFragment());
    }
    
    filteredList.forEach(story -> {
      StoryFragment src = fragmentMap.get(story);
      StoryFragment next = new StoryFragment();
      
      next.setAuthorSessionId(src.getAuthorSessionId());
      story.nextFragment(next);
    });
  }
  
  FinishedStoryResponse getFinishedStory(Map<String,Session> sessionMap) {
    if(!isFinished()) {
      return FinishedStoryResponse.createFailure("Stories are still being written!");
    }
    else if(selectedStory == null) {
      return FinishedStoryResponse.createWaiting();
    }
    else {
      return selectedStory.getFinishedStory(sessionMap);
    }
  }
  
  RoomStatus getRoomStatus(String sessionId, Map<String, Session> sessionMap, Session roomLeader) {
    RoomStatus status = new RoomStatus();
    
    status.setEpisodeDone(finished);
    status.setMaxRounds(maxRounds);
    status.setRoomName(roomName);
    status.setRoundNumber(roundNumber);
    status.setRoundTimerRunning(roundTimeout != 0);
    if(roundTimeout != 0) {
      long remaining = roundTimeout - System.currentTimeMillis();
      
      if(remaining < 0) {
        remaining = 0;
      }
      status.setRoundTimeRemaining(remaining);
    }
    
    status.setEpisodeTimerRunning(this.episodeTimeout != 0);
    if(episodeTimeout != 0) {
      long remaining = episodeTimeout - System.currentTimeMillis();
      
      if(remaining < 0) {
        remaining = 0;
      }
      status.setEpisodeTimeRemaining(remaining);
    }
    
    if(selectedStory != null) {
      Session authorSession = sessionMap.get(selectedStory.getCreatorSessionId());
      
      if(authorSession == null) {
        log.log(Level.WARNING, "Failed to find author session ID of selected story!");
      }
      else {
        status.setSelectedStoryAuthorId(authorSession.getPublicSessionId());
      }
    }
    
    getStoryList().forEach(story -> {
      StoryFragment frag = story.getWorkingFragment();
      WriterStatus writerStatus = new WriterStatus();
      Session session = sessionMap.get(frag.getAuthorSessionId());
      
      writerStatus.setAuthor(session.getName());
      writerStatus.setExpired(session == null || session.isExpired());
      writerStatus.setFinished(frag.isFinished() && !writerStatus.isExpired());
      writerStatus.setFont(session.getFont());
      writerStatus.setMyStatus(sessionId.equals(frag.getAuthorSessionId()));
      writerStatus.setPublicId(session.getPublicSessionId());
      writerStatus.setReportedAfk(session != null && session.isReportedAfk());
      writerStatus.setRoomLeader(session == roomLeader);
      
      if(writerStatus.isMyStatus()) {
        if(!finished) {
          StoryFragment previous = story.getPreviousFragment();
        
          status.setFragmentDone(frag.isFinished());
          if(previous != null) {
            Session prevAuthorSession = sessionMap.get(previous.getAuthorSessionId());
            
            status.setPrevAuthorPublicId(prevAuthorSession == null ? null : prevAuthorSession.getPublicSessionId());
            status.setPrevVisibleFragment(previous.getVisibleText());
          }
          status.setWorkingHiddenFragment(frag.getHiddenText());
          status.setWorkingVisibleFragment(frag.getVisibleText());
        }
        
        status.getWriterStatusList().add(0, writerStatus);
      }
      else {
        status.getWriterStatusList().add(writerStatus);
      }
    });
    
    return status;
  }
  
  EpisodeArchiveResponse getEpisodeArchive(Map<String,Session> sessionMap) {
    EpisodeArchive archive = new EpisodeArchive();
    
    if(!finished) {
      return EpisodeArchiveResponse.createFailure("Cannot get archive until episode is finished.");
    }
    
    getStoryList().forEach(story -> {
      FinishedStoryResponse resp = story.getFinishedStory(sessionMap);
      
      if(resp.isSuccess()) {
        archive.getStoryList().add(resp.getStory());
      }
    });
    
    return EpisodeArchiveResponse.createSuccess(archive);
  }
  
  BasicResponse selectFinishedStory(Session authorSession) {
    Story story = null;
    
    if(!finished) {
      return BasicResponse.createFailure("Cannot select story until episode is finished!");
    }
   
    for(Story s : getStoryList()) {
      if(s.getCreatorSessionId().equals(authorSession.getSessionId())) {
        story = s;
        break;
      }
    }
    
    if(story == null) {
      return BasicResponse.createFailure("Could not find story for author " + authorSession.getName());
    }
    
    selectedStory = story;
    return BasicResponse.createSuccess();
  }
}
