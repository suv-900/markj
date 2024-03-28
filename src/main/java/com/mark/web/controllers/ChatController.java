package com.mark.web.controllers;

//simple architecture
//-authenticate users,establish connections,send messages+store in db for further retrival.
//-destroy connection when one user disconnects,store incoming messages in db.
//-notify about the messages when the user comes online again.

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import com.mark.web.services.serviceImplementation.TokenServiceImplementation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.ServerEndpoint;

//user can get into a chat
//send message and recive message
//authenticate users requests and establish a connection for both users.
//store in db
//keep sending messages if one user is not active store in their local db[might cause flooding]
//destroy connection,store messages in db,when the other user comes online send the unread messages
//a middleware that acts as a server and handles message content
//a db to store messages
//one way connection for real time message delivery

@ServerEndpoint(value="/chat/{}/{}")
public class ChatController {
    //notify
    
    private static TokenServiceImplementation tokenService=new TokenServiceImplementation();    
    private static final Logger log=LogManager.getLogger(); 

   
}
