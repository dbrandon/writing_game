package com.bubba.game.writing.ws;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonDecoder implements Decoder.Text<ClientMessage> {
  private ObjectMapper mapper = new ObjectMapper();
  @Override
  public void init(EndpointConfig config) {
    // TODO Auto-generated method stub
      
  }
  
  @Override
  public ClientMessage decode(String s) throws DecodeException {
    try {
      return mapper.readValue(s, ClientMessage.class);
    }
    catch(Exception ex) {
      throw new DecodeException(s, "Failed to decode", ex);
    }
  }
  
  @Override
  public boolean willDecode(String s) {
    return true;
  }
  
  @Override
  public void destroy() {
    // TODO Auto-generated method stub
    
  }
}
