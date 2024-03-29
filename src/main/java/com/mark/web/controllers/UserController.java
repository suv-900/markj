package com.mark.web.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.apache.tomcat.util.codec.binary.Base64;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


import com.mark.web.exceptions.RequestExistsException;
import com.mark.web.exceptions.UserNotFoundException;
import com.mark.web.models.Friend;
import com.mark.web.models.FriendRequest;
import com.mark.web.services.serviceImplementation.TokenServiceImplementation;
import com.mark.web.services.serviceImplementation.UserServiceImplementation;
import com.mark.web.utils.LogFileWriter;

/**
 * all the handlers for user authentications.
 */

 //login,register,verifyToken,getFriends,getFriendRequests,sendFriendRequest
// getOneFriend,acceptFriendRequest,denyFriendRequest

@Slf4j
@CrossOrigin(origins="http://localhost:3000",exposedHeaders = "Token")
@Controller
@RequestMapping("/users")
public class UserController {
    private static UserServiceImplementation userService=new UserServiceImplementation();
    private static TokenServiceImplementation tokenService=new TokenServiceImplementation();   

    @PostMapping("/verifytoken")
    @ResponseBody
    public String verifyToken(HttpServletRequest request,HttpServletResponse response){

        String token=request.getHeader("Token");
       
        if(token == null){
            response.setStatus(400);
            return "No Token Found";
        }
        
        try{
            tokenService.verifyToken(token);
        }catch(JWTVerificationException e){
           log.warn(e.getMessage());
           response.setStatus(401);
           return e.getMessage(); 
        }catch(NumberFormatException e){
            response.setStatus(500);
            log.error(e.getMessage());
            return "Server Error";
        }
        
        response.setStatus(200);
        return "OK";

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
    

    @PostMapping(path="/login")
    @ResponseBody
    public String loginUser(HttpServletRequest request,HttpServletResponse response){
        
        String username="";
        String password="";
        try{
            BufferedReader bufReader=request.getReader();

            String str="";
            while(bufReader.ready()){
                str+=(char)bufReader.read();
            }
            JsonObject jsonObject=JsonParser.parseString(str).getAsJsonObject();
            username=jsonObject.get("username").getAsString();
            password=jsonObject.get("password").getAsString();
        }catch(IOException e){
            log.error(e.getMessage());
            response.setStatus(500);
            return "Server Error";
        }catch(UnsupportedOperationException e){
            log.error(e.getMessage());
            response.setStatus(400);
            return "Bad Request";
        }catch(IllegalStateException e){
            log.error(e.getMessage());
            response.setStatus(400);
            return "Bad Request";
        }catch(Exception e){
            log.error(e.getMessage());
            response.setStatus(500);
            return "Server Error";
        }
        
        if( username.isEmpty() || password.isEmpty()){
            response.setStatus(400);
            return "Bad Request";
        }

        try{
            List<String> list;
            
            try{
                list=userService.loginUser(username);
            }catch(UserNotFoundException e){
                log.error(e.getMessage());
                response.setStatus(400);
                return e.getMessage();
            }catch(Exception e){
                throw e;
            }

            String s=list.get(1);
            int userid=Integer.parseInt(s);
            String dbPassword=list.get(0);

            boolean isOK=userService.comparePassword(dbPassword,password);
            
            if(!isOK){
                response.setStatus(401);
                return "Incorrect Password";
            }

            String token=tokenService.createToken(userid);
            response.setHeader("Token",token);
            response.setStatus(200);
            return "OK";
        }catch(Exception e){
            log.error(e.getMessage());
            response.setStatus(500);
            return e.getMessage();
        }

    }

   
    @PostMapping(path="/register")
    @ResponseBody
    public String registerUser(HttpServletRequest request,HttpServletResponse response){
        String username="";
        String password="";
        String email="";
        try{
            BufferedReader bufReader=request.getReader();

            String str="";
            while(bufReader.ready()){
                str+=(char)bufReader.read();
            }
            JsonObject jsonObject=JsonParser.parseString(str).getAsJsonObject();
            username=jsonObject.get("username").getAsString();
            password=jsonObject.get("password").getAsString();
            email=jsonObject.get("email").getAsString();
        }catch(IOException e){
            log.error(e.getMessage());
            response.setStatus(500);
            return "Server Error";
        }catch(UnsupportedOperationException e){
            log.error(e.getMessage());
            response.setStatus(400);
            return "Bad Request";
        }catch(IllegalStateException e){
            log.error(e.getMessage());
            response.setStatus(400);
            return "Bad Request";
        }catch(Exception e){
            log.error(e.getMessage());
            response.setStatus(500);
            return "Server Error";
        }
     
        if(username.isEmpty() || password.isEmpty() || email.isEmpty()){
            response.setStatus(400);
            return "Bad Request Missing fields";
        }
         
        try{
            if(userService.checkUsername(username)){
                response.setStatus(400);
                return "User exists";
            }        
            int userid=userService.registerUser(username,password,email);
            String token=tokenService.createToken(userid);
            response.setHeader("Token",token);
            response.setStatus(200);
            return "OK";

        }catch(Exception e){
            log.info(e.getMessage());
            response.setStatus(500);
            return "Server Error";
        }

    }
    
    @SendTo
    public void sendToWS(){

    }
    @GetMapping(path="/getFriends")
    @ResponseBody
    public String getFriends(HttpServletRequest request,HttpServletResponse response){
        
        String token=request.getHeader("Token");
        if(token == null){
            response.setStatus(400);
            return "Token Not Found";
        } 
    
        List<Friend> friendsList=new LinkedList<Friend>();
        
        try{
            int userid=tokenService.verifyToken(token);
            friendsList=userService.getAllFriends(userid);

            Gson gson=new Gson();
            String str=gson.toJson(friendsList);
            response.setStatus(200);
            return str;
        }catch(JWTVerificationException e){
           log.warn(e.getMessage());
           response.setStatus(401);
           return e.getMessage(); 
        }catch(NumberFormatException e){
            response.setStatus(500);
            log.error(e.getMessage());
            return "Server Error";
        }
        catch(Exception e){
            log.error(e.getMessage());
            response.setStatus(500);
            return "Server Error";
        } 
       
    }

    @RequestMapping(path="/getPendingFriendRequests",produces="application/json",method=RequestMethod.GET)
    @ResponseBody
    public String getFriendRequests(HttpServletRequest request,HttpServletResponse response){
        
        String token=request.getHeader("Token");
        if(token == null){
            response.setStatus(401);
            return "Token Not Found";
        }
        
        List<FriendRequest> list=new LinkedList<FriendRequest>();
        try{
            int userid=tokenService.verifyToken(token);
            list=userService.getFriendRequests(userid);

            Gson gson=new Gson();
            String str=gson.toJson(list);
            response.setStatus(200); 
            return str;
        }
        catch(JWTVerificationException e){
           log.warn(e.getMessage());
           response.setStatus(401);
           return e.getMessage(); 
        }
        catch(NumberFormatException e){
            log.error(e.getMessage());
            response.setStatus(500);
            return "Server Error";
        }
        catch(Exception e){
            log.error(e.getMessage());
            response.setStatus(500);
            return "Server Error";
        }
        
    }

    @ResponseBody
    @PostMapping(path="/sendFriendRequest")
    public String sendFriendRequest(HttpServletRequest request,HttpServletResponse response){
        
        String token=request.getHeader("Token");
        //i need to come with better names 
        String toUsername;
        try{
            BufferedReader bufReader=request.getReader();

            String str="";
            while(bufReader.ready()){
                str+=(char)bufReader.read();
            }
            JsonObject jsonObject=JsonParser.parseString(str).getAsJsonObject();
            toUsername=jsonObject.get("ToUsername").getAsString();
        }catch(IOException e){
            log.error(e.getMessage());
            response.setStatus(500);
            return "Server Error";
        }catch(UnsupportedOperationException e){
            log.error(e.getMessage());
            response.setStatus(400);
            return "Bad Request";
        }catch(IllegalStateException e){
            log.error(e.getMessage());
            response.setStatus(400);
            return "Bad Request";
        }catch(Exception e){
            log.error(e.getMessage());
            response.setStatus(500);
            return "Server Error";
        }
        if(token == null){
            response.setStatus(401);
            return "Token not found.";
        }
        if(toUsername.length() == 0){
            response.setStatus(400);
            return "ToUsername not found";
        }

        
        try{
            int userid=tokenService.verifyToken(token);
            userService.sendFriendRequest(toUsername,userid);

            response.setStatus(200);
            return "OK";
        }catch(JWTVerificationException e){
           log.warn(e.getMessage());
           response.setStatus(401);
           return e.getMessage(); 
        }
        catch(NumberFormatException e){
            response.setStatus(500);
            log.error(e.getMessage());
            return "Server Error";
        }catch(RequestExistsException r){
            response.setStatus(409);
            return r.getMessage();
        }
        catch(Exception e){
            log.info(e.getMessage());
            response.setStatus(500);
            return "Server Error";
        }

    }
    
    @ResponseBody
    @RequestMapping(path="/denyFriendRequest",method=RequestMethod.POST)
    public String denyFriendRequest(HttpServletRequest request,HttpServletResponse response){
        
        String token=request.getHeader("Token");
        String fs=request.getParameter("fromUserID"); 
        
        if(token == null){
            response.setStatus(401);
            return "Token not found.";
        }
        if(fs.length() == 0){
            response.setStatus(400);
            return "fromUserID not found";
        }
        
        try{
            //you go here once
            int fromUserID=Integer.parseInt(fs);
            int toUserID=tokenService.verifyToken(token);
            userService.denyFriendRequest(fromUserID, toUserID);
            
            response.setStatus(200);
            return "OK";
        }catch(JWTVerificationException e){
           log.warn(e.getMessage());
           response.setStatus(401);
           return e.getMessage(); 
        }
        catch(NumberFormatException e){
            response.setStatus(500);
            log.error(e.getMessage());
            return "Server Error";
        }
        catch(Exception e){
            log.info(e.getMessage());
            response.setStatus(500);
            return "Server Error";
        }


    }

    @RequestMapping(path="/acceptFriendRequest",method=RequestMethod.POST)
    @ResponseBody
    public String acceptFriendRequest(HttpServletRequest request,HttpServletResponse response){
        
        String token=request.getHeader("Token");
        String username=request.getParameter("username"); 
        if(token == null){
            response.setStatus(401);
            return "Token not found.";
        }
        if(username.length() == 0){
            response.setStatus(400);
            return "fromUserID not found";
        }
        
        try{
            int userid=tokenService.verifyToken(token);
            userService.acceptFriendRequest(username, userid);
            
            response.setStatus(200);
            return "OK";
        }catch(JWTVerificationException e){
           log.warn(e.getMessage());
           response.setStatus(401);
           return e.getMessage(); 
        }
        catch(NumberFormatException e){
            response.setStatus(500);
            log.error(e.getMessage());
            return "Server Error";
        }
        catch(Exception e){
            log.info(e.getMessage());
            response.setStatus(500);
            return "Server Error";
        }


    }
    
    @RequestMapping(path="/getOneFriend",method=RequestMethod.GET)
    @ResponseBody
    public String getFriend(HttpServletRequest request,HttpServletResponse response){
        
        String token=request.getHeader("Token");
        String uid=request.getParameter("userID"); 

        if(token == null){
            response.setStatus(401);
            return "Token not found.";
        }
        if(uid == null){
            response.setStatus(400);
            return "userID not found";
        }
        
        try{
            //you go here once
            
            Friend friend=new Friend();    
            int userID=Integer.parseInt(uid);
            tokenService.verifyToken(token);
            friend=userService.getOneFriend(userID);
            
            Gson gson=new Gson();
            String str=gson.toJson(friend);
            response.setStatus(200);
            return str;
        }catch(JWTVerificationException e){
           log.warn(e.getMessage());
           response.setStatus(401);
           return e.getMessage(); 
        }
        catch(NumberFormatException e){
            response.setStatus(500);
            log.error(e.getMessage());
            return "Server Error";
        }
        catch(Exception e){
            log.info(e.getMessage());
            response.setStatus(500);
            return "Server Error";
        }

    }
    
}
