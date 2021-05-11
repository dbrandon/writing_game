package com.bubba.game.writing.rest.types;

public class WriterStatus {
  private String author;
  private String publicId;
  private boolean typing;
  private boolean finished;
  private boolean expired;
  private boolean reportedAfk;
  private boolean myStatus;
  private boolean roomLeader;
  private FontFamily font;
  
  public String getAuthor() {
    return author;
  }
  
  public void setAuthor(String author) {
    this.author = author;
  }
  
  public boolean isExpired() {
    return expired;
  }
  
  public void setExpired(boolean expired) {
    this.expired = expired;
  }
  
  public boolean isFinished() {
    return finished;
  }
  
  public void setFinished(boolean finished) {
    this.finished = finished;
  }
  
  public FontFamily getFont() {
    return font;
  }
  
  public void setFont(FontFamily font) {
    this.font = font;
  }
  
  public boolean isMyStatus() {
    return myStatus;
  }
  
  public void setMyStatus(boolean myStatus) {
    this.myStatus = myStatus;
  }
  
  public String getPublicId() {
    return publicId;
  }
  
  public void setPublicId(String publicId) {
    this.publicId = publicId;
  }
  
  public boolean isReportedAfk() {
    return reportedAfk;
  }
  
  public void setReportedAfk(boolean reportedAfk) {
    this.reportedAfk = reportedAfk;
  }
  
  public boolean isRoomLeader() {
    return roomLeader;
  }
  
  public void setRoomLeader(boolean roomLeader) {
    this.roomLeader = roomLeader;
  }
  
  public boolean isTyping() {
    return typing;
  }
  
  public void setTyping(boolean typing) {
    this.typing = typing;
  }
}
