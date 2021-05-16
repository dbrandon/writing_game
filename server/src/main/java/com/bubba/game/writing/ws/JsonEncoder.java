package com.bubba.game.writing.ws;

import java.io.IOException;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonEncoder implements Encoder.Text<Object> {
  private ObjectMapper mapper;
  
  @Override
  public void init(EndpointConfig config) {
    mapper = new ObjectMapper();
  }
  
  @Override
  public void destroy() {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public String encode(Object object) throws EncodeException {
    try {
      return mapper.writeValueAsString(object);
    }
    catch(IOException ex) {
      throw new EncodeException(object, "Could not encode", ex);
    }
  }
}
