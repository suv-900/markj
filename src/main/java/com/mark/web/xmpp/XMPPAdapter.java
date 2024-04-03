package com.mark.web.xmpp;

import java.io.IOException;
import java.util.Set;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.PresenceBuilder;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;
// import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.socket.WebSocketSession;

import com.mark.web.websocket.wsmessage.Message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
// @EnableConfigurationProperties(XMPPConfig.class)
public class XMPPAdapter {
   // private XMPPConfig baseConfig;
   private SocketMessageSender messageSender=SocketMessageSender.getInstance();
   private int port=5222;
   private String host="openfire";
   private String domain="core.localdomain";
   public XMPPAdapter(){}
   
   public XMPPTCPConnection createConnection(String username,String password,boolean presence)
         throws IOException,XMPPException,SmackException,InterruptedException
   {
         XMPPTCPConnection connection;

         // .setCompressionEnabled(true)
         try{
            XMPPTCPConnectionConfiguration config=XMPPTCPConnectionConfiguration.builder()
               .setUsernameAndPassword(username, password)
               .setHost("localhost")
               .setPort(5222)
               .setXmppDomain("core.localdomain")
               // .setHost(baseConfig.getHost())
               // .setPort(baseConfig.getPort())
               // .setXmppDomain(baseConfig.getDomain())
               .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
               .setSendPresence(presence)
               .build();
            connection=new XMPPTCPConnection(config);
            connection.connect();
         }catch(IOException | InterruptedException | XMPPException | SmackException e){
            throw e;
         }
         return connection; 
   }
   
   public void login(XMPPTCPConnection connection)
      throws IOException,SmackException,XMPPException,InterruptedException
   {
      AccountManager accountManager=AccountManager.getInstance(connection);
      try{
         connection.login();
      }catch(IOException | SmackException | XMPPException | InterruptedException e){
         throw e;
      }
      log.info("Login Successful.");
   }
   
   public void createAccount(XMPPTCPConnection connection,String username,String password)throws XMPPException.XMPPErrorException,
   SmackException.NoResponseException,InterruptedException,XmppStringprepException,SmackException.NotConnectedException{
      
      AccountManager accountManager=AccountManager.getInstance(connection);
      accountManager.sensitiveOperationOverInsecureConnection(true);
      
      try{
         accountManager.createAccount(Localpart.from(username),password);
         log.info("Account created using account manager,connection: "+connection);
      }catch( SmackException.NoResponseException 
         | InterruptedException  |XMPPException.XMPPErrorException | SmackException.NotConnectedException e){
         throw e;
      }
   }

   public void sendMessage(XMPPTCPConnection connection,String message,String to)
      throws XmppStringprepException,SmackException.NotConnectedException,InterruptedException
   {
     ChatManager chatManager=ChatManager.getInstanceFor(connection);
      try{
         // Chat chat=chatManager.chatWith(JidCreate.entityBareFrom(to+"@"+baseConfig.getDomain()));
         Chat chat=chatManager.chatWith(JidCreate.entityBareFrom(to+"@"+domain));
         chat.send(message);
      }catch(XmppStringprepException | SmackException.NotConnectedException | InterruptedException e){
         throw e;
      }
   }
   public void addIncomingMessageListener(XMPPTCPConnection connection,WebSocketSession session){
      ChatManager chatManager=ChatManager.getInstanceFor(connection);
      chatManager.addIncomingListener((from,message,chat)->messageSender.send(session,Message.builder()
         .to(message.getTo().getLocalpartOrNull().toString()).messageContent(message.getBody()).from(message.getFrom().getLocalpartOrNull().toString())
         .build()));
   }
   public Set<RosterEntry> getContacts(XMPPTCPConnection connection)
      throws SmackException.NotConnectedException,InterruptedException,SmackException.NotLoggedInException
   {
      Roster roster=Roster.getInstanceFor(connection);
      if(!roster.isLoaded()){
         try{
            roster.reloadAndWait();
         }catch(SmackException.NotConnectedException | InterruptedException | SmackException.NotLoggedInException e){
            throw e;
         }
      }
      return roster.getEntries();
   }
   public void addContact(XMPPTCPConnection connection,String to)
      throws XmppStringprepException,XMPPException.XMPPErrorException,SmackException.NotConnectedException, 
         SmackException.NoResponseException,SmackException.NotLoggedInException,InterruptedException
   {
      Roster roster=Roster.getInstanceFor(connection);

      if(!roster.isLoaded()){
         try{
            roster.reloadAndWait();
         }catch(SmackException.NotConnectedException | InterruptedException | SmackException.NotLoggedInException e){
            throw e;
         }
      }

      try{
         // BareJid jid=JidCreate.bareFrom(to+"@"+baseConfig.getDomain());
         BareJid jid=JidCreate.bareFrom(to+"@"+domain);
         roster.createItemAndRequestSubscription(jid,to,null);
      }catch(XmppStringprepException | XMPPException.XMPPErrorException | SmackException.NotConnectedException 
         | SmackException.NoResponseException | SmackException.NotLoggedInException | InterruptedException e){
         throw e;
      }
   }
   public void removeContact(XMPPTCPConnection connection,String user)throws SmackException.NotConnectedException,InterruptedException,
      SmackException.NotLoggedInException,XMPPException.XMPPErrorException,
      XmppStringprepException,SmackException.NoResponseException{
      
      Roster roster=Roster.getInstanceFor(connection);

      //dynamic list->presence 
      if(!roster.isLoaded()){
         try{
            roster.reloadAndWait();
         }catch(SmackException.NotConnectedException | InterruptedException | SmackException.NotLoggedInException e){
            throw e;
         }
      }

      try{
         // BareJid jid=JidCreate.bareFrom(user+"@"+baseConfig.getDomain());
         BareJid jid=JidCreate.bareFrom(user+"@"+domain);
         roster.removeEntry(roster.getEntry(jid));
      }catch(XmppStringprepException | XMPPException.XMPPErrorException | SmackException.NotConnectedException 
         | SmackException.NoResponseException | SmackException.NotLoggedInException | InterruptedException e){
            throw e;
      } 
   }
   public void disconnect(XMPPTCPConnection connection)
   throws SmackException.NotConnectedException,InterruptedException 
   {
      try{
         log.info("Disconnecting from Server.");  
         Presence presence = PresenceBuilder.buildPresence()
         .ofType(Presence.Type.unavailable).build();

         log.info("Sending stanza.");  
         connection.sendStanza(presence);
         
         connection.disconnect();
         log.info("disconnected successfully");
      }catch(SmackException.NotConnectedException | InterruptedException e){
         throw e;
      }
   }

   public void sendStanza(XMPPTCPConnection connection,Presence.Type type)
   throws SmackException.NotConnectedException,InterruptedException 
   {
      log.info("sending stanza of type "+type.toString());
      try{
         Presence presence=PresenceBuilder.buildPresence().ofType(type).build();
         connection.sendStanza(presence);
      }catch(SmackException.NotConnectedException | InterruptedException e){
         throw e;
      }
      log.info("stanza sent.");
   }
}
