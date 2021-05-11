package com.bubba.game.writing.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.bubba.game.writing.rest.types.BasicResponse;
import com.bubba.game.writing.rest.types.EpisodeArchiveResponse;
import com.bubba.game.writing.rest.types.FinishedStoryResponse;
import com.bubba.game.writing.rest.types.FragmentCommand;
import com.bubba.game.writing.rest.types.FragmentUpdate;
import com.bubba.game.writing.rest.types.RoomInfo;
import com.bubba.game.writing.rest.types.RoomStatus;
import com.bubba.game.writing.rest.types.RoomStatusResponse;
import com.bubba.game.writing.rest.types.SessionInfo;
import com.bubba.game.writing.rest.types.UserPrefs;

public class WritingRoom {
  private Logger log = LogManager.getLogManager().getLogger(WritingRoom.class.getSimpleName());
  private final static long SESSION_TIMEOUT_MILLIS = 30000;

  private String roomName;
  
  private List<Episode> episodeList;
  private Episode currentEpisode;
  private Map<String,Session> sessionMap = new HashMap<String,Session>();
  private Session roomLeader;
  
  WritingRoom(String roomName) {
    this.roomName = roomName;
  }
  
  boolean containsSession(String sessionId) {
    synchronized(this) {
      return sessionMap.containsKey(sessionId);
    }
  }
  
  SessionInfo lookupSession(String sessionId) {
    synchronized(this) {
      return toSessionInfo(sessionMap.get(sessionId));
    }
  }
  
  SessionInfo updateOrCreateSession(SessionInfo newInfo) {
    synchronized(this) {
      Session session = sessionMap.get(newInfo.getSessionId());
      SessionInfo info;
      
      if(session == null) {
        String name = newInfo.getName();
        
        while(session == null) {
          Session temp = Session.generate();
          if(!sessionMap.containsKey(temp.getSessionId())) {
            session = temp;
          }
        }
        
        if(name == null || name.isEmpty()) {
          name = "NO NAME";
        }
        session.setName(name);
        sessionMap.put(session.getSessionId(), session);
        log.log(Level.INFO, "Adding session " + session.getSessionId() + "(" +
            session.getName() + ") to room " + getRoomName());
      }
      else {
        session.setLastAccess(System.currentTimeMillis());
        session.setName(newInfo.getName());
      }
      
      pickNewLeader();
      info = toSessionInfo(session);
      getCurrentEpisode().addAuthor(session);
      
      return info;
    }
  }
  
  private SessionInfo toSessionInfo(Session session) {
    SessionInfo info = null;
    
    if(session != null) {
      info = session.getSessionInfo();
      info.setRoomName(roomName);
    }
    return info;
  }
  
  public RoomInfo getRoomInfo() {
    RoomInfo info = new RoomInfo();
    
    info.setRoomName(roomName);
    return info;
  }
  
  public BasicResponse handleUpdateFragmentLimit(String sessionId, int limit) {
    if(limit < 1) {
      return BasicResponse.createFailure("Cannot set the limit to less than one (1)");
    }
    
    synchronized(this) {
      return getCurrentEpisode().handleUpdateFragmentLimit(limit);
    }
  }
  
  public Episode getCurrentEpisode() {
    synchronized(this) {
      if(currentEpisode == null) {
        currentEpisode = Episode.create(getRoomName());
        getEpisodeList().add(currentEpisode);
      }
    }
    
    return currentEpisode;
  }
  
  public List<Episode> getEpisodeList() {
    if(episodeList == null) {
      episodeList = new ArrayList<Episode>();
    }
    return episodeList;
  }
  
