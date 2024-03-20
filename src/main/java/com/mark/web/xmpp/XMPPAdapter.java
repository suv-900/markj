package com.mark.web.XMPP;

import java.io.IOException;
import java.util.Optional;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableConfigurationProperties(XMPPConfig.class)
@RequiredArgsConstructor
public class XMPPAdapter {
   private final XMPPConfig baseConfig;
   private final AccountManager accountManager; 

   public Optional<XMPPTCPConnection> connect(String username,String password,boolean presence){
         XMPPTCPConnection connection;

         // .setCompressionEnabled(true)
         try{
            XMPPTCPConnectionConfiguration config=XMPPTCPConnectionConfiguration.builder()
               .setUsernameAndPassword(username, password)
               .setHost(baseConfig.getHost())
               .setPort(baseConfig.getPort())
               .setXmppDomain(baseConfig.getDomain())
               .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
               .setSendPresence(presence)
               .build();
            connection=new XMPPTCPConnection(config);
            connection.connect();
         }catch(IOException | InterruptedException | XMPPException | SmackException e){
            log.info("Error occured while connection to server "+e.getMessage());
            return Optional.empty();
         }
         return Optional.of(connection);
   }
}
