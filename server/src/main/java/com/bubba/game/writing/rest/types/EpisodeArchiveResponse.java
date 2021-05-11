package com.bubba.game.writing.rest.types;

public class EpisodeArchiveResponse {
  private boolean success;
  private String message;
  private EpisodeArchive archive;
  
  public static EpisodeArchiveResponse createFailure(String message) {
    EpisodeArchiveResponse resp = new EpisodeArchiveResponse();
    
    resp.setMessage(message);
    resp.setSuccess(false);
    
    return resp;
  }
  
  public static EpisodeArchiveResponse createSuccess(EpisodeArchive archive) {
    EpisodeArchiveResponse resp = new EpisodeArchiveResponse();
    
    resp.setArchive(archive);
    resp.setMessage("OK");
    resp.setSuccess(true);
    
    return resp;
  }
  
  public EpisodeArchive getArchive() {
    return archive;
  }
  
  public void setArchive(EpisodeArchive archive) {
    this.archive = archive;
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
