package com.bubba.game.writing.model;

import com.bubba.game.writing.rest.types.FragmentUpdate;

public class StoryFragment {
  private String hiddenText;
  private String visibleText;
  private String authorSessionId;
  private boolean finished;
  
  public String getAuthorSessionId() {
    return authorSessionId;
  }
  
  public void setAuthorSessionId(String authorSessionId) {
    this.authorSessionId = authorSessionId;
  }
  
  /**
   * Returns true if the author has marked this fragment as finished
   * @return
   */
  public boolean isFinished() {
    return finished;
  }
  
  public void setFinished(boolean finished) {
    this.finished = finished;
  }
  
  public String getHiddenText() {
    return hiddenText;
  }
  
  public void setHiddenText(String hiddenText) {
    this.hiddenText = hiddenText;
  }
  
  public String getVisibleText() {
    return visibleText;
  }
  
  public void setVisibleText(String visibleText) {
    this.visibleText = visibleText;
  }
  
  void applyUpdate(FragmentUpdate update) {
    System.out.println("apply update to fragment, from " + update.getSessionId());
    setFinished(update.isFinished());
    setHiddenText(update.getHiddenText());
    setVisibleText(update.getVisibleText());
  }
}
