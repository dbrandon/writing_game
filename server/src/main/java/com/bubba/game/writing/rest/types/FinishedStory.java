package com.bubba.game.writing.rest.types;

import java.util.ArrayList;
import java.util.List;

public class FinishedStory {
  private String author;
  private FontFamily font;
  private List<FinishedFragment> fragmentList;
  
  public String getAuthor() {
    return author;
  }
  
  public void setAuthor(String author) {
    this.author = author;
  }
  
  public FontFamily getFont() {
    return font;
  }
  
  public void setFont(FontFamily font) {
    this.font = font;
  }
  
  public List<FinishedFragment> getFragmentList() {
    if(fragmentList == null) {
      fragmentList = new ArrayList<FinishedFragment>();
    }
    return fragmentList;
  }
  
  public void setFragmentList(List<FinishedFragment> fragmentList) {
    this.fragmentList = fragmentList;
  }
}
