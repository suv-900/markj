package com.mark.web.controllers;


import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;


import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tomcat.util.codec.binary.Base64;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mark.web.exceptions.UserNotFoundException;
import com.mark.web.models.Friend;
import com.mark.web.models.FriendRequest;
import com.mark.web.services.serviceImplementation.TokenServiceImplementation;
import com.mark.web.services.serviceImplementation.UserServiceImplementation;
import com.mark.web.utils.LogFileWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins="http://localhost:3000",exposedHeaders = "Token")
@Controller
@RequestMapping("/users")
public class UserController {
    private static UserServiceImplementation userService=new UserServiceImplementation();
    private static TokenServiceImplementation tokenService=new TokenServiceImplementation();   
    private static LogFileWriter logwriter=LogFileWriter.getInstance();

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
            logwriter.write(e.getMessage());
            return "Server Error";
        }
        
        response.setStatus(200);
        return "OK";

    }
    
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
            logwriter.write(e.getMessage());
            response.setStatus(500);
            return "Server Error";
        }catch(UnsupportedOperationException e){
            log.error(e.getMessage());
            String msg="Element is neither JsonPrimitive nor JsonArray";
            logwriter.write(msg+" "+e.getMessage());
            response.setStatus(400);
            return "Bad Request";
        }catch(IllegalStateException e){
            log.error(e.getMessage());
            String msg="JsonElement is a array,but there is more than 1 element";
            logwriter.write(msg+" "+e.getMessage());
            response.setStatus(400);
            return "Bad Request";
        }catch(Exception e){
            log.error(e.getMessage());
            logwriter.write(e.getMessage());
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
                logwriter.write(e.getMessage());
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
            logwriter.write(e.toString());
            return e.getMessage();
        }

    }

    @GetMapping("/logerror")
    public void logError(){
        try{
            throw new Exception("Hi");

        }catch(Exception e){
            logwriter.write(e.toString());
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
            System.out.println(jsonObject);
            username=jsonObject.get("username").getAsString();
            password=jsonObject.get("password").getAsString();
            email=jsonObject.get("email").getAsString();
        }catch(IOException e){
            log.error(e.getMessage());
            logwriter.write(e.getMessage());
            response.setStatus(500);
            return "Server Error";
        }catch(UnsupportedOperationException e){
            log.error(e.getMessage());
            String msg="Element is neither JsonPrimitive nor JsonArray";
            logwriter.write(msg+" "+e.getMessage());
            response.setStatus(400);
            return "Bad Request";
        }catch(IllegalStateException e){
            log.error(e.getMessage());
            String msg="JsonElement is a array,but there is more than 1 element";
            logwriter.write(msg+" "+e.getMessage());
            response.setStatus(400);
            return "Bad Request";
        }catch(Exception e){
            log.error(e.getMessage());
            logwriter.write(e.getMessage());
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
            logwriter.write(e.getMessage());
            return "Server Error";
        }

    }
    
    
  
    @GetMapping("/friends")
    public String sendFriendsPage(){
        return "friendsPage";
    }


    // @RequestMapping(path="/getFriendRequests",produces="application/json",method=RequestMethod.GET)
    // @ResponseBody
    // public List<FriendRequest> getFriendRequests(HttpServletRequest req,HttpServletResponse res){
    //     List<FriendRequest> friendRequests=new LinkedList<FriendRequest>();
        
    //     String token=req.getHeader("Token");
    //     if(token == null){
    //         res.setStatus(400);
    //         return friendRequests;
    //     }

    //     int userid=tokenService.verifyToken(token);
        
    //     try{
    //         if(userid == -1){
    //             res.sendError(404, "User not found");
    //             return friendRequests;
    //         }
    //         if(userid == 500){
    //             res.setStatus(500);
    //             return friendRequests;
    //         }
    //     }catch(IOException e){
    //         log.info(e);
    //     }


    //     try{
    //         friendRequests=userService.getFriendRequests(userid);
    //     }catch(SQLException e){
    //         log.info(e);
    //         res.setStatus(500);
    //         return friendRequests;
    //     }

    //     System.out.println("FriendRequests: "+friendRequests);

    //     return friendRequests;
        
    // }

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
        }catch(JWTVerificationException e){
           log.warn(e.getMessage());
           response.setStatus(401);
           return e.getMessage(); 
        }catch(NumberFormatException e){
            response.setStatus(500);
            log.error(e.getMessage());
            logwriter.write(e.getMessage());
            return "Server Error";
        }
        catch(Exception e){
            log.info(e.getMessage());
            e.printStackTrace();
            response.setStatus(500);
            return "Server Error";
        } 
        Gson gson=new Gson();
        String friendsListString=gson.toJson(friendsList);
        response.setStatus(200);
        return friendsListString;
       
    }

    @RequestMapping(path="/getPendingFriendRequests",produces="application/json",method=RequestMethod.GET)
    @ResponseBody
    public List<FriendRequest> getFriendRequests(HttpServletRequest req,HttpServletResponse res){
        List<FriendRequest> list=new LinkedList<FriendRequest>();
        
        String token=req.getHeader("Token");
        if(token == null){
            res.setStatus(400);
            return list;
        }
        
        try{
            int userid=tokenService.verifyToken(token);
            if(userid == -1){
                res.sendError(404, "User not found");
                return list;
            }
            if(userid == 500){
                res.setStatus(500);
                return list;
            }
            list=userService.getFriendRequests(userid);
            res.setStatus(200);
        }catch(Exception e){
            log.info(e.getMessage());
            e.printStackTrace();
            res.setStatus(500);
        }
        
        return list;
    }

    @ResponseBody
    @PostMapping(path="/sendFriendRequest",consumes={MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public String sendFriendRequest(@RequestParam MultiValueMap<String,String> form,HttpServletRequest req,HttpServletResponse res){
        
        String token=req.getHeader("Token");
        String ToUsername=form.getFirst("ToUsername");
        //i need to come with better names 
        
        if(token == null || (ToUsername.length()==0)){
            res.setStatus(400);

            return "Invalid Request";
        }
        try{
            //you go here once
            
            int userid=tokenService.verifyToken(token);
            if(userid == -1){
                res.setStatus(400);
                return "Bad token";
            }
            if(userid == 500){
                res.setStatus(500);
                return "Server Error";
            }

            userService.sendFriendRequest(ToUsername,userid);
            res.setStatus(200);
            return "OK";
        }catch(Exception e){
            log.info(e.getMessage());
            
            res.setStatus(500);
            return e.getMessage();
        }

    }
    
    @ResponseBody
    @RequestMapping(path="/denyFriendRequest",method=RequestMethod.POST)
    public String acceptFriendRequest(HttpServletRequest req,HttpServletResponse res){
        
        String token=req.getHeader("Token");
        String fs=req.getParameter("fromUserID"); 
        if(token == null ||  fs == null ){
            res.setStatus(400);
            return "Invalid Request";
        }
        
        
        try{
            //you go here once
            int fromUserID=Integer.parseInt(fs);

            int toUserID=tokenService.verifyToken(token);
            if(toUserID== -1){
                res.setStatus(400);
                return "Bad token";
            }
            if(toUserID == 500){
                res.setStatus(500);
                return "Server Error";
            }
            
            userService.denyFriendRequest(fromUserID, toUserID);
            res.setStatus(200);
            return "OK";
        }catch(Exception e){
            log.info(e.getMessage());
            e.printStackTrace(); 
            res.setStatus(500);
            return e.getMessage();
        }


    }

    @ResponseBody
    @RequestMapping(path="/acceptFriendRequest",method=RequestMethod.POST)
    public String denyFriendRequest(HttpServletRequest req,HttpServletResponse res){
        
        String token=req.getHeader("Token");
        String username=req.getParameter("username"); 
        if(token == null ||  username.length()==0 ){
            res.setStatus(400);
            return "Invalid Request";
        }
        
        
        try{
            //you go here once
            
            int userid=tokenService.verifyToken(token);
            if(userid == -1){
                res.setStatus(400);
                return "Bad token";
            }
            if(userid == 500){
                res.setStatus(500);
                return "Server Error";
            }
            
            userService.acceptFriendRequest(username, userid);
            res.setStatus(200);
            return "OK";
        }catch(Exception e){
            log.info(e.getMessage());
            e.printStackTrace(); 
            res.setStatus(500);
            return e.getMessage();
        }


    }
    
    @ResponseBody
    @RequestMapping(path="/getOneFriend",method=RequestMethod.GET)
    public Friend getFriend(HttpServletRequest req,HttpServletResponse res){
        Friend friend=new Friend();    
        
        String token=req.getHeader("Token");
        String uid=req.getParameter("userID"); 

        if(token == null ||  uid.length()==0 ){
            res.setStatus(400);
            return friend;
        }
        
        int userID=Integer.parseInt(uid);
        
        
        try{
            //you go here once
            
            int userid=tokenService.verifyToken(token);
            if(userid == -1){
                res.setStatus(400);
                return friend;
            }
            if(userid == 500){
                res.setStatus(500);
                return friend; 
            }
            
            friend=userService.getFriend(userID);
            res.setStatus(200);
            return friend;
        }catch(Exception e){
            log.info(e.getMessage());
            e.printStackTrace(); 
            res.setStatus(500);
            return friend;
        }



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
