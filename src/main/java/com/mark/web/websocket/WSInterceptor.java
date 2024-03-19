package com.mark.web.websocket;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WSInterceptor implements HandshakeInterceptor{
    
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,ServerHttpResponse response,WebSocketHandler handler,Map<String,Object> attributes){
        log.info("beforeHandshake()");
        HttpHeaders headers=request.getHeaders();
        
        System.out.println("Host "+headers.getHost());

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,ServerHttpResponse response,WebSocketHandler handler,Exception e){
        log.info("after handshake");
    }
}
