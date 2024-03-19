package com.mark.web.websocket.wsmessage;

import com.google.gson.Gson;

import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<Message>{
    @Override
    public String encode(Message msg){
        Gson gson=new Gson();
        String str=gson.toJson(msg);
        System.out.println("MessageEncoder "+str);
        return str;
    }
    
    @Override 
    public void init(EndpointConfig config){
        System.out.println("MessageEncoder init() called"); 
    }

    @Override
    public void destroy(){
        System.out.println("MessageEncoder destroy() called."); 

    }
}
