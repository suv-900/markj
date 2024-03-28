package com.mark.web.websocket.handlers;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.mark.web.websocket.wsmessage.Message;
import com.mark.web.xmpp.XMPPManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatWSHandler extends TextWebSocketHandler{
    private XMPPManager manager=new XMPPManager();

    public ChatWSHandler(){
        // this.manager=(XMPPManager) SpringContext.getApplicationContext().getBean("XMPPManager");
    }
    
    @Override
    public void handleTextMessage(WebSocketSession session,TextMessage message){
        String str=message.getPayload();
        Gson gson=new Gson();
        Message msg=gson.fromJson(str,Message.class);
        log.info("Message: "+msg);
        manager.handleMessage(msg,session);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session)throws Exception{
        log.info("Connection established");
    }

    @Override 
    public void afterConnectionClosed(WebSocketSession session,CloseStatus status)throws Exception{
        log.info("Connection closed");
        log.info("Status: "+status.getReason()+" "+status.getCode());
    }
}
