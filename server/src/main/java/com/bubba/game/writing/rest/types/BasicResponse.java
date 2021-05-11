package com.bubba.game.writing.rest.types;

public class BasicResponse {
  private boolean success;
  private String message;
  
  public static BasicResponse createSuccess() {
    BasicResponse response = new BasicResponse();
    
    response.setMessage("OK");
    response.setSuccess(true);
    
    return response;
  }
  
  public static BasicResponse createFailure(String message) {
    BasicResponse response = new BasicResponse();
    
    response.setMessage(message);
    response.setSuccess(false);
    
    return response;
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
