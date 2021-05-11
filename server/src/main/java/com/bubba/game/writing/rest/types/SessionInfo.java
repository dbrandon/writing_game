package com.bubba.game.writing.rest.types;

public class SessionInfo {
  private String sessionId;
  private String name;
  private String roomName;
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getRoomName() {
    return roomName;
  }
  
  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }
  
  public String getSessionId() {
    return sessionId;
  }
  
  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }
}
