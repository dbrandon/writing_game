package com.bubba.game.writing;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.bubba.game.writing.model.SessionManager;
import com.bubba.game.writing.rest.types.SessionInfo;

@Path("Session")
public class SessionService {
  @Path("create")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public SessionInfo createSession() {
    return SessionManager.getInstance().createSession();
  }
  
  @Path("get/{sessionId}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public SessionInfo get(@PathParam("sessionId") String sessionId) {
    return SessionManager.getInstance().getSession(sessionId);
  }
  
  @Path("set")
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public SessionInfo set(SessionInfo info) {
    return SessionManager.getInstance().updateSession(info);
  }
}
