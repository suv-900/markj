package com.mark.web.websocket.wsmessage;

public enum MessageType {
    CONNECT,
    DISCONNECT,
    MESSAGE,
    JOIN_SUCCESS, 
    LEAVE, 
    ERROR, 
    FORBIDDEN, 
    ADD_CONTACT, 
    GET_CONTACTS, 
    REMOVE_CONTACT,
    SUCCESS,
    LOGIN,
    REGISTER 
}
