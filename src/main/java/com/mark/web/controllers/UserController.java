package com.mark.web.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.mark.web.models.User;
import com.mark.web.services.serviceImplementation.UserServiceImplementation;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/users")
public class UserController {

    private static UserServiceImplementation userServiceImpl=new UserServiceImplementation();
    // private static final Logger logger=LogManager.getLogger(); 
    // private static final UtillityServiceImplementation utils=new UtillityServiceImplementation();
    
    
    @GetMapping("/login")
    public String sendLoginPage(Model model){
        model.addAttribute("name","Isoap");
        System.out.println(model);
        return "loginUser";
    }
    

    @GetMapping("/register")
    public String sendRegisterPage(Model model){
        System.out.println(model);
        model.addAttribute(new User());
        System.out.println(model);
        return "registerUser";
    }
    @RequestMapping(value="/validateUserLogin",method=RequestMethod.POST)
    public String validateUserLogin(@RequestParam("username")String username,
        @RequestParam("password")String password,    
        HttpServletResponse res){
        System.out.println(username+" "+password);
        return "homePage";
        // boolean loginSuccess=userServiceImpl.loginUser(username,password);
    }

    //username pass email
    @RequestMapping(value="/registerUser",method=RequestMethod.POST)
    public String registerUser(@ModelAttribute User user){
           //username invalid
            
            userServiceImpl.checkUsername(user.getUsername()); 
            return "homePage";
        //    if(userServiceImpl.checkUsername(user.getUsername())){
            //username exists;   

            // // System.out.println(user);

            // // String hashedPassword=utils.generateHashedPassword(user.getPassword());
            // // user.setPassword(hashedPassword);
            
            // try{
            //     // userServiceImpl.add(user);
            // }catch(SQLException e){
                
            //     e.printStackTrace();
            //     //return 500 and log error
            // }
            //only care about username and token and feed post

        }
    //what a code
    //checkusername function
    //check the response
    @RequestMapping(value="checkUsername",method=RequestMethod.POST)
    public void checkIfUsernameExists(@RequestBody String username){
        // boolean usernameExists=userServiceImpl.checkUsername(username);
        // return usernameExists; 
    }
    
}
