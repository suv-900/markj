package com.mark.web.services.serviceImplementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mark.web.db.DatabaseConnections;
import com.mark.web.services.UserService;

public class UserServiceImplementation implements UserService {
    // static Connection connection= get connection from the pool
    // private static PrintWriter writer=new PrintWriter(); 
    private static final PasswordCodec utils=new PasswordCodec();
    private static final Logger log=LogManager.getLogger(); 
    private static BasicDataSource datasource=DatabaseConnections.getDataSource();
    
    public UserServiceImplementation(){}
      
    public int registerUser(String username,String password,String email)throws Exception{
            // String hashedPass=utils.encrypt(password); 
            
            Connection con=datasource.getConnection();
            // String query1="create table ?(request_id smallserial primarykey,requestuser_id smallserial references users(user_id),created_at timestamp defualt current_timestamp,pending boolean not null);"; 
            String query="insert into users(username,password,email) values(?,?,?) returning user_id";
            String userFriendRequests=username+"FriendRequests";
            PreparedStatement ps=con.prepareStatement(query);
            ps.setString(1,username);
            ps.setString(2,password);
            ps.setString(3,email);
            ResultSet rs=ps.executeQuery(); 
            // int rs=ps.executeUpdate();
            rs.next();
            int user_id=(int)rs.getObject("user_id");
            // ps=null;
            // ps=con.prepareStatement(query1);
            // ps.setString(1,userFriendRequests);
            // System.out.println("PreparedStatment: "+ps.toString()); 
            // int rowsAffected=ps.executeUpdate(); 
            
            
            ps.close();
            con.close();
            
            return user_id;
    }
       public boolean loginUser(String username,String password)throws Exception{
        //raw password compare to encrypted password
        // Connection con =cd.getNewConnection();
        // if(con==null) return; 
            Connection con=datasource.getConnection();
            
            String query1="select password from users where username=?";
            PreparedStatement ps=con.prepareStatement(query1);
            ps.setString(1,username);
            
            ResultSet rs=ps.executeQuery();
            rs.next();
            String dbPassword=(String)rs.getObject("password");
            
            ps.close();
            con.close();

            // String decryptedText=utils.decrypt(dbPassword);
            // System.out.println("DecryptedText: "+decryptedText);

            if(password.length()!=dbPassword.length()){
                return false;
            }
            char[] passwordChar=password.toCharArray();
            char[] decryptedTextChar=dbPassword.toCharArray();

            for(int i=0;i<password.length();i++){
                if(passwordChar[i]!=decryptedTextChar[i]){
                    return false;
                }
            }
            return true;

    }
    public void getFriendRequests(int user_id)throws Exception{
        Connection con=datasource.getConnection();
        
        String query="select (request_status,from_user_id,created_at) from friendRequests where to_user_id=?";
        PreparedStatement ps=con.prepareStatement(query);
        ps.setInt(1,user_id);
        ResultSet rs=ps.executeQuery();
        ps.close();
        con.close();
        System.out.println("getRequests ResultSet: "+rs.toString()); 
    }

    public boolean checkUsername(String username)throws SQLException{
        boolean usernameExists=false;
        Connection con=datasource.getConnection();
        String query="select * from users where username=?"; 
        
        PreparedStatement ps=con.prepareStatement(query);
        ps.setString(1,username);

        ResultSet rs=ps.executeQuery();
        usernameExists=rs.next();
        ps.close();
        con.close();
        return usernameExists;
    }

    public void addImage(int user_id,String image_description,String imageHexString)throws SQLException{
        String query="insert into images(image,image_description,user_id) values(?,?,?)";
        Connection con=datasource.getConnection();
        PreparedStatement ps=con.prepareStatement(query);
        ps.setString(1,imageHexString);
        ps.setString(2,image_description);
        ps.setInt(3,user_id);

        ps.executeQuery();

        ps.close();
        con.close();
            
    }
    public void sendFriendRequest(String username){

    }

    public void deleteUser(int id)throws SQLException{
        try{
            Connection con=datasource.getConnection();
            String query1="select count(*) from users where userID=?";
            PreparedStatement ps=con.prepareStatement(query1);
            String userID=Integer.toString(id);
            ps.setString(1,userID);
            ResultSet rs=ps.executeQuery();
            System.out.println("resultSet: "+rs); 
        // return;
        // boolean userFound=rs.getRow();
        }catch(SQLException e){
            e.printStackTrace();
        }


    }
    
    public void getUser(int id)throws SQLException{
        try{
            Connection con=datasource.getConnection();
            String query="select (username,online) from users where userID=?";
            PreparedStatement ps=con.prepareStatement(query);
            ps.setString(1,Integer.toString(id));
            ResultSet rs=ps.executeQuery();
            System.out.println(rs);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public int getUserID(String username){
        int user_id=-1;
        
        try{
            Connection con=datasource.getConnection();
            String query="select user_id from users where username=?";
            PreparedStatement ps=con.prepareStatement(query);
            ps.setString(1,username);
            ResultSet rs=ps.executeQuery();
            rs.next();
            user_id=(int)rs.getObject("user_id");
            return user_id;
        }catch(Exception e){
            log.info(e);
        }
        
        return user_id;

    }
 
    public void searchUser(){

    }
    
    public void sendFriendRequest(){

    }

    public void removeFriend(){

    }

    public void askUserOnline(){

    } 

    public void updateProfilePicture(){

    }
    public void reportUser(String username,int userID){

    }
}
