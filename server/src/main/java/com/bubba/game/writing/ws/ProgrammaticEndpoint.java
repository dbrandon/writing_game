package com.bubba.game.writing.ws;

import java.util.Date;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bubba.generated.proto.TestOuterClass;

@MyWsAnnotation
public class ProgrammaticEndpoint extends Endpoint {
  private Logger log = LogManager.getLogger("Server");
  
  @Override
  public void onOpen(Session session, EndpointConfig config) {
    // TODO Auto-generated method stub
    log.info("Opened session!!");
    session.addMessageHandler(new MessageHandler.Whole<byte[]>() {

      @Override
      public void onMessage(byte[] message) {
        // TODO Auto-generated method stub
        StringBuffer sb = new StringBuffer();
        for(byte b : message) {
          sb.append(String.format("%02X ", (b < 0 ? b + 256 : b)));
        }
        log.info("<byte[]> Raw message: " + sb.toString());
        
        try {
          TestOuterClass.Test test = TestOuterClass.Test.parseFrom(message);
          log.info("Parsed protocol buffer object: " + test.getNum() + ": [" + test.getPayload() + "]");
          if(test.hasTimestamp()) {
            log.info("  Timestamp: " + test.getTimestamp());
            log.info("   Or roughly " + new Date((long)test.getTimestamp()));
          }
          else {
            log.info("  No timestamp included.");
          }
        }
        catch(Exception ex) {
          log.warn("<byte[]> Message was not a protocol buffer object: " + ex.getMessage());
          log.info("<byte[]> Message Text: [" + new String(message) + "]");
        }
      }
    });
    
    session.addMessageHandler(new MessageHandler.Whole<String>() {
      @Override
      public void onMessage(String message) {
        // TODO Auto-generated method stub
       log.info("<String> Got message: [" + message + "]"); 
      }
    });
  }
  
  @Override
  public void onClose(Session session, CloseReason closeReason) {
    // TODO Auto-generated method stub
    super.onClose(session, closeReason);
    log.info("Closed session: " + closeReason.getReasonPhrase());
  }
  
  @Override
  public void onError(Session session, Throwable thr) {
    // TODO Auto-generated method stub
    super.onError(session, thr);
  }
}
