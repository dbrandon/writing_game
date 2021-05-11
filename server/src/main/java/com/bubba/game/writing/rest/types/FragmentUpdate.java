package com.bubba.game.writing.rest.types;

public class FragmentUpdate {
  private String sessionId;
  private String hiddenText;
  private String visibleText;
  private boolean finished;
  
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
  
  public String getSessionId() {
    return sessionId;
  }
  
  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }
  
  public String getVisibleText() {
    return visibleText;
  }
  
  public void setVisibleText(String visibleText) {
    this.visibleText = visibleText;
  }
}
