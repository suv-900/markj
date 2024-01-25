package com.mark.web.services;

import java.sql.SQLException;


public interface UserService {
   public void loginUser(String username,String password)throws SQLException;
   // public void update(User user) throws SQLException;
   public void getUser(int id) throws SQLException;
   public void deleteUser(int id)throws SQLException;
   public void checkUsername(String username);
   public void reportUser(String username,int id); 
}
