package com.mark.web.websocket.wsmessage;

import com.google.gson.Gson;

import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<Message>{
   
    @Override 
    public boolean willDecode(String s){
        return s != null;
    }

    @Override
    public Message decode(String s){
        Gson gson=new Gson();
        return gson.fromJson(s,Message.class);
    }

    @Override 
    public void init(EndpointConfig config){

    }

    @Override
    public void destroy(){

    }
}
