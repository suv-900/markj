package com.mark.web.services.serviceImplementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mark.web.db.DatabaseConnections;
import com.mark.web.services.UserService;

public class UserServiceImplementation implements UserService {
    // static Connection connection= get connection from the pool
    // private static PrintWriter writer=new PrintWriter(); 
   
    private static final Logger log=LogManager.getLogger(); 
    private BasicDataSource datasource=DatabaseConnections.getDataSource();

    public UserServiceImplementation(){
        log.info("datasource object: "+datasource);
    }
    
    public void add(String username,String password,String email)throws SQLException{
        //check if user exists
        Connection con=null;
        try{
            con=datasource.getConnection();
        }catch(SQLException e){
            e.printStackTrace();
        }

        String query="insert into users(username,password,email) VALUES(?,?,?,?)";
        PreparedStatement ps=con.prepareStatement(query);
        ps.setString(1,username);
        ps.setString(2,password);
        ps.setString(3,email);
        
        int rowsAffected=ps.executeUpdate();
        System.out.println("rowsaffected: "+rowsAffected);
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
    public void reportUser(String username,int userID){

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

    public void loginUser(String username,String rawPassword)throws SQLException{
        //raw password compare to encrypted password
        // Connection con =cd.getNewConnection();
        // if(con==null) return; 
        try{
            Connection con=datasource.getConnection();
            String query1="select (username,password) from users where username=?";
            PreparedStatement ps=con.prepareStatement(query1);
            ps.setString(1,username);
            ResultSet rs=ps.executeQuery();
            //LOOK
            System.out.println(rs);
            // User user
        
            //get encrypted pass 
            //comparePassword();
        }catch(SQLException e){
            e.printStackTrace();
        } 
    }

    public void checkUsername(String username){
       
        try{
            // Connection con =cd.getNewConnection();
            // if(con==null) return;
            Connection con=datasource.getConnection();

            log.info("Got a connection[checkUsername]");

            String query="select count(*) from users where username=?";
            PreparedStatement ps=con.prepareStatement(query);

            System.out.println("ps: "+ps); 
            ps.setString(1, username);
            //false=no result,
            boolean exists=ps.execute();
            System.out.println("result: "+exists); 
        }catch(SQLException s){
            s.printStackTrace();
            // throw s;
        }
        // return exists;
    }
}
