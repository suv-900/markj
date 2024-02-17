package com.mark.web.services.serviceImplementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mark.web.db.DatabaseConnections;
import com.mark.web.models.Friend;
import com.mark.web.models.FriendRequest;
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
       public List<String> loginUser(String username)throws Exception{
        //raw password compare to encrypted password
            Connection con=datasource.getConnection();
            
            String query1="select user_id,password from users where username=? ";
            
            PreparedStatement ps=con.prepareStatement(query1);
            ps.setString(1,username);
            
          
            ResultSet rs=ps.executeQuery();
            rs.next();
           
            List<String> list=new LinkedList<String>();
            String dbPassword=(String)rs.getObject("password");
            int userid=(int)rs.getObject("user_id");
            
            rs.close();
            ps.close();
            con.close();


            list.add(dbPassword);
            list.add(Integer.toString(userid));
            return list;
            // String decryptedText=utils.decrypt(dbPassword);
            // System.out.println("DecryptedText: "+decryptedText);


    }


    public boolean comparePassword(String dbPassword,String password){

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

    // public void getFriendRequests(int user_id)throws Exception{
    //     Connection con=datasource.getConnection();
        
    //     String query="select (request_status,from_user_id,created_at) from friendRequests where to_user_id=?";
    //     PreparedStatement ps=con.prepareStatement(query);
    //     ps.setInt(1,user_id);
    //     ResultSet rs=ps.executeQuery();
    //     ps.close();
    //     con.close();

    //     rs.close();
    //     System.out.println("getRequests ResultSet: "+rs.toString()); 
    // }

    public boolean checkUsername(String username)throws SQLException{
        boolean usernameExists=false;
        Connection con=datasource.getConnection();
        String query="select * from users where username=?"; 
        
        PreparedStatement ps=con.prepareStatement(query);
        ps.setString(1,username);

        ResultSet rs=ps.executeQuery();
        usernameExists=rs.next();
        rs.close();
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

    public List<List<String>> getImages(int user_id)throws Exception{
        String query="select image,image_description from images where user_id=?";
        Connection con=datasource.getConnection();
        PreparedStatement ps=con.prepareStatement(query);
        ps.setInt(1,user_id);

        ResultSet rs=ps.executeQuery();
        
        List<String> imagesList=new LinkedList<String>();
        List<String> imageDescriptionList=new LinkedList<String>();

        while(rs.next()){
            String image=(String)rs.getObject("images");
            imagesList.add(image);
            String imageDescription=(String)rs.getObject("image_description");
            imageDescriptionList.add(imageDescription);
        }
        rs.close();
        ps.close();
        con.close();

        List<List<String>> allInOne=new LinkedList<List<String>>();
        allInOne.add(imagesList);
        allInOne.add(imageDescriptionList);

        return allInOne;
    }

    //write better exception handling
    public void sendFriendRequest(String username,int user1id) throws Exception{
        
        Connection con=null;
        PreparedStatement ps=null;
                //release all resources 
        try{
            
            String query="insert into friendRequests(to_user_id,from_user_id) values(?,?)";
            
            con=datasource.getConnection();
            ps=con.prepareStatement(query); 

            int user2id=getUserID(username);
            if(user2id == -1){
                throw new Exception("user2 not found");
            } 
            ps.setInt(1,user2id);
            ps.setInt(2,user1id);
            
            ps.executeUpdate();
            con.commit();
            // log.info("Rows affected: "+ra);

        }catch(Exception e){
            log.info(e);
            con.rollback();
            throw e;
        }finally{
           if( ps != null){
                ps.close();
           }
           if( con != null){
                con.close();
           } 
        }
        
        
    }

    //checked
    public List<FriendRequest> getFriendRequests(int userid)throws SQLException{
        String query1="select from_user_id,created_at,username from friendRequests fr join users u on fr.from_user_id=u.user_id where to_user_id=?";

        // String query="select from_user_id,created_at from friendRequests where to_user_id=?";
        
        
        Connection con=datasource.getConnection();
        PreparedStatement ps=con.prepareStatement(query1);
        ps.setInt(1,userid);
        
        ps.execute(); 
        
        ResultSet rs=ps.getResultSet();
        
         
        List<FriendRequest> list=new LinkedList<FriendRequest>();
        while(rs.next()){
            int from_userId=(int) rs.getObject("from_user_id");
            int sentTime=(int) rs.getObject("created_at");
            String username=(String)rs.getObject("username");

            FriendRequest frObj=new FriendRequest();
            frObj.setFromUserID(from_userId);
            frObj.setToUserID(userid);
            frObj.setTime(sentTime);
            frObj.setSenderUsername(username);
            list.add(frObj);
        }
        ps.close();
        con.close();
        
        return list;
    }

    public int getOnlineMembers()throws SQLException{
        String query="select * from onlineUsers";

        Connection con=datasource.getConnection();
        PreparedStatement ps=con.prepareStatement(query);

        ps.execute();

        ResultSet rs=ps.getResultSet();
        rs.next();
        int totalOnlineMembers=(int)rs.getObject("online");

        return totalOnlineMembers;
    }

    //refactor
    public List<Friend> getAllFriends(int userid)throws SQLException{
        // String query="select user_id,username,online,email from friends f join users u on f.users1_friend = u.user_id where f.user1 =?";
        
        String query="select getfriends(?)";
        
       
        Connection con=datasource.getConnection();
        PreparedStatement ps=con.prepareStatement(query);
        
        List<Friend> friendsList=new LinkedList<Friend>();

        try{
            ps.setInt(1,userid);
            ps.execute();
       
            ResultSet rs=ps.getResultSet();
            System.out.println("Warnings: "+rs.getWarnings());
        
            while(rs.next()){
                String username=(String)rs.getObject("username");
                String email=(String)rs.getObject("email");
                boolean userActive=(boolean)rs.getObject("online");
                int friendUserID=(int)rs.getObject("user_id");
                
                Friend f=new Friend();
                f.setUsername(username);
                f.setOnline(userActive);
                f.setEmailID(email);
                f.setUserID(friendUserID);
                friendsList.add(f);
            }

        }catch(SQLException e){
            throw e;
        }finally{
            ps.close();
            con.close();
        } 
        return friendsList;
    }
    public void acceptFriendRequest(String username1,int user2id)throws Exception{

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

    public int getUserID(String username)throws Exception{
        int userid=-1;
        
        String query="select user_id from users where username=?";
        Connection con=datasource.getConnection();
        PreparedStatement ps=con.prepareStatement(query);
       
        try{
            ps.setString(1,username);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                userid=(int)rs.getObject("user_id");
            }

        }catch(Exception e){
            log.info(e);
            throw e;
        }finally{
            ps.close();
            con.close();
        }

        return userid;
    
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
