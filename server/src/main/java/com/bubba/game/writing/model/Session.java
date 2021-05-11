package com.bubba.game.writing.model;

import java.util.UUID;

import com.bubba.game.writing.rest.types.FontFamily;
import com.bubba.game.writing.rest.types.SessionInfo;

public class Session {
  private String sessionId;
  private String publicSessionId;
  private long lastAccess;
  private String name;
  private boolean expired;
  private boolean reportedAfk;
  private FontFamily font;
  
  public static Session generate() {
    Session session = new Session();
    
    session.lastAccess = System.currentTimeMillis();
    session.setFont(FontFamily.DefaultFont);
    session.setSessionId(null);
    session.setPublicSessionId(null);
    return session;
  }
  
  public SessionInfo getSessionInfo() {
    SessionInfo info = new SessionInfo();
    
    info.setName(name);
    info.setSessionId(sessionId);
    return info;
  }
  
  public void apply(SessionInfo info) {
    setName(info.getName());
    setLastAccess(System.currentTimeMillis());
  }
  
  public boolean isExpired() {
    return expired;
  }
  
  public void setExpired(boolean expired) {
    this.expired = expired;
  }
  
  public FontFamily getFont() {
    return font == null ? FontFamily.DefaultFont : font;
  }
  
  public void setFont(FontFamily font) {
    this.font = font;
  }
  
  public long getLastAccess() {
    return lastAccess;
  }
  
  public void setLastAccess(long lastAccess) {
    this.lastAccess = lastAccess;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    if(name == null || name.isEmpty()) {
      name = "NO NAME";
    }
    this.name = name;
  }
  
  public boolean isReportedAfk() {
    return reportedAfk;
  }
  
  public void setReportedAfk(boolean reportedAfk) {
    this.reportedAfk = reportedAfk;
  }
  
  public String getPublicSessionId() {
    return publicSessionId;
  }
  
  public void setPublicSessionId(String publicSessionId) {
    this.publicSessionId = validateSessionId(publicSessionId);
  }
  
  public String getSessionId() {
    return sessionId;
  }
  
  public void setSessionId(String sessionId) {
    this.sessionId = validateSessionId(sessionId);
  }
  
  private static String validateSessionId(String sessionId) {
    if(sessionId == null || sessionId.isEmpty()) {
      sessionId = UUID.randomUUID().toString();
    }
    return sessionId;
  }
}
