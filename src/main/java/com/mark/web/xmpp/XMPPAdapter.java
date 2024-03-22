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
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableConfigurationProperties(XMPPConfig.class)
@RequiredArgsConstructor
public class XMPPAdapter {
   private final XMPPConfig baseConfig;

   public XMPPTCPConnection connect(String username,String password,boolean presence)
         throws IOException,XMPPException,SmackException,InterruptedException
   {
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
            throw e;
         }
         return connection; 
   }
   
   public void login(XMPPTCPConnection connection)
      throws IOException,SmackException,XMPPException,InterruptedException
   {
      try{
         connection.login();
      }catch(IOException | SmackException | XMPPException | InterruptedException e){
         throw e;
      }
      log.info("Login Successful.");
   }
   
   public void createAccount(XMPPTCPConnection connection,String username,String password)throws Exception{
      AccountManager accountManager=AccountManager.getInstance(connection);
      
      log.info("AccountManager instacne found for the connection "+accountManager);
      accountManager.sensitiveOperationOverInsecureConnection(true);
      try{
         accountManager.createAccount(Localpart.from(username),password);
         System.out.println("Account created using account manager: "+accountManager+" connection: "+connection);
      }catch(XMPPException.XMPPErrorException | SmackException.NoResponseException 
         | InterruptedException  | XmppStringprepException | SmackException.NotConnectedException e){
         throw e;
      }
   }

   public void sendMessage(XMPPTCPConnection connection,String message,String to)
      throws XmppStringprepException,SmackException.NotConnectedException,InterruptedException
   {
     ChatManager chatManager=ChatManager.getInstanceFor(connection);
      try{
         Chat chat=chatManager.chatWith(JidCreate.entityBareFrom(to+"@"+baseConfig.getDomain()));
         chat.send(message);
      }catch(XmppStringprepException | SmackException.NotConnectedException | InterruptedException e){
         throw e;
      }
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
         BareJid jid=JidCreate.bareFrom(to+"@"+baseConfig.getDomain());
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
         BareJid jid=JidCreate.bareFrom(user+"@"+baseConfig.getDomain());
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
