package com.bubba.game.writing.ws;

public class SimpleRequest extends ClientMessage {
  private String text;
  private int number;
  
  public int getNumber() {
    return number;
  }
  
  public void setNumber(int number) {
    this.number = number;
  }
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
}
