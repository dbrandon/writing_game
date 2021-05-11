package com.bubba.game.writing;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.bubba.game.writing.model.RoomManager;
import com.bubba.game.writing.model.SessionManager;

@WebListener
public class WritingGameServer implements ServletContextListener {
  private Logger log = LogManager.getLogManager().getLogger(WritingGameServer.class.getSimpleName());
  
  private boolean keepRunning;
  private boolean pollerRunning;

  @Override
  public void contextDestroyed(ServletContextEvent arg0) {
    synchronized(this) {
      keepRunning = false;
      this.notifyAll();
      while(pollerRunning) {
        try {
          log.log(Level.INFO, "Waiting for poller thread to finish...");
          this.wait(500);
        }
        catch(Exception ex) {}
      }
    }
    log.log(Level.INFO, "Poller finished and ready to exit.");
  }

  @Override
  public void contextInitialized(ServletContextEvent arg0) {
    log.log(Level.INFO, "Creating session manager...");
    SessionManager.getInstance();
    log.log(Level.INFO, "Creating room manager...");
    RoomManager.getInstance();
    log.log(Level.INFO, "\n\n==========> Initialized\n\n");
    
    keepRunning = true;
    new Thread(() -> runPoller()).start();
  }
  
  private void runPoller() {
    pollerRunning = true;
    
    while(keepRunning) {
      synchronized(this) {
        try { this.wait(1000); } catch(Exception ex) {}
      }
      
      if(keepRunning) {
        SessionManager.getInstance().pollStatus();
        RoomManager.getInstance().pollStatus();
      }
    }
    
    synchronized(this) {
      pollerRunning = false;
      this.notifyAll();
    }
  }
}
