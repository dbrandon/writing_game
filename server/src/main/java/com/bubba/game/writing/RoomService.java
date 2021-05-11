package com.bubba.game.writing;

import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.bubba.game.writing.model.RoomManager;
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

@Path("/Rooms")
public class RoomService {
  Logger log = LogManager.getLogManager().getLogger(RoomService.class.getSimpleName());
  
  @GET
  @Path("/list")
  @Produces(MediaType.APPLICATION_JSON)
  public List<RoomInfo> getRoomList() {
    return RoomManager.getInstance().getRoomInfoList();
  }
  
  @GET
  @Path("lookup/{sessionId}")
  @Produces(MediaType.APPLICATION_JSON)
  public SessionInfoResponse lookupRoomSession(@PathParam("sessionId") String sessionId) {
    return RoomManager.getInstance().lookupRoomSession(sessionId);
  }
  
  @PUT
  @Path("joinOrCreate")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public SessionInfoResponse joinOrCreateRoom(SessionInfo info) {
    return RoomManager.getInstance().joinOrCreateRoom(info);
  }
  
  @PUT
  @Path("updateSession")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public SessionInfoResponse updateSession(SessionInfo info) {
    return RoomManager.getInstance().joinOrCreateRoom(info);
  }
  
  @GET
  @Path("status/{sessionId}")
  @Produces(MediaType.APPLICATION_JSON)
  public RoomStatusResponse statusRequest(@PathParam("sessionId") String sessionId) {
    return RoomManager.getInstance().handleStatusRequest(sessionId);
  }
  
  @GET
  @Path("fragmentLimit/{sessionId}/{limit}")
  @Produces(MediaType.APPLICATION_JSON)
  public BasicResponse updateFragmentLimit(@PathParam("sessionId")String sessionId, @PathParam("limit") int limit) {
    return RoomManager.getInstance().handleUpdateFragmentLimit(sessionId, limit);
  }
  
  @PUT
  @Path("fragment")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public RoomStatusResponse fragmentUpdate(FragmentUpdate update) {
    return RoomManager.getInstance().handleFragmentUpdate(update);
  }
  
  @GET
  @Path("selectFinishedStory/{sessionId}/{authorId}")
  @Produces(MediaType.APPLICATION_JSON)
  public BasicResponse selectFinishedStory(@PathParam("sessionId") String sessionId, @PathParam("authorId") String authorId) {
    return RoomManager.getInstance().selectFinishedStory(sessionId, authorId);
  }
  
  @GET
  @Path("finishedStory/{sessionId}")
  @Produces(MediaType.APPLICATION_JSON)
  public FinishedStoryResponse getFinishedStory(@PathParam("sessionId") String sessionId) {
    return RoomManager.getInstance().getFinishedStories(sessionId);
  }
  
  @PUT
  @Path("command")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public BasicResponse runCommand(FragmentCommand command) {
    return RoomManager.getInstance().runCommand(command);
  }
  
  @PUT
  @Path("updatePrefs")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public BasicResponse updatePrefs(UserPrefs prefs) {
    return RoomManager.getInstance().updatePrefs(prefs);
  }
  
  @GET
  @Path("startNewEpisode/{sessionId}")
  @Produces(MediaType.APPLICATION_JSON)
  public String startNewEpisode(@PathParam("sessionId") String sessionId) {
    return RoomManager.getInstance().startNewEpisode(sessionId);
  }
  
  @GET
  @Path("cancelEpisodeStart/{sessionId}")
  @Produces(MediaType.APPLICATION_JSON)
  public String cancelNewEpisode(@PathParam("sessionId") String sessionId) {
    return RoomManager.getInstance().cancelNewEpisode(sessionId);
  }
  
  @GET
  @Path("episodeArchive/{sessionId}")
  @Produces(MediaType.APPLICATION_JSON)
  public EpisodeArchiveResponse getEspisodeArchive(@PathParam("sessionId") String sessionId) {
    return RoomManager.getInstance().getEpisodeArchive(sessionId);
  }
}
