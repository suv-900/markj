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

import lombok.extern.slf4j.Slf4j;

/**
 * manager that initiates xmpp services,reports error and success.
 */

@Slf4j
public class XMPPManager {
    private Map<WebSocketSession,XMPPTCPConnection> connections=new HashMap<>();
    private final XMPPAdapter adapter=new XMPPAdapter();
    private final SocketMessageSender messageSender=new SocketMessageSender();
    
    public XMPPManager(){}

    public void handleMessage(Message message,WebSocketSession session){
        if(message.getMessageType() == null ){
           sendMessageToSocket(session, Message.builder()
            .messageType(MessageType.ERROR)
            .messageContent("MessageType is null")
            .build());
            
            return; 
        } 
        MessageType type=message.getMessageType();
        //type.ordinal()-> int position in enum
        switch(type){
            case REGISTER -> {
                String username=message.getFrom();
                String password=message.getMessageContent();
                //TODO
                boolean presence=true;

                if(username == null || password == null){
                    //bad message
                    sendMessageToSocket(session, Message.builder().messageType(MessageType.ERROR)
                    .messageContent("username/password is null").build());
                    return;
                }
                //rehashing
                register(session,username,password,presence);
            }
            case CONNECT -> {
                String username=message.getFrom();
                String password=message.getMessageContent();
                //TODO
                boolean presence=true;

                if(username == null || password == null){
                    //bad message
                    sendMessageToSocket(session, Message.builder().messageType(MessageType.ERROR)
                    .messageContent("username/password is null").build());
                    return;
                }
                //rehashing
                connect(session,username,password,presence);

            }
            case DISCONNECT -> {
                disconnect(session);
            }
            case NEW_MESSAGE -> {
                sendMessage(session,message);
            }
            default -> {
                log.error(type+" type is not implemented.");
                sendMessageToSocket(session, Message.builder()
                    .messageType(MessageType.ERROR)
                    .messageContent(type+" type is not implemented.")
                    .build());
            }
        }
    }
    private void closeSession(WebSocketSession session){
        try{
            if(session.isOpen()){
                session.close();
            }
        }catch(IOException e){
            log.error("Couldnt close session. "+e.getMessage());
        }
    }
    public void register(WebSocketSession session,String username,String password,boolean presence){
        XMPPTCPConnection connection=null;
       
        try{
            connection=adapter.connect(username,password,presence);
            log.info("XMPP connection created");
        }catch(IOException | InterruptedException e){
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Couldnt create XMPPConnection."+e.getMessage())
                .build());
            log.error("IO/Interrupted Exception: "+e.getMessage());
            closeSession(session); 
            return;
        }catch(ServiceRuntimeException e){
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Couldnt create XMPPConnection."+e.getMessage())
                .build());
            log.error("RuntimeException: "+e.getMessage());
            closeSession(session); 
            return;
        }catch(XMPPException | SmackException e){
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Couldnt create XMPPConnection."+e.getMessage())
                .build());
            log.error("XMPP/SmackException: "+e.getMessage());
            closeSession(session); 
            return;
        }
        
        try{
            adapter.createAccount(connection, username, password);
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.SUCCESS)
                .build());
            closeSession(session);
        }catch(IOException | InterruptedException e){
            connection.disconnect();
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Couldnt create user.")
                .build());
            closeSession(session); 
            log.error("IO/Interrupted Exception: "+e.getMessage());
            return;
        }catch(ServiceRuntimeException r){
            connection.disconnect();
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Couldnt create user.")
                .build());
            closeSession(session); 
            log.error("RuntimeException: "+r.getMessage());
            return;
        }catch(XMPPException | SmackException e){
            connection.disconnect();
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Couldnt create user.")
                .build());
            closeSession(session); 
            log.error("XMPP/SmackException: "+e.getMessage());
            return;
        }

    }
    public void connect(WebSocketSession session,String username,String password,boolean presence){
        XMPPTCPConnection connection=null;
       
        try{
            connection=adapter.connect(username,password,presence);
            log.info("XMPP connection created");
        }catch(IOException | InterruptedException e){
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Couldnt create XMPPConnection."+e.getMessage())
                .build());
            log.error("IO/Interrupted Exception: "+e.getMessage());
            closeSession(session); 
            return;
        }catch(ServiceRuntimeException e){
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Couldnt create XMPPConnection."+e.getMessage())
                .build());
            log.error("RuntimeException: "+e.getMessage());
            closeSession(session); 
            return;
        }catch(XMPPException | SmackException e){
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Couldnt create XMPPConnection."+e.getMessage())
                .build());
            log.error("XMPP/SmackException: "+e.getMessage());
            closeSession(session); 
            return;
        }

        

        try{   
            adapter.login(connection);
            log.info("Logged in successfully.");
            
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.SUCCESS)
                .build());

            log.info("started xmppsession successfully.");
            log.info("Stored connection & session");
            connections.put(session,connection);
 
        }catch(IOException | InterruptedException e){
            connection.disconnect();
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Couldnt login user.")
                .build());
            closeSession(session); 
            log.error("IO/Interrupted Exception: "+e.getMessage());
            return;
        }catch(ServiceRuntimeException r){
            connection.disconnect();
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Couldnt login user.")
                .build());
            closeSession(session); 
            log.error("RuntimeException: "+r.getMessage());
            return;
        }catch(XMPPException | SmackException e){
            connection.disconnect();
            sendMessageToSocket(session,Message.builder()
                .messageType(MessageType.ERROR)
                .messageContent("Couldnt login user.")
                .build());
            closeSession(session); 
            log.error("XMPP/SmackException: "+e.getMessage());
            return;
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
    
    public void sendMessageToSocket(WebSocketSession session,Message message){

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
