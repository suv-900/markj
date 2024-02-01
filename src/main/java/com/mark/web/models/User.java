package com.mark.web.models;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String userDescription;
    private boolean online;
    public User(){}

    //setter functions;
    public void setUsername(String name){
        this.username=name;
    }
    
    public void setEmail(String e){
        this.email=e;
    }
    public void setUserOnline(boolean o){
        this.online=o;
    }

    public void setID(int id){
        this.id=id;
    }

    public void setPassword(String pass){
        this.password=pass;
    }

    public void setDescription(String description){
        this.userDescription=description;
    }

    //getter functions;
    public String getUsername(){
        return this.username;
    }
    public String getEmail(){
        return this.email;
    }
    public boolean getOnline(){
        return this.online;
    }
    public String getPassword(){
        return this.password;
    }

    public int getUserID(){
        return this.id;
    }

    public String getUserDescription(){
        return this.userDescription;
    }
    //private String email;
    public boolean checkNULL(){
        if(username.isEmpty()){ 
            return true;
        }
        if(email.isEmpty()){
            return true;
        }
        if(password.isEmpty()){
            return true;
        }
        return false;
    }
}
