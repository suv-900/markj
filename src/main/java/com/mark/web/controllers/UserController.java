package com.mark.web.controllers;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;


import com.mark.web.models.User;
import com.mark.web.services.serviceImplementation.UserServiceImplementation;
import com.mark.web.services.serviceImplementation.UtillityServiceImplementation;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/users")
public class UserController {

    private static final Logger log=LogManager.getLogger(); 
    private static UserServiceImplementation userService=new UserServiceImplementation();
    private static final UtillityServiceImplementation utils=new UtillityServiceImplementation();
    
    private File recievedFile;
      
    
    @GetMapping("/getFile")
    public File sendFile(){
        return recievedFile;
    }
  

    @GetMapping("/uploadFile")
    public String sendUploadFile(){
        StringBuilder sb=new StringBuilder();
        sb.append("Hi");
        System.out.println("StringBuilder: "+sb); 
        return "uploadFile";
    }

    @PostMapping("/uploadImage")
    public String getFile(Model model,@RequestParam("image")MultipartFile file){
        
        String uploadDirectory=System.getProperty("user.dir")+"/uploads";
        Path fileNameAndPath=Paths.get(uploadDirectory,file.getOriginalFilename());

        StringBuilder fileName=new StringBuilder();
        fileName.append(file.getOriginalFilename());
        
        //

        try{
            byte[] fileByte=file.getBytes();
            
            // String hexString=utils.byteToHex(fileByte);
            // System.out.println("hex string: "+hexString+"\nstring length: "+hexString.length());
            
            String encbase64String=Base64.encodeBase64String(fileByte);

            System.out.println("string length: "+encbase64String.length());
            // byte[] decodedBytes=Base64.decodeBase64(encbase64String);

            Files.write(fileNameAndPath,fileByte);


        }catch(IOException e){
            e.printStackTrace();
        } 

        model.addAttribute("msg","image added "+fileName.toString());
        return "uploadFile";
    }
    
  
    @GetMapping("/login")
    public String sendLoginPage(Model model){
        model.addAttribute(new User());
        return "loginUser";
    }
    
    
    @PostMapping("/login")
    public String validateUserLogin(@ModelAttribute User user, HttpServletResponse response,Model model){
        String username=user.getUsername();
        String password=user.getPassword();
        if(utils.stringEmpty(username)||utils.stringEmpty(password)){
            model.addAttribute("error","missing fields");
            response.setStatus(400);
            return "loginUser";
        }        
        
        try{
            if(userService.loginUser(username,password)){
                return "homePage";
            }else{
                model.addAttribute("error","doesnt match.");
                response.setStatus(401);
                return "loginUser";
            }
        
        }catch(SQLException e){
            e.printStackTrace();
            model.addAttribute("error","Server Error");
            response.setStatus(400);
            return "loginUser";
        }
         

        // boolean loginSuccess=userServiceImpl.loginUser(username,password);
    }
    
    
    @GetMapping("/register")
    public String sendRegisterPage(Model model){
        model.addAttribute(new User());
        return "registerUser";
    }
    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user,HttpServletResponse response,Model model){
        try{
            if(user.checkNULL()){
                response.setStatus(400);
                model.addAttribute("error","missing fields");
                return "registerUser";
            }

            if(userService.checkUsername(user.getUsername())){
                response.setStatus(400);
                model.addAttribute("error","username exists");   
                return "registerUser";
            }
            
            String hashedPass=utils.generateHashedPassword(user.getPassword());        
            userService.registerUser(user.getUsername(),hashedPass,user.getEmail());
            response.setStatus(200);
            return "homePage";

        }catch(SQLException e){
            response.setStatus(500);
            model.addAttribute("error","Server Error");
            e.printStackTrace();
            return "registerUser";
        } 
    }
    
    
    @RequestMapping(value="checkUsername",method=RequestMethod.POST)
    public void checkIfUsernameExists(@RequestBody String username){
        // boolean usernameExists=userServiceImpl.checkUsername(username);
        // return usernameExists; 
    }
    
}
