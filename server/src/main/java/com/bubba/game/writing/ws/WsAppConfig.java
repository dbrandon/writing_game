package com.bubba.game.writing.ws;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class WsAppConfig implements ServerApplicationConfig {
  private Logger log = LogManager.getLogger("Server");
  
  @Inject
  private ServletContext context;
  
  public WsAppConfig() throws MalformedURLException {
    Set<URL> pathSet = new HashSet<URL>();
    log.info("Created a WsAppConfig!");

    log.info("Me = " + WsAppConfig.class.getResource("."));
    String path = WsAppConfig.class.getResource(".").toString();
    int index = path.indexOf("/WEB-INF");
    if(index >= 0) {
      path = path.substring(0, index) + "/WEB-INF/classes";
      log.info("Path will be [" + path + "]");
      pathSet.add(new URL(path));
    }
    
    Reflections reflections = new Reflections(
        new ConfigurationBuilder()
          .setUrls(pathSet) //ClasspathHelper.forPackage("com.bubba.game"))
          .setScanners(new TypeAnnotationsScanner(), new SubTypesScanner())
          .setExecutorService(Executors.newFixedThreadPool(4))
        );
    
    log.info("context: " + (context == null ? "none" : "ok"));
    
    
    log.info("--- Class Loader ---");
    ClasspathHelper.forClassLoader().forEach(url -> log.info("URL: " + url));
    log.info("--- Java Class Path ---");
    ClasspathHelper.forJavaClassPath().forEach(url -> log.info("URL: " + url));
    log.info("-- Manifset ---");
    ClasspathHelper.forManifest().forEach(url -> log.info("URL: " + url));
    
    Set<Class<?>> set = reflections.getTypesAnnotatedWith(MyWsAnnotation.class);
    if(set.isEmpty()) {
      log.info("No classes found.");
    }
    else {
      log.info("Found classes:");
      set.forEach(cls -> log.info("  Class: " + cls.getSimpleName()));
    }
  }
  
  @Override
  public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
    // TODO Auto-generated method stub
    log.info("Request for annotated endpoints!");
    return new HashSet<Class<?>>();
  }
  
  @Override
  public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
    Set<ServerEndpointConfig> set = new HashSet<ServerEndpointConfig>();
    
    log.info("requested endpoint configs!!", new Throwable());
    set.add(ServerEndpointConfig.Builder
        .create(ProgrammaticEndpoint.class, "/pgrm")
        .build());
    
    return set;
  }
}
