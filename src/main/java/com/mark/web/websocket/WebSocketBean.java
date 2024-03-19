package com.mark.web.websocket;

import com.mark.web.websocket.wsmessage.Message;
import com.mark.web.websocket.wsmessage.MessageDecoder;
import com.mark.web.websocket.wsmessage.MessageEncoder;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ServerEndpoint(value="/socket",encoders=MessageEncoder.class,decoders=MessageDecoder.class)
public class WebSocketBean{
    
    public WebSocketBean(){
        log.info("WebSocketBean creating");
    }

    @OnOpen
    public void onOpen(Session session){
        log.info("ws opened.");

        log.info("SessionId: "+session.getId());
        log.info("Number of session: "+session.getOpenSessions().size());
    
    }
    
    @OnMessage
    public void onMessage(Message message,Session session){
        System.out.println("on message");
    }
    
    @OnClose
    public void onClose(Session session){
        log.info("closing ws.");
    }
    
    @OnError
    public void onError(Throwable e,Session session){
        log.error(e.getMessage());
    }
}