package com.mark.web.controllers;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;

import com.mark.web.models.Image;
import com.mark.web.models.User;
import com.mark.web.services.serviceImplementation.PasswordCodec;
import com.mark.web.services.serviceImplementation.TokenServiceImplementation;
import com.mark.web.services.serviceImplementation.UserServiceImplementation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin
@Controller
@RequestMapping("/users")
public class UserController {

    private static final Logger log=LogManager.getLogger(); 
    private static UserServiceImplementation userService=new UserServiceImplementation();
    private static TokenServiceImplementation tokenService=new TokenServiceImplementation();    
      
    @GetMapping("/uploadImage")
    public String sendUploadFile(){
        return "uploadImage";
    }

    @PostMapping(path="/uploadImage")
    public String getFile(@RequestBody MultipartFile file,HttpServletResponse response,HttpServletRequest request){
        
        String token=request.getHeader("Token");
        int userid=tokenService.verifyToken(token);
        
        if(userid == -1){
            response.setStatus(401);
            return "homePage";
        }
        
        System.out.println("token "+token); 
        // String formData=form.getFirst("formData");
        // String imageDescription=form.getFirst("imageDescription");
        // System.out.println("FormData: "+formData+"\nimageDesc: "+imageDescription);
        
        String uploadDirectory=System.getProperty("user.dir")+"/uploads";
        Path fileNameAndPath=Paths.get(uploadDirectory,file.getOriginalFilename());

        StringBuilder fileName=new StringBuilder();
        fileName.append(file.getOriginalFilename());
        
        // String image_desc=image.getImageDescription(); 
        //

        try{
            byte[] fileByte=file.getBytes();
            
            String encbase64String=Base64.encodeBase64String(fileByte);
            
            // userService.addImage(userid,image_desc,encbase64String);
            
            Files.write(fileNameAndPath,fileByte);
            return "homePage";
        
        }catch(Exception e){
            e.printStackTrace();
            response.setStatus(500);
            return "homePage";
        }

        // model.addAttribute("msg","image added "+fileName.toString());
        // return "uploadFile";
    }
    
    // @GetMapping("/login2")
    // public String sendLoginPage(Model model){
    //     model.addAttribute(new User());
    //     return "loginUser2";
    // }


    // @PostMapping("/login2")
    // public String validateUserLogin(@ModelAttribute User user, HttpServletResponse response,Model model){
    //     String username=user.getUsername();
    //     String password=user.getPassword();
    //     if(username.isEmpty() || password.isEmpty()){
    //         model.addAttribute("error","missing fields");
    //         response.setStatus(400);
    //         return "loginUser2";
    //     }        
    //     try{
    //         if(userService.loginUser(username,password)){
    //             int userid=userService.getUserID(username);
    //             if(userid==-1){
    //                 model.addAttribute("error","server error.");
    //                 response.setStatus(500);
    //                 return "loginUser2";
    //             }
    //             String token=tokenService.createToken(userid);
    //             response.setHeader("token",token);
    //             response.setStatus(200);
    //             return "homePage";
    //         }else{
    //             model.addAttribute("error","doesnt match.");
    //             response.setStatus(401);
    //             return "loginUser2";
    //         }
        
    //     }catch(Exception e){
    //         // e.printStackTrace();
    //         log.info(e);
    //         model.addAttribute("error","Server Error");
    //         response.setStatus(500);
    //         return "loginUser2";
    //     }
         

    //     // boolean loginSuccess=userServiceImpl.loginUser2(username,password);
    // }

 
    @GetMapping("/login")
    public String loginUser(){
        return "loginUser";
    }
  

    @PostMapping(path="/login",consumes={MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public String loginUser(@RequestParam MultiValueMap<String,String> form,HttpServletResponse response){

        String username=form.getFirst("username");
        String password=form.getFirst("password");

        if( username.isEmpty() || password.isEmpty()){
            response.setStatus(400);
            return "homePage";
        }

        try{

            List<String> list=userService.loginUser(username);
            String s=list.get(1);
            int userid=Integer.parseInt(s);
            
            if(userid == -1){
                response.setStatus(400);
                return "homePage";
            }
            
            String dbPassword=list.get(0);
            boolean isOK=userService.comparePassword(dbPassword,password);
            
            if(!isOK){
                response.setStatus(401);
                return "homePage";
            }

            String token=tokenService.createToken(userid);
            response.setHeader("Token",token);
            response.setStatus(200);
            return "homePage";

        }catch(Exception e){
            log.info(e);
            response.setStatus(500);
            return "homePage";
        }

    }


    @GetMapping("/register")
    public String sendRegisterPage(){
        return "registerUser";
    }
    
    @PostMapping(path="/register",consumes={MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public String registerUser(@RequestParam MultiValueMap<String,String> form,HttpServletResponse response){
        String username=form.getFirst("username");
        String password=form.getFirst("password");
        String email=form.getFirst("email");
        
        if(username.isEmpty() || password.isEmpty() || email.isEmpty()){
            response.setStatus(400);
            return "homePage";
        }
         
        try{
            if(userService.checkUsername(username)){
                response.setStatus(400);
                return "homePage";
            }        

            int userid=userService.registerUser(username,password,email);
            String token=tokenService.createToken(userid);
            response.setHeader("Token",token);
            response.setStatus(200);
            return "homePage";

        }catch(Exception e){
            log.info(e);
            response.setStatus(500);
            return "homePage";
        }

    }
    

    
    @GetMapping("/register2")
    public String sendRegisterPage(Model model){
        model.addAttribute(new User());
        return "registerUser";
    }
   
    
    
    @PostMapping("/register2")
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
