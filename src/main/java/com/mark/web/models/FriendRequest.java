package com.mark.web.models;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendRequest {
   private int toUserID;
   private int fromUserID;
   private Timestamp createdAt; 
   private String senderUsername;
   
}
