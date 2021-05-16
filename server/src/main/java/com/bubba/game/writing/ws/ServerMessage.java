package com.bubba.game.writing.ws;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class ServerMessage<T> {
  private T value;
  
  public T getValue() {
    return value;
  }
  
  public void setValue(T value) {
    this.value = value;
  }
}
