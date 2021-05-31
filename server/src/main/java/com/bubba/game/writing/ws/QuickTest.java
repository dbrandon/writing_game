package com.bubba.game.writing.ws;

import java.io.StringWriter;

import com.bubba.game.writing.rest.types.BasicResponse;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class QuickTest {
  public static void main(String[] args) {
    ObjectMapper mapper = new ObjectMapper();
    SimpleRequest req = new SimpleRequest();
    
    ServerMessage<BasicResponse> resp = new ServerMessage<BasicResponse>();
    resp.setValue(BasicResponse.createFailure("some kind of failure"));
    
    req.setNumber(123);
    req.setText("Some random text here");
    
    try {
      String json = mapper.writeValueAsString(req);
      
      System.out.println("Raw: " + json);
      ClientMessage msg = mapper.readValue(json, ClientMessage.class);
      System.out.println("Deserialized class=" + msg.getClass().getSimpleName());
      
      StringWriter sw = new StringWriter();
      JsonGenerator gen = new JsonFactory().createGenerator(sw);
      gen.writeStartObject();
      gen.writeFieldName("1");
      gen.writeRaw(":");
      gen.flush();
      mapper.writeValue(sw, BasicResponse.createSuccess());
      gen.writeEndObject();
      gen.close();
      System.out.println("Generated: " + sw.toString());

      JsonParser parser = new JsonFactory().createParser(sw.toString());
      while(parser.nextToken() != null) {
        if(parser.getCurrentToken() == JsonToken.FIELD_NAME) {
          System.out.println("Got Msg ID=" + parser.getCurrentName());
          
          parser.nextToken();
          BasicResponse br = mapper.readValue(parser, BasicResponse.class);
          System.out.println("Parsed message: success:" + br.isSuccess() + "; Msg=[" + br.getMessage() + "]");
        }
      }

//      JsonParser parser = new JsonFactory().createParser(json);
//      while(parser.nextToken() != null) {//JsonToken.END_OBJECT) {
//        System.out.println("Token: " + parser.getCurrentToken());
//        if(parser.getCurrentName() != null) {
//          System.out.println("Name = [" + parser.getCurrentName() + "]");
//          if(".SimpleRequest".equals(parser.getCurrentName())) {
//            parser.nextToken();
//            SimpleName2 sr = mapper.readValue(parser, SimpleName2.class);
//            System.out.println("Read thingy: Name=[" + sr.getText() + "] Num=[" + sr.getNumber() + "]");
//          }
//        }
//      }
      
      JsonNode treeNode = mapper.readTree(json);
      System.out.println("tree size = " + treeNode.size());
      treeNode.fieldNames().forEachRemaining(s -> System.out.println("Field name is [" + s + "]"));
      System.out.println("First node = " + treeNode.get(0));
      JsonNode reqNode = treeNode.get(".SimpleRequest");
      System.out.println("reqNode: " + reqNode.toString());
      json = mapper.writeValueAsString(resp);
      System.out.println("Raw resp: " + json);
      ServerMessage<BasicResponse> decoded = mapper.readValue(json, ServerMessage.class);
      System.out.println("Deserialized resp=" + decoded.getClass().getSimpleName());
      System.out.println("  respMessage=[" + decoded.getValue().getMessage() + "]");
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
}
