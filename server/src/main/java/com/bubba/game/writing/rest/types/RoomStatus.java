package com.bubba.game.writing.rest.types;

import java.util.ArrayList;
import java.util.List;

public class RoomStatus {
  private String roomName;
  private List<WriterStatus> writerStatusList;
  
  private boolean isEpisodeDone;
  
  private String prevVisibleFragment;
  private String prevAuthorPublicId;
  private String workingHiddenFragment;
  private String workingVisibleFragment;
  private boolean isFragmentDone;
  
  private int roundNumber;
  private int maxRounds;
  private boolean roundTimerRunning;
  private long roundTimeRemaining;
  
  private boolean episodeTimerRunning;
  private long episodeTimeRemaining;
  
  private String selectedStoryAuthorId;
  
  public long getEpisodeTimeRemaining() {
    return episodeTimeRemaining;
  }
  public void setEpisodeTimeRemaining(long episodeTimeRemaining) {
    this.episodeTimeRemaining = episodeTimeRemaining;
  }
  
  public boolean isEpisodeTimerRunning() {
    return episodeTimerRunning;
  }
  
  public void setEpisodeTimerRunning(boolean episodeTimerRunning) {
    this.episodeTimerRunning = episodeTimerRunning;
  }
  
  public String getRoomName() {
    return roomName;
  }
  
  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }
  
  public boolean isEpisodeDone() {
    return isEpisodeDone;
  }
  
  public void setEpisodeDone(boolean isEpisodeDone) {
    this.isEpisodeDone = isEpisodeDone;
  }
  
  public boolean isFragmentDone() {
    return isFragmentDone;
  }
  public void setFragmentDone(boolean isFragmentDone) {
    this.isFragmentDone = isFragmentDone;
  }
  
  public String getPrevAuthorPublicId() {
    return prevAuthorPublicId;
  }
  
  public void setPrevAuthorPublicId(String prevAuthorPublicId) {
    this.prevAuthorPublicId = prevAuthorPublicId;
  }
  
  public String getPrevVisibleFragment() {
    return prevVisibleFragment;
  }
  
  public void setPrevVisibleFragment(String prevVisibleFragment) {
    this.prevVisibleFragment = prevVisibleFragment;
  }
  
  public String getWorkingHiddenFragment() {
    return workingHiddenFragment;
  }
  
  public void setWorkingHiddenFragment(String workingHiddenFragment) {
    this.workingHiddenFragment = workingHiddenFragment;
  }
  
  public String getWorkingVisibleFragment() {
    return workingVisibleFragment;
  }
  
  public void setWorkingVisibleFragment(String workingVisibleFragment) {
    this.workingVisibleFragment = workingVisibleFragment;
  }
  
  
  public int getMaxRounds() {
    return maxRounds;
  }
  
  public void setMaxRounds(int maxRounds) {
    this.maxRounds = maxRounds;
  }
  
  public int getRoundNumber() {
    return roundNumber;
  }
  
  public void setRoundNumber(int roundNumber) {
    this.roundNumber = roundNumber;
  }
  
  public boolean isRoundTimerRunning() {
    return roundTimerRunning;
  }
  
  public void setRoundTimerRunning(boolean roundTimerRunning) {
    this.roundTimerRunning = roundTimerRunning;
  }
  
  public long getRoundTimeRemaining() {
    return roundTimeRemaining;
  }
  
  public void setRoundTimeRemaining(long roundTimeRemaining) {
    this.roundTimeRemaining = roundTimeRemaining;
  }
  
  public String getSelectedStoryAuthorId() {
    return selectedStoryAuthorId;
  }
  
  public void setSelectedStoryAuthorId(String selectedStoryAuthorId) {
    this.selectedStoryAuthorId = selectedStoryAuthorId;
  }
  
  public List<WriterStatus> getWriterStatusList() {
    if(writerStatusList == null) {
      writerStatusList = new ArrayList<WriterStatus>();
    }
    return writerStatusList;
  }
}
