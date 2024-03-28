package com.mark.web.xmpp;

import java.io.IOException;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;
import com.mark.web.websocket.wsmessage.Message;

import lombok.extern.slf4j.Slf4j;


/**
 * sends content to the WebSocketSession 
 */
@Slf4j
public class SocketMessageSender {
public SocketMessageSender(){}
public void send(WebSocketSession session,Message message){
    try{
        Gson gson=new Gson();
        String str=gson.toJson(message);
        
        session.sendMessage(new TextMessage(str));
    }catch(IOException e){
        log.error("couldnt sent message to websocketsession "+e.getMessage());
    }
   } 
}
