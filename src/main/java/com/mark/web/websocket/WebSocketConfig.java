package com.mark.web.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import com.mark.web.websocket.handlers.ChatWSHandler;
import com.mark.web.websocket.interceptors.WSInterceptor;
import com.mark.web.xmpp.SocketMessageSender;

// import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableWebSocket
@Configuration
//TODO:use SockJS
public class WebSocketConfig implements WebSocketConfigurer{
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWSHandler(), "/chat")
                .addInterceptors(new WSInterceptor())
                .setAllowedOrigins("http://localhost:3000");
    }

    @Bean
    public ServletServerContainerFactoryBean createContainer(){
        ServletServerContainerFactoryBean container=new ServletServerContainerFactoryBean();
        log.info("Creating new WebSocket container.");
        return container;
    }
    // @Bean 
    // public SocketMessageSender socketMessageSender(){
    //     return new SocketMessageSender();
    // }
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
} 
