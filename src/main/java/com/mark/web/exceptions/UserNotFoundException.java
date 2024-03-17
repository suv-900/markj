package com.mark.web.exceptions;

public class UserNotFoundException extends Throwable {
    public UserNotFoundException(){
        super("User Not found.");
    }

    // public UserNotFoundException(String message){
    //     super(message);
    // }
}

