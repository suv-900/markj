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
    private static final UtillityServiceImplementation utils=new UtillityServiceImplementation();
    private static final Logger log=LogManager.getLogger(); 
    private static BasicDataSource datasource=DatabaseConnections.getDataSource();
    
    public UserServiceImplementation(){}
      
    public void registerUser(String username,String password,String email)throws SQLException{
            Connection con=datasource.getConnection();
            System.out.println("Username: "+username);
            System.out.println("Password: "+password);
            System.out.println("Email: "+email);
            
            String query="insert into users(username,password,email) VALUES(?,?,?)";
            PreparedStatement ps=con.prepareStatement(query);
            ps.setString(1,username);
            ps.setString(2,password);
            ps.setString(3,email);
            
            ps.executeUpdate();
            
            ps.close();
            con.close();
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

    public boolean loginUser(String username,String rawPassword)throws SQLException{
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

            boolean isEqual=utils.validatePassword(rawPassword, dbPassword);
            return isEqual;

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
