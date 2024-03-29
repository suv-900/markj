package com.mark.web.services;

import java.sql.SQLException;
import java.util.List;

import com.mark.web.exceptions.UserNotFoundException;
import com.mark.web.models.Friend;
import com.mark.web.models.FriendRequest;
import com.mark.web.models.User;


public interface UserService {
   public int registerUser(String username,String email,String password)throws Exception;
   public List<String> loginUser(String username)throws Exception, UserNotFoundException;
   public boolean comparePassword(String dbPassword,String password);
   // public void update(User user) throws SQLException;
   // public User getUser(int id) throws Exception;
   public Friend getOneFriend(int id) throws Exception;
   public void deleteUser(int id)throws SQLException;
   public boolean checkUsername(String username)throws SQLException;
   public void addImage(int user_id,String image_description,String imageHexString)throws SQLException;
   // public void sendFriendRequest(String username)throws Exception;
   public List<List<String>> getImages(int user_id)throws Exception;
   public List<FriendRequest> getFriendRequests(int userid)throws Exception; 
   public int getOnlineMembers()throws SQLException;
   public void acceptFriendRequest(String username,int ownerID)throws Exception;
   public void storeMessages(int user1ID,int user2ID,String message)throws Exception;
   public int getUserID(String username)throws Exception;
}
