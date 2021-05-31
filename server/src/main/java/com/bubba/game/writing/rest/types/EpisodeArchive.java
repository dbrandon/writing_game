package com.bubba.game.writing.rest.types;

import java.util.ArrayList;
import java.util.List;

public class EpisodeArchive {
  private String episodeId;
  private List<FinishedStory> storyList;
  private long finishTime;
  
  public String getEpisodeId() {
    return episodeId;
  }
  
  public void setEpisodeId(String episodeId) {
    this.episodeId = episodeId;
  }
  
  public long getFinishTime() {
    return finishTime;
  }
  
  public void setFinishTime(long finishTime) {
    this.finishTime = finishTime;
  }
  
  public List<FinishedStory> getStoryList() {
    if(storyList == null) {
      storyList = new ArrayList<FinishedStory>();
    }
    return storyList;
  }
  
  public void setStoryList(List<FinishedStory> storyList) {
    this.storyList = storyList;
  }
}
