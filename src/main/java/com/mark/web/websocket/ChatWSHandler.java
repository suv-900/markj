package com.mark.web.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatWSHandler extends TextWebSocketHandler{
   
    @Override
    public void handleTextMessage(WebSocketSession session,TextMessage message){
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session)throws Exception{
        log.info("SessionID: "+session.getId());
        log.info("LocalAddress: "+session.getLocalAddress().getPort());
        log.info("RemoteAddress: "+session.getRemoteAddress().getHostName());
    }

    @Override 
    public void afterConnectionClosed(WebSocketSession session,CloseStatus status)throws Exception{
        log.info("Connection closed");
        log.info("Status: "+status.getReason()+" "+status.getCode());
    }
}
