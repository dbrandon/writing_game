package com.bubba.game.writing.ws;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS,  include=JsonTypeInfo.As.WRAPPER_OBJECT, property="@class")
public abstract class ClientMessage {

}
