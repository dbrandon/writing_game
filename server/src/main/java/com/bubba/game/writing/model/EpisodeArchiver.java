package com.bubba.game.writing.model;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bubba.game.writing.rest.types.EpisodeArchive;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EpisodeArchiver {
  static Logger logger = LogManager.getLogger("MainLogger");
  
  private static String getArchivePath(String roomName) {
    String base = System.getProperty("jboss.server.data.dir");
    String path = null;
    
    if(base != null) {
      path = base + "/writinggame/" + roomName + "/";
    }
    
    return path;
  }
  
  public static void archiveEpisode(String roomName, EpisodeArchive archive) {
    String path = getArchivePath(roomName);
    
    if(path == null) {
      logger.warn("Failed to determine archive path for episodes (room=" + roomName + ")");
    }
    else {
      File dir = new File(path);
      ObjectMapper mapper = new ObjectMapper();
      
      dir.mkdirs();
      try(OutputStream os = new FileOutputStream(new File(dir, archive.getEpisodeId() + ".json"))) {
        BufferedOutputStream bos = new BufferedOutputStream(os);
        
        mapper.writeValue(bos, archive);
      }
      catch(Exception ex) {
        logger.error("Failed to record episode archive", ex);
      }
    }
  }
}
