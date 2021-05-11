package com.bubba.game.writing.rest.types;

public class FinishedFragment {
  private String author;
  private String hiddenText;
  private String visibleText;
  private FontFamily font;
  
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
  
  public String getHiddenText() {
    return hiddenText;
  }
  
  public void setHiddenText(String hiddenText) {
    this.hiddenText = hiddenText;
  }
  
  public String getVisibleText() {
    return visibleText;
  }
  
  public void setVisibleText(String visibleText) {
    this.visibleText = visibleText;
  }
}
