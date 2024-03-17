package com.mark.web.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController {
   private static final Logger log=LogManager.getLogger(); 
   
   @GetMapping("/home")
   public String sendHomePage(){
    return "homePage";
   }
   
   @GetMapping("/error")
   public String error(){
      log.info("HI");
      return "errorPage";
   }
   
   @GetMapping("/button-click")
   public void gotRequest(){
      System.out.println("done.");
   }
}