  public String getRoomName() {
    return roomName;
  }
  
  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }
  
  RoomStatusResponse applyUpdate(FragmentUpdate update) {
    synchronized(this) {
      if(update == null) {
        return RoomStatusResponse.createFailure("Fragment update was missing");
      }
      
      Session session = sessionMap.get(update.getSessionId());
        
      if(session == null) {
        log.warning("Got request to update from unknown session ID=" + update.getSessionId());
        return RoomStatusResponse.createFailure("Failed to locate session ID");
      }
      else {
        log.log(Level.INFO, "Apply update from " + session.getSessionId() + "(" + session.getName() + ")");
        session.setLastAccess(System.currentTimeMillis());
        session.setReportedAfk(false);
        expireSessions();
        
        return getCurrentEpisode().applyUpdate(update, session, sessionMap, roomLeader);
      }
    }
  }
  
  RoomStatusResponse getRoomStatus(String sessionId) {
    synchronized(this) {
      Session session = sessionMap.get(sessionId);
      
      if(session == null) {
        return RoomStatusResponse.createFailure("Could not find status for session ID=" + sessionId);
      }
      
      session.setLastAccess(System.currentTimeMillis());
      expireSessions();
      
      RoomStatus status = getCurrentEpisode().getRoomStatus(sessionId, sessionMap, roomLeader);
      
      status.setRoomName(getRoomName());
      return RoomStatusResponse.createSuccess(status);
    }
  }
  
  BasicResponse runCommand(FragmentCommand command) {
    synchronized(this) {
      Session session = sessionMap.values().stream().reduce(null, (r,e) -> {
        return e.getPublicSessionId().equals(command.getAuthorPublicId()) ? e : r;
      });
      
      if(session == null) {
        return BasicResponse.createFailure("Could not find author.");
      }
      
      switch(command.getCommand()) {
      case KICK :
        return BasicResponse.createFailure("Kick not supported");
        
      case PROMOTE_LEADER :
        return promoteLeader(command.getAuthorPublicId());
        
      case MARK_AFK :
        session.setReportedAfk(true);
        return BasicResponse.createSuccess();
        
      case MARK_NOT_AFK :
        session.setReportedAfk(false);
        return BasicResponse.createSuccess();
      }
      
      log.log(Level.WARNING, "Unrecognized command: " + command.getCommand());
      return BasicResponse.createFailure("Unrecognized command");
    }
  }
  
  private BasicResponse promoteLeader(String authorId) {
    synchronized(this) {
      Session session = null;
      
      for(Session s : sessionMap.values()) {
        if(s.getPublicSessionId().equals(authorId)) {
          session = s;
        }
      }
      
      if(session == null) {
        return BasicResponse.createFailure("Could not find author ID");
      }
      if(session.isExpired()) {
        return BasicResponse.createFailure("Cannot promote expired author to leader!");
      }
      roomLeader = session;
      return BasicResponse.createSuccess();
    }
  }
  
  BasicResponse selectFinishedStory(String sessionId, String authorId) {
    synchronized(this) {
      Session session = sessionMap.get(sessionId);
      Session author = null;
      
      for(Session s : sessionMap.values()) {
        if(s.getPublicSessionId().equals(authorId)) {
          author = s;
          break;
        }
      }
      
      if(session == null) {
        return BasicResponse.createFailure("Could not find your session ID");
      }
      if(session != roomLeader) {
        return BasicResponse.createFailure("Only the room leader can select a story");
      }
      if(author == null) {
        return BasicResponse.createFailure("Author session not found");
      }
      
      return getCurrentEpisode().selectFinishedStory(author);
    }
  }
  
  public FinishedStoryResponse getFinishedStory(String sessionId) {
    synchronized(this) {
      Session session = sessionMap.get(sessionId);
      
      if(session == null) {
        return FinishedStoryResponse.createFailure("Could not find session in room");
      }
      
      return getCurrentEpisode().getFinishedStory(sessionMap);
    }
  }
  
  BasicResponse updatePrefs(UserPrefs prefs) {
    synchronized(this) {
      Session session = sessionMap.get(prefs.getSessionId());
      
      if(session == null) {
        return BasicResponse.createFailure("Could not find author");
      }
      
      if(prefs.getName() != null && !prefs.getName().isEmpty()) {
        session.setName(prefs.getName());
      }
      
      if(prefs.getFont() != null) {
        session.setFont(prefs.getFont());
      }
      
      return BasicResponse.createSuccess();
    }
  }
  
  String startNewEpisode() {
    return getCurrentEpisode().startNewEpisodeTimer();
  }
  
  String cancelNewEpisode() {
    return getCurrentEpisode().cancelNewEpisodeTimer();
  }
  
  EpisodeArchiveResponse getEpisodeArchive() {
    synchronized(this) {
      return getCurrentEpisode().getEpisodeArchive(sessionMap);
    }
  }
  
  void pollStatus() {
    synchronized(this) {
      expireSessions();
      
      if(!getCurrentEpisode().isFinished()) {
        getCurrentEpisode().updateFragmentFinishedTimer(sessionMap);
        getCurrentEpisode().pollStatus(sessionMap);
      }
      
      if(getCurrentEpisode().isEpisodeTimerFinished()) {
        Episode prev = getCurrentEpisode();
        
        getEpisodeList().add(prev);
        currentEpisode = null;
        sessionMap.values().forEach(session-> {
          if(!session.isExpired() && !session.isReportedAfk()) {
            getCurrentEpisode().addAuthor(session);
            log.log(Level.INFO, "After adding author, there are " +
                getCurrentEpisode().getStoryList().size() + " stories.");
          }
        });
      }
    }
  }
  
  private void expireSessions() {
    long now = System.currentTimeMillis();
    
    synchronized(this) {
      for(Session session : sessionMap.values()) {
        boolean wasExpired = session.isExpired();
        
        session.setExpired((now - session.getLastAccess()) > SESSION_TIMEOUT_MILLIS);
        if(session.isExpired() != wasExpired) {
          log.log(Level.INFO, "Session " + session.getSessionId() +
              "(" + session.getName() + ") is now " + (session.isExpired() ? "expired" : "unexpired"));
        }
      }
      
      pickNewLeader();
    }
  }
  
  private void pickNewLeader() {
    synchronized(this) {
      boolean ok = roomLeader != null && sessionMap.values().contains(roomLeader);
      
      if(!ok || roomLeader.isExpired()) {
        Session newLeader = null;
        
        for(Session session : sessionMap.values()) {
          if(!session.isExpired()) {
            newLeader = session;
            break;
          }
        }
        
        if(newLeader != null) {
          roomLeader = newLeader;
        }
      }
    }
  }
}
