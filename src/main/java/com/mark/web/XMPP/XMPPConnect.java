package com.mark.web.XMPP;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import lombok.extern.slf4j.Slf4j;

//server -> local openfire jabberserver

@Slf4j
public class XMPPConnect {
    private static AbstractXMPPConnection connection;
    
    public XMPPConnect(){}

    public static void connect()throws Exception{
        log.info("Connecting to XMPP server");
            AbstractXMPPConnection connection;
            XMPPTCPConnectionConfiguration config=XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword("admin","123")
            .setHost("localhost")
            .setXmppDomain("core.localdomain")
            .setSecurityMode(SecurityMode.disabled)
            .build();
        
        connection=new XMPPTCPConnection(config);
        connection.connect();
        connection.login();
    }
    public static AbstractXMPPConnection getConnection()throws Exception{
        if(connection == null){
            throw new Exception("Connection is null");
        }
        return connection;
    } 
    // Example	Type
    // example.org	DomainBareJid
    // example.org/resource	DomainFullJid
    // user@example.org	EntityBareJid
    // user@example.org/resource	EntityFullJid

    // EntityBareJid user@xmppserver.org  

    
}
