package com.mark.web.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableWebSocket
@Configuration
public class WebSocketConfig implements WebSocketConfigurer{

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWSHandler(), "/chat")
                .addInterceptors(new WSInterceptor());

    }

    @Bean
    public ServletServerContainerFactoryBean createContainer(){
        log.info("Creating new container.");
        ServletServerContainerFactoryBean container=new ServletServerContainerFactoryBean();
        return container;
    }
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
} 
