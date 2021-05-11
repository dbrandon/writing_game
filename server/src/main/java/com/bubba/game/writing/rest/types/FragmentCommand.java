package com.bubba.game.writing.rest.types;

public class FragmentCommand {
  public enum Type {
    MARK_AFK,
    MARK_NOT_AFK,
    KICK,
    PROMOTE_LEADER
  }
  
  private String sessionId;
  private String authorPublicId;
  private Type command;
  
  public String getAuthorPublicId() {
    return authorPublicId;
  }
  
  public void setAuthorPublicId(String authorPublicId) {
    this.authorPublicId = authorPublicId;
  }
  
  public Type getCommand() {
    return command;
  }
  
  public void setCommand(Type command) {
    this.command = command;
  }
  
  public String getSessionId() {
    return sessionId;
  }
  
  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }
}
