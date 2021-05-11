package com.bubba.game.writing.rest.types;

public class FinishedStoryResponse {
  private boolean success;
  private String message;
  
  private FinishedStory story;
  
  public static FinishedStoryResponse createFailure(String message) {
    FinishedStoryResponse response = new FinishedStoryResponse();
    
    response.setMessage(message);
    response.setSuccess(false);
    
    return response;
  }
  
  public static FinishedStoryResponse createSuccess(FinishedStory story) {
    FinishedStoryResponse response = new FinishedStoryResponse();
    
    response.setMessage("OK");
    response.setStory(story);
    response.setSuccess(true);
    
    return response;
  }
  
  public static FinishedStoryResponse createWaiting() {
    FinishedStoryResponse response = new FinishedStoryResponse();
    
    response.setMessage("Waiting for selection");
    response.setSuccess(true);
    
    return response;
  }
  
  public boolean isSuccess() {
    return success;
  }
  
  public void setSuccess(boolean success) {
    this.success = success;
  }
  
  public String getMessage() {
    return message;
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
  
  public FinishedStory getStory() {
    return story;
  }
  
  public void setStory(FinishedStory story) {
    this.story = story;
  }
}
