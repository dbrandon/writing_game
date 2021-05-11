package com.bubba.game.writing.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.bubba.game.writing.rest.types.FinishedFragment;
import com.bubba.game.writing.rest.types.FinishedStory;
import com.bubba.game.writing.rest.types.FinishedStoryResponse;
import com.bubba.game.writing.rest.types.FontFamily;
import com.bubba.game.writing.rest.types.FragmentUpdate;

public class Story {
  Logger log = LogManager.getLogManager().getLogger(Story.class.getSimpleName());
  
  private List<StoryFragment> fragmentList;
  private String creatorSessionId;
  
  private StoryFragment workingFragment;
  
  private boolean finished;
  
  public String getCreatorSessionId() {
    return creatorSessionId;
  }
  
  public void setCreatorSessionId(String creatorSessionId) {
    this.creatorSessionId = creatorSessionId;
  }
  
  public boolean isFinished() {
    return finished;
  }
  
  public void setFinished(boolean finished) {
    this.finished = finished;
  }
  
  public List<StoryFragment> getFragmentList() {
    if(fragmentList == null) {
      fragmentList = new ArrayList<StoryFragment>();
    }
    return fragmentList;
  }
  
  public StoryFragment getWorkingFragment() {
    if(workingFragment == null) {
      workingFragment = new StoryFragment();
      workingFragment.setAuthorSessionId(creatorSessionId);
    }
    return workingFragment;
  }

  public void setWorkingFragment(StoryFragment workingFragment) {
    this.workingFragment = workingFragment;
  }
  
  void applyUpdate(FragmentUpdate update) {
    getWorkingFragment().applyUpdate(update);
  }
  
  void finishStory() {
    if(!finished) {
      getFragmentList().add(getWorkingFragment());
      finished = true;
    }
  }
  
  void nextFragment(StoryFragment fragment) {
    getFragmentList().add(getWorkingFragment());
    workingFragment = fragment;
  }

  /**
   * Returns the previous fragment or null if still on the first fragment
   * @return
   */
  StoryFragment getPreviousFragment() {
    int len = getFragmentList().size();
    return len < 1 ? null : getFragmentList().get(len-1);
  }
  
  FinishedStoryResponse getFinishedStory(Map<String,Session> sessionMap) {
    if(!isFinished()) {
      return FinishedStoryResponse.createFailure("Stories are still being written!");
    }
    
    FinishedStory story = new FinishedStory();
    Session creatorSession = sessionMap.get(getCreatorSessionId());
    String name = creatorSession == null ? null : creatorSession.getName();
    
    story.setAuthor(name == null ? "UNKNOWN" : name);
    story.setFont(creatorSession == null || creatorSession.getFont() == null ?
        FontFamily.DefaultFont : creatorSession.getFont());
    getFragmentList().forEach(frag -> {
      FinishedFragment ff = new FinishedFragment();
      Session session = sessionMap.get(frag.getAuthorSessionId());
      
      ff.setAuthor(session == null ? "UNKNOWN" : session.getName());
      ff.setFont(session == null || session.getFont() == null ? FontFamily.DefaultFont : session.getFont());
      ff.setHiddenText(frag.getHiddenText());
      ff.setVisibleText(frag.getVisibleText());
      
      story.getFragmentList().add(ff);
    });
    
    return FinishedStoryResponse.createSuccess(story);
  }
}
