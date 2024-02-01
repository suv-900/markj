package com.mark.web.services;

import java.sql.SQLException;


public interface UserService {
   public void registerUser(String username,String email,String password)throws SQLException;
   public boolean loginUser(String username,String password)throws SQLException;
   // public void update(User user) throws SQLException;
   public void getUser(int id) throws SQLException;
   public void deleteUser(int id)throws SQLException;
   public boolean checkUsername(String username)throws SQLException;
   public void reportUser(String username,int id); 
}
