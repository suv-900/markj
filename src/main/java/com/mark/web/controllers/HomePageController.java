package com.mark.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController {
   @GetMapping("/home")
   public String sendHomePage(){
    return "homePage";
   }
   
   @GetMapping("/error")
   public String error(){
      return "errorPage";
   }
   
   @GetMapping("/button-click")
   public void gotRequest(){
      System.out.println("done.");
   }
}
