package com.bubba.game.writing.rest.types;

public class RoomStatusResponse {
  private boolean success;
  private String message;
  private RoomStatus status;
  
  public static RoomStatusResponse createFailure(String message) {
    RoomStatusResponse response = new RoomStatusResponse();
    
    response.setMessage(message);
    response.setStatus(null);
    response.setSuccess(false);
    
    return response;
  }
  
  public static RoomStatusResponse createSuccess(RoomStatus status) {
    RoomStatusResponse response = new RoomStatusResponse();
    
    response.setMessage("OK");
    response.setStatus(status);
    response.setSuccess(true);
    
    return response;
  }
  
  public String getMessage() {
    return message;
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
  
  public RoomStatus getStatus() {
    return status;
  }
  
  public void setStatus(RoomStatus status) {
    this.status = status;
  }
  
  public boolean isSuccess() {
    return success;
  }
  
  public void setSuccess(boolean success) {
    this.success = success;
  }
}
