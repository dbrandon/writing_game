package com.bubba.game.writing.ws;

import java.io.IOException;

import javax.enterprise.event.Observes;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bubba.game.writing.rest.types.BasicResponse;

@ServerEndpoint(value="/hello", decoders={JsonDecoder.class}, encoders={JsonEncoder.class} )
public class HelloEndpoint {
  private Logger log = LogManager.getLogger("MainLogger");
  
  private Session session;
  
  @OnOpen
  public void open(Session session, EndpointConfig conf) throws IOException {
    this.session = session;
    log.info("Got open for websocket client!");
    sendResponse(BasicResponse.createFailure("Simulating something bad happening"));
  }
  
  private void sendResponse(@Observes Object obj) {
    log.info("sending a response...");
    session.getAsyncRemote().sendObject(obj);
    log.info("response was sent!");
  }
  
  @OnMessage
  public void message(ClientMessage message, Session session) {
    log.info("got a client message!!");
    log.info("client type is " + message.getClass().getSimpleName());
    if(message instanceof SimpleRequest) {
      SimpleRequest req = (SimpleRequest)message;
      
      log.info("  Request=[" + req.getText() + "]");
      log.info("  Number=" + req.getNumber());
    }
  }
//  
  @OnClose
  public void onClose(Session session, CloseReason closeReason) {
    log.info("Socket closed: " + closeReason);
//    this.session.close();
  }
  
}
