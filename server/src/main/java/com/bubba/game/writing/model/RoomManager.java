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
import com.bubba.game.writing.rest.types.FragmentUpdate;
import com.bubba.game.writing.rest.types.FragmentCommand;
import com.bubba.game.writing.rest.types.RoomInfo;
import com.bubba.game.writing.rest.types.RoomStatusResponse;
import com.bubba.game.writing.rest.types.SessionInfo;
import com.bubba.game.writing.rest.types.SessionInfoResponse;
import com.bubba.game.writing.rest.types.UserPrefs;

public class RoomManager {
  private Logger log = LogManager.getLogManager().getLogger(RoomManager.class.getSimpleName());
  private static RoomManager instance;
  
  static String DEFAULT_ROOM = "Brandonses";
  
  private Map<String,WritingRoom> roomMap;
  
  private RoomManager() {
    roomMap = new HashMap<String, WritingRoom>();
  }
  
  public static RoomManager getInstance() {
    if(instance == null) {
      instance = new RoomManager();
    }
    return instance;
  }
  
  public SessionInfoResponse lookupRoomSession(String sessionId) {
    SessionInfoResponse response = null;
    
    synchronized(roomMap) {
      for(WritingRoom room : roomMap.values()) {
        SessionInfo info = room.lookupSession(sessionId);
        
        if(info != null) {
          response = SessionInfoResponse.createSuccess(info);
        }
      }
    }
    
    if(response == null) {
      response = SessionInfoResponse.createFailure("session not found.");
    }
    
    return response;
  }
  
  public SessionInfoResponse joinOrCreateRoom(SessionInfo newInfo) {
    if(newInfo == null) {
      return SessionInfoResponse.createFailure("Session information missing from request");
    }
    else {
      String sessionId = newInfo.getSessionId();
      String roomName = newInfo.getRoomName();
      String authorName = newInfo.getName();
      boolean noSession = sessionId == null || sessionId.isEmpty();
      boolean noRoomName = roomName == null || roomName.isEmpty();
      boolean noAuthor = authorName == null || authorName.isEmpty();
      SessionInfo curInfo = null;
      
      if(noSession && noRoomName) {
        return SessionInfoResponse.createFailure("Missing session ID and name!");
      }
      
      if(noSession && noAuthor) {
        return SessionInfoResponse.createFailure("No author name or session!");
      }
      
      if(noRoomName) {
        WritingRoom targetRoom = null;
        
        synchronized(roomMap) {
          for(WritingRoom searchRoom : roomMap.values()) {
            if(searchRoom.containsSession(sessionId)) {
              targetRoom = searchRoom;
              break;
            }
          }
        }
        
        if(targetRoom == null) {
          return SessionInfoResponse.createFailure("Session ID does not exist");
        }
        curInfo = targetRoom.updateOrCreateSession(newInfo);
      }
      else {
        curInfo = getOrCreateRoom(newInfo.getRoomName())
            .updateOrCreateSession(newInfo);
      }
      
      return curInfo == null ? SessionInfoResponse.createFailure("Unexpected error - should have info here")
          : SessionInfoResponse.createSuccess(curInfo);
    }
  }
  
  private WritingRoom getOrCreateRoom(String name) {
    WritingRoom room = null;
    
    if(name != null && !name.isEmpty()) {
      synchronized(roomMap) {
        room = roomMap.get(name);
      
        if(room == null) {
          log.log(Level.INFO, "Creating room [" + name + "]");
          room = new WritingRoom(name);
          roomMap.put(name, room);
        }
      }
    }
    
    return room;
  }
    
  public List<RoomInfo> getRoomInfoList() {
    List<RoomInfo> list = new ArrayList<RoomInfo>();
    
    synchronized(roomMap) {
      roomMap.values().forEach(room -> {
        list.add(room.getRoomInfo());
      });
    }
    
    return list;
  }
  
  public BasicResponse handleUpdateFragmentLimit(String sessionId, int limit) {
    WritingRoom room = getSessionRoom(sessionId);
      
    if(room == null) {
      return BasicResponse.createFailure("Session not found");
    }
    return room.handleUpdateFragmentLimit(sessionId, limit);
  }
  
  public BasicResponse selectFinishedStory(String sessionId, String authorId) {
    WritingRoom room = getSessionRoom(sessionId);
    
    if(room == null) {
      return BasicResponse.createFailure("Session not found");
    }
    return room.selectFinishedStory(sessionId, authorId);
  }
  
  public FinishedStoryResponse getFinishedStories(String sessionId) {
    WritingRoom room = getSessionRoom(sessionId);
    
    if(room == null) {
      return FinishedStoryResponse.createFailure("Session not found");
    }
    else {
      return room.getFinishedStory(sessionId);
    }
  }
  
  public EpisodeArchiveResponse getEpisodeArchive(String sessionId) {
    WritingRoom room = getSessionRoom(sessionId);
    
    if(room == null) {
      return EpisodeArchiveResponse.createFailure("Session not found");
    }
    
    return room.getEpisodeArchive();
  }
  
  public RoomStatusResponse handleStatusRequest(String sessionId) {
    return handleRequest(sessionId, null);
  }
  
  public RoomStatusResponse handleFragmentUpdate(FragmentUpdate update) {
    return handleRequest(update.getSessionId(), update);
  }
  
  private RoomStatusResponse handleRequest(String sessionId, FragmentUpdate update) {
    WritingRoom room = getSessionRoom(sessionId);
    
    if(room == null) {
      return RoomStatusResponse.createFailure("Session not found");
    }
    if(update != null) {
      return room.applyUpdate(update); 
    }
    return room.getRoomStatus(sessionId);
  }
  
  private WritingRoom getSessionRoom(String sessionId) {
    synchronized(roomMap) {
      for(WritingRoom room : roomMap.values()) {
        if(room.containsSession(sessionId)) {
          return room;
        }
      }
    }
    
    return null;
  }
  
  public BasicResponse runCommand(FragmentCommand command) {
    if(command == null) {
      return BasicResponse.createFailure("Command missing");
    }
    
    log.log(Level.INFO, "Run command " + command.getCommand() + " for session " + command.getSessionId());
    WritingRoom room = getSessionRoom(command.getSessionId());
    
    if(room == null) {
      return BasicResponse.createFailure("Room not found for session");
    }
    
    return room.runCommand(command);
  }
  
  public BasicResponse updatePrefs(UserPrefs prefs) {
    if(prefs == null) {
      return BasicResponse.createFailure("User prefs missing");
    }
    
    WritingRoom room = getSessionRoom(prefs.getSessionId());
    if(room == null) {
      return BasicResponse.createFailure("Room not found for session");
    }
    
    return room.updatePrefs(prefs);
  }
  
  public String startNewEpisode(String sessionId) {
    return manageEpisode(sessionId, true);
  }
  
  public String cancelNewEpisode(String sessionId) {
    return manageEpisode(sessionId, false);
  }
  
  private String manageEpisode(String sessionId, boolean start) {
    WritingRoom room = getSessionRoom(sessionId);
    String resp;
    
    if(room == null) {
      resp = "Session not found";
    }
    else {
      resp = start ? room.startNewEpisode() : room.cancelNewEpisode();
    }
    
    return resp;
  }
  
  public void pollStatus() {
    synchronized(roomMap) {
      roomMap.values().forEach(room -> room.pollStatus());
    }
  }
}
