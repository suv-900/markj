package com.mark.web.exceptions;

public class RequestExistsException extends Throwable {
   public RequestExistsException(){
    super("Request already exists.");
   }
   public RequestExistsException(String message){
    super(message);
   } 
}
