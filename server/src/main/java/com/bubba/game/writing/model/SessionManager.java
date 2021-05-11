package com.bubba.game.writing.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.bubba.game.writing.rest.types.SessionInfo;

public class SessionManager {
  private Logger log = LogManager.getLogManager().getLogger(SessionManager.class.getSimpleName());
  
  private final static long SESSION_TIMEOUT_MILLIS = 30000;
  
  private static SessionManager instance;
  
  private Map<String, Session> sessionMap;
  
  private SessionManager() {
    sessionMap = new HashMap<String,Session>();
  }
  
  public static SessionManager getInstance() {
    if(instance == null) {
      instance = new SessionManager();
    }
    return instance;
  }
  
  public SessionInfo createSession() {
    Session session = Session.generate();

    synchronized(sessionMap) {
      sessionMap.put(session.getSessionId(), session);
    }
    
    return session.getSessionInfo();
  }
  
  public SessionInfo getSession(String sessionId) {
    synchronized(sessionMap) {
      Session session = sessionMap.get(sessionId);
      
      if(session != null) {
        session.setLastAccess(System.currentTimeMillis());
        return session.getSessionInfo();
      }
      
      return null;
    }
  }
  
  public void pollStatus() {
    List<SessionInfo> expiredList = new ArrayList<SessionInfo>();
    
    synchronized(sessionMap) {
      sessionMap.values().forEach(session -> {
        long age = System.currentTimeMillis() - session.getLastAccess();
        
        if(age >= SESSION_TIMEOUT_MILLIS && !session.isExpired()) {
          session.setExpired(true);
          expiredList.add(session.getSessionInfo());
        }
      });
    }
    
    ////RoomManager.getInstance().handleExpiredSessions(expiredList);
  }
  
  public SessionInfo updateSession(SessionInfo info) {
    SessionInfo respInfo;

    synchronized(sessionMap) {
      Session session = sessionMap.get(info.getSessionId());
      
      if(session == null) {
        session = new Session();
        session.setSessionId(info.getSessionId());
        sessionMap.put(info.getSessionId(), session);
      }
      session.apply(info);
      respInfo = session.getSessionInfo();
    }
    
////    RoomManager.getInstance().updateAuthorInfo(respInfo);
    
    return respInfo;
  }
}
