package com.mark.web.controllers;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;

import com.mark.web.models.Image;
import com.mark.web.models.User;
import com.mark.web.services.serviceImplementation.PasswordCodec;
import com.mark.web.services.serviceImplementation.TokenServiceImplementation;
import com.mark.web.services.serviceImplementation.UserServiceImplementation;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin
@Controller
@RequestMapping("/users")
public class UserController {

    private static final Logger log=LogManager.getLogger(); 
    private static UserServiceImplementation userService=new UserServiceImplementation();
    private static final PasswordCodec codec=new PasswordCodec();
    private static TokenServiceImplementation tokenService=new TokenServiceImplementation();    
    private File recievedFile;
      
    
    @GetMapping("/getFile")
    public File sendFile(){
        return recievedFile;
    }
  

    @GetMapping("/uploadFile")
    public String sendUploadFile(Model model){
        
        model.addAttribute(new Image());
        return "uploadFile";
    }

    @PostMapping("/uploadImage")
    public String getFile(@ModelAttribute Image image ,Model model,@RequestParam("image")MultipartFile file,HttpServletResponse response){
        
        if(codec.stringEmpty(image.getImageDescription())){
            model.addAttribute("error","Missing fields");
            response.setStatus(400); 
            return "uploadFile";
        } 
        String uploadDirectory=System.getProperty("user.dir")+"/uploads";
        Path fileNameAndPath=Paths.get(uploadDirectory,file.getOriginalFilename());

        StringBuilder fileName=new StringBuilder();
        fileName.append(file.getOriginalFilename());
        
        int user_id=2;
        String image_desc=image.getImageDescription(); 
        //

        try{
            byte[] fileByte=file.getBytes();
            String encbase64String=Base64.encodeBase64String(fileByte);
            userService.addImage(user_id,image_desc,encbase64String);
            Files.write(fileNameAndPath,fileByte);
        }catch(IOException e){
            e.printStackTrace();
            model.addAttribute("error","Server error");
            response.setStatus(500);
            return "uploadFile";
        }catch(SQLException e){
            e.printStackTrace();
            model.addAttribute("error","Server error");
            response.setStatus(500);
            return "uploadFile";
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
        if(codec.stringEmpty(username) || codec.stringEmpty(password)){
            model.addAttribute("error","missing fields");
            response.setStatus(400);
            return "loginUser";
        }        
        try{
            if(userService.loginUser(username,password)){
                int userid=userService.getUserID(username);
                if(userid==-1){
                    model.addAttribute("error","server error.");
                    response.setStatus(500);
                    return "loginUser";
                }
                String token=tokenService.createToken(userid);
                response.setHeader("token",token);
                response.setStatus(200);
                return "homePage";
            }else{
                model.addAttribute("error","doesnt match.");
                response.setStatus(401);
                return "loginUser";
            }
        
        }catch(Exception e){
            // e.printStackTrace();
            log.info(e);
            model.addAttribute("error","Server Error");
            response.setStatus(500);
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
            
            int user_id=userService.registerUser(user.getUsername(),user.getPassword(),user.getEmail());
            String token=tokenService.createToken(user_id);
            response.setHeader("token", token); 
            response.setStatus(200);
            return "homePage";

        }catch(Exception e){
            log.info(e);
            response.setStatus(500);
            model.addAttribute("error","Server Error");
            // e.printStackTrace();
            return "registerUser";
        }
    }
    @GetMapping("/register2")
    public String sendRegisterPage2(){
        return "registerUser2";
    }
    
    @PostMapping(path="/register2",consumes={MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public String registerUser2(@RequestParam MultiValueMap<String,String> form){
        String username=form.getFirst("username");
        String password=form.getFirst("password");
        String email=form.getFirst("email");
         
        return "homePage";
    }
    
    @GetMapping("/sendFriendRequest")
    public String sendFriendRequestHTML(){
        return "sendFriendRequest";
    }

    // @PostMapping("/sendFriendRequest")
    // public String sendFriendRequest(@RequestParam("username")String username){
    //     //validate user request
    //     //get username send request to the user 
    //     //check for pending and accepted request
    // }
    
    // @GetMapping("/viewFriendRequets")
    // public String viewFriendRequests(){

    // }

    // @RequestMapping(value="find_user",method=RequestMethod.POST)
    // public void findUser(@RequestParam("username")String username){
    //    boolean finduser=userService.checkUsername(username);

    // }
    
}
