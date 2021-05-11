package com.bubba.game.writing.rest.types;

public class SessionInfoResponse {
  private boolean success;
  private SessionInfo info;
  private String message;
  
  public static SessionInfoResponse createSuccess(SessionInfo info) {
    SessionInfoResponse response = new SessionInfoResponse();
    response.setInfo(info);
    response.setMessage("OK");
    response.setSuccess(true);
    return response;
  }
  
  public static SessionInfoResponse createFailure(String message) {
    SessionInfoResponse response = new SessionInfoResponse();
    response.setMessage(message);
    response.setSuccess(false);
    return response;
  }
  
  public SessionInfo getInfo() {
    return info;
  }
  
  public void setInfo(SessionInfo info) {
    this.info = info;
  }
  
  public String getMessage() {
    return message;
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
  
  public boolean isSuccess() {
    return success;
  }
  
  public void setSuccess(boolean success) {
    this.success = success;
  }
}
