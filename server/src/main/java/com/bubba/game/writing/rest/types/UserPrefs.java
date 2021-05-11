package com.bubba.game.writing.rest.types;

public class UserPrefs {
  private String name;
  private String sessionId;
  private FontFamily font;
  
  public FontFamily getFont() {
    return font;
  }
  
  public void setFont(FontFamily font) {
    this.font = font;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getSessionId() {
    return sessionId;
  }
  
  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }
}
