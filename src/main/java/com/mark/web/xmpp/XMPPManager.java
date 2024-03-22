package com.mark.web.xmpp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jxmpp.stringprep.XmppStringprepException;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;
import com.mark.web.exceptions.ServiceRuntimeException;
import com.mark.web.websocket.wsmessage.Message;
import com.mark.web.websocket.wsmessage.MessageType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * manager that initiates xmpp services,reports error and success.
 */

@Slf4j
@RequiredArgsConstructor
public class XMPPManager {
    private Map<WebSocketSession,XMPPTCPConnection> connections=new HashMap<>();
    private XMPPAdapter adapter;
    private SocketMessageSender messageSender;

    public void connect(WebSocketSession session,String username,String password,boolean presence){
        XMPPTCPConnection connection=null;
       
        try{
            connection=adapter.connect(username,password,presence);
        }catch(IOException | InterruptedException e){
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Couldnt create XMPPConnection.")
                .build());
            log.error("IO/Interrupted Exception: "+e.getMessage());
        }catch(ServiceRuntimeException r){
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Couldnt create XMPPConnection.")
                .build());
            log.error("RuntimeException: "+r.getMessage());
        }catch(XMPPException | SmackException e){
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Couldnt create XMPPConnection.")
                .build());
            log.error("XMPP/SmackException: "+e.getMessage());
        }

        try{
           if(connection == null && session.isOpen()){
            session.close(CloseStatus.SERVER_ERROR);
           } 
        }catch(IOException e){
            //TODO:unable to close session
            log.warn("Error closing websocket IOException: "+e.getMessage());
        }


        log.info("started xmppsession successfully.");
        log.info("Stored connection & session");
        connections.put(session,connection);
       
        try{
            
            adapter.login(connection);
            log.info("logged in successfully.");
            
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.SUCCESS)
                .build());
        }catch(IOException | InterruptedException e){
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Couldnt create XMPPConnection.")
                .build());
            log.error("IO/Interrupted Exception: "+e.getMessage());
        }catch(ServiceRuntimeException r){
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Couldnt create XMPPConnection.")
                .build());
            log.error("RuntimeException: "+r.getMessage());
        }catch(XMPPException | SmackException e){
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Couldnt create XMPPConnection.")
                .build());
            log.error("XMPP/SmackException: "+e.getMessage());
        }

    }

    public void sendMessage(WebSocketSession session,Message message){
        XMPPTCPConnection connection=connections.get(session);

        if(connection == null){
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Connection is not registered")
                .build());
            log.error("Connection not registered");
            try{
                if(session.isOpen()){
                    session.close(CloseStatus.SERVER_ERROR);
                }            
            }catch(IOException e){
                //TODO:unable to close session
                log.warn("Error closing websocket IOException: "+e.getMessage());
            }
            return;
        }

        switch(message.getMessageType()){
            case NEW_MESSAGE -> {
                try{
                    adapter.sendMessage(connection,message.getMessageContent(),message.getTo());
                    log.info("Message sent");
                    
                    log.info("Sending confirmation.");
                    sendMessageToSocket(session,Message.builder()
                        .messageType(MessageType.SUCCESS).build());
                
                }catch(SmackException.NotConnectedException e){
                    sendMessageToSocket(session,Message.builder()
                    .messageType(MessageType.ERROR)
                    .messageContent("Couldnt send message.")
                    .build());
                    log.error("Client not connected anymore: "+e.getMessage());
                }catch(InterruptedException e){
                    sendMessageToSocket(session,Message.builder()
                    .messageType(MessageType.ERROR)
                    .messageContent("Couldnt send message.")
                    .build());
                    log.error("InterruptedException: "+e.getMessage());
                }catch(ServiceRuntimeException r){
                    sendMessageToSocket(session,Message.builder()
                        .messageType(MessageType.ERROR)
                        .messageContent("Couldnt create XMPPConnection.")
                        .build());
                    log.error("RuntimeException: "+r.getMessage());
                }catch(XmppStringprepException e){
                    sendMessageToSocket(session,Message.builder()
                    .messageType(MessageType.ERROR)
                    .messageContent("Couldnt send message.")
                    .build());
                    log.error("StringpreprException: "+e.getMessage());
                } 
            }
            case ADD_CONTACT -> {
                try{
                    adapter.addContact(connection,message.getTo());
                    log.info("Contact added.");

                    log.info("Sending confirmation.");
                    sendMessageToSocket(session,Message.builder()
                        .messageType(MessageType.SUCCESS).build());
                }catch(SmackException.NotConnectedException e){
                    sendMessageToSocket(session,Message.builder()
                    .messageType(MessageType.ERROR)
                    .messageContent("Client not connected anymore.")
                    .build());
                    log.error("Client not connected anymore: "+e.getMessage());
                }catch(InterruptedException e){
                    sendMessageToSocket(session,Message.builder()
                    .messageType(MessageType.ERROR)
                    .messageContent("Couldnt send message.")
                    .build());
                    log.error("InterruptedException: "+e.getMessage());
                }catch(SmackException.NotLoggedInException e){
                    sendMessageToSocket(session,Message.builder()
                    .messageType(MessageType.ERROR)
                    .messageContent("Not logged in.")
                    .build());
                    log.error("NotLogedIn: "+e.getMessage());
                }catch(ServiceRuntimeException r){
                    sendMessageToSocket(session,Message.builder()
                        .messageType(MessageType.ERROR)
                        .messageContent("runtime error.")
                        .build());
                    log.error("RuntimeException: "+r.getMessage());
                }catch(SmackException.NoResponseException e){
                    sendMessageToSocket(session,Message.builder()
                        .messageType(MessageType.ERROR)
                        .messageContent("no response error.")
                        .build());
                    log.error("NoResponse: "+e.getMessage());
                }catch(XMPPException.XMPPErrorException e){
                    sendMessageToSocket(session,Message.builder()
                        .messageType(MessageType.ERROR)
                        .messageContent("xmpp error.")
                        .build());
                    log.error("Xmpp error: "+e.getMessage());
                }catch(XmppStringprepException e){
                    sendMessageToSocket(session,Message.builder()
                        .messageType(MessageType.ERROR)
                        .messageContent("string error.")
                        .build());
                    log.error("Stringprep error: "+e.getMessage());
                }
            }
            case GET_CONTACTS -> {
                Set<RosterEntry> contacts=Set.of(); 
                
                try{
                    contacts=adapter.getContacts(connection);
                }catch(SmackException.NotConnectedException e){
                    sendMessageToSocket(session,Message.builder()
                    .messageType(MessageType.ERROR)
                    .messageContent("Client not connected anymore.")
                    .build());
                    log.error("Client not connected anymore: "+e.getMessage());
                }catch(InterruptedException e){
                    sendMessageToSocket(session,Message.builder()
                    .messageType(MessageType.ERROR)
                    .messageContent("Couldnt get Contacts.")
                    .build());
                    log.error("InterruptedException: "+e.getMessage());
                }catch(SmackException.NotLoggedInException e){
                    sendMessageToSocket(session,Message.builder()
                    .messageType(MessageType.ERROR)
                    .messageContent("Not logged in.")
                    .build());
                    log.error("NotLogedIn: "+e.getMessage());
                }catch(ServiceRuntimeException r){
                    sendMessageToSocket(session,Message.builder()
                        .messageType(MessageType.ERROR)
                        .messageContent("Couldnt get contacts.")
                        .build());
                    log.error("RuntimeException: "+r.getMessage());
                }

                try{
                    log.info("Contacts found.");
                    Gson gson=new Gson();
                    String str=gson.toJson(contacts);
                    
                    log.info("Sending contacts");
                    sendMessageToSocket(session, Message.builder()
                        .messageContent(str)
                        .messageType(MessageType.SUCCESS)
                        .build());

                }catch(ServiceRuntimeException r){
                    sendMessageToSocket(session,Message.builder()
                        .messageType(MessageType.ERROR)
                        .messageContent("Couldnt get contacts.")
                        .build());
                    log.error("RuntimeException: "+r.getMessage());
                }
            }
            case REMOVE_CONTACT -> {
                try{
                    adapter.removeContact(connection,message.getTo());
                    log.info("Removed contact from roster");

                    sendMessageToSocket(session,Message.builder()
                        .messageType(MessageType.SUCCESS).build());
                }catch(SmackException.NotConnectedException e){
                    sendMessageToSocket(session,Message.builder()
                    .messageType(MessageType.ERROR)
                    .messageContent("Client not connected anymore.")
                    .build());
                    log.error("Client not connected anymore: "+e.getMessage());
                }catch(InterruptedException e){
                    sendMessageToSocket(session,Message.builder()
                    .messageType(MessageType.ERROR)
                    .messageContent("Couldnt send message.")
                    .build());
                    log.error("InterruptedException: "+e.getMessage());
                }catch(SmackException.NotLoggedInException e){
                    sendMessageToSocket(session,Message.builder()
                    .messageType(MessageType.ERROR)
                    .messageContent("Not logged in.")
                    .build());
                    log.error("NotLogedIn: "+e.getMessage());
                }catch(ServiceRuntimeException r){
                    sendMessageToSocket(session,Message.builder()
                        .messageType(MessageType.ERROR)
                        .messageContent("runtime error.")
                        .build());
                    log.error("RuntimeException: "+r.getMessage());
                }catch(SmackException.NoResponseException e){
                    sendMessageToSocket(session,Message.builder()
                        .messageType(MessageType.ERROR)
                        .messageContent("no response error.")
                        .build());
                    log.error("NoResponse: "+e.getMessage());
                }catch(XMPPException.XMPPErrorException e){
                    sendMessageToSocket(session,Message.builder()
                        .messageType(MessageType.ERROR)
                        .messageContent("xmpp error.")
                        .build());
                    log.error("Xmpp error: "+e.getMessage());
                }catch(XmppStringprepException e){
                    sendMessageToSocket(session,Message.builder()
                        .messageType(MessageType.ERROR)
                        .messageContent("string error.")
                        .build());
                    log.error("Stringprep error: "+e.getMessage());
                }
            }
            default -> log.warn("Message Type not implemented");
        }

    }

    public void disconnect(WebSocketSession session){
        XMPPTCPConnection connection = connections.get(session);
        
        if(connection == null){
            log.info("Connection not found to disconnect in the map.");
            sendMessageToSocket(session,Message.builder()
                    .messageType(MessageType.SUCCESS)
                    .messageContent("xmpp connection was not found to be closed.")
                    .build());
            try{
                if(session.isOpen()){
                    session.close();
                }            
            }catch(IOException e){
                //TODO:unable to close session
                log.warn("Error closing websocket IOException: "+e.getMessage());
            }  
            return;
        }

        try{
            adapter.disconnect(connection);
        }catch(SmackException.NotConnectedException e){
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Client not connected anymore.")
                .build());
            log.error("Client not connected anymore: "+e.getMessage());
        }catch(InterruptedException e){
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Couldnt disconnect interrupted error.")
                .build());
            log.error("InterruptedException: "+e.getMessage());
        }catch(ServiceRuntimeException r){
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Couldnt disconnect runtime error.")
                .build());
            log.error("RuntimeException: "+r.getMessage());
        }
        connections.remove(session);
        sendMessageToSocket(session,Message.builder()
            .messageType(MessageType.SUCCESS)
            .build());
    }
    
    private void sendMessageToSocket(WebSocketSession session,Message message){

        if(session == null){
            log.info("Session is null cannot send message");
            return;
        }
        if(!session.isOpen()){
            log.info("Session is closed cannot send message");
            return;
        }
       
        messageSender.send(session,message);

    }
}
