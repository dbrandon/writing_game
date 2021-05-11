package com.bubba.game.writing.rest.types;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EpisodeArchive {
  private List<FinishedStory> storyList;
  private Date finishDate;
  
  public Date getFinishDate() {
    return finishDate;
  }
  
  public void setFinishDate(Date finishDate) {
    this.finishDate = finishDate;
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
