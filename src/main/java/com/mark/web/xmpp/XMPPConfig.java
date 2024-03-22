package com.mark.web.xmpp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix="xmpp")
public class XMPPConfig {
    private String host;
    private int port;
    private String domain; 
}
