package com.mark.web.services.serviceImplementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mark.web.db.DatabaseConnections;
import com.mark.web.exceptions.UserNotFoundException;
import com.mark.web.models.Friend;
import com.mark.web.models.FriendRequest;
import com.mark.web.models.User;
import com.mark.web.services.UserService;

public class UserServiceImplementation implements UserService {
    // static Connection connection= get connection from the pool
    // private static PrintWriter writer=new PrintWriter(); 
    private static final Logger log=LogManager.getLogger(); 
    private static BasicDataSource datasource=DatabaseConnections.getDataSource();
    
    public UserServiceImplementation(){}
      
    public int registerUser(String username,String password,String email)throws Exception{
            // String hashedPass=utils.encrypt(password); 
            
            Connection con=null;
            PreparedStatement ps=null;

            try{
                String query="insert into users(username,password,email) values(?,?,?) returning user_id";
                
                con=datasource.getConnection();
                ps=con.prepareStatement(query);
                
                ps.setString(1,username);
                ps.setString(2,password);
                ps.setString(3,email);
                
                ResultSet rs=ps.executeQuery(); 
                rs.next();
                int userid=(int)rs.getObject("user_id");
                return userid;
            }catch(Exception e){
                log.info(e);
                throw e;
            }finally{
                if(ps != null){
                    ps.close();
                }
                if(con != null){
                    con.close();
                }
            }
            

            // ps=con.prepareStatement(query1);
            // ps.setString(1,userFriendRequests);
            // System.out.println("PreparedStatment: "+ps.toString()); 
            // int rowsAffected=ps.executeUpdate(); 
            
            
    }
       public List<String> loginUser(String username)throws Exception, UserNotFoundException{
        //raw password compare to encrypted password

            Connection con=null;
            PreparedStatement ps=null;


            List<String> list=new LinkedList<String>();
            try{
                String query1="select user_id,password from users where username=? ";
                con=datasource.getConnection();
                 
                
                ps=con.prepareStatement(query1);
                ps.setString(1,username);
          
                ResultSet rs=ps.executeQuery();
                
                if(!rs.next()){
                    throw new UserNotFoundException();
                }

                String dbPassword=(String)rs.getObject("password");
                int userid=(int)rs.getObject("user_id");
                list.add(dbPassword);
                list.add(Integer.toString(userid));

                return list;

            }catch(Exception e){
                throw e;
            }finally{
                if(ps != null){
                    ps.close();
                }
                if(con != null){
                    con.close();
                }
            }

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

        Connection con=null;
        PreparedStatement ps=null;
        
        try{
            String query="select image,image_description from images where user_id=?";
            con=datasource.getConnection();
            ps=con.prepareStatement(query);
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

            List<List<String>> allInOne=new LinkedList<List<String>>();
            allInOne.add(imagesList);
            allInOne.add(imageDescriptionList);

            return allInOne;

        }catch(Exception e){
            log.info(e);
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

    //write better exception handling
    public void sendFriendRequest(String username,int fromUserID) throws Exception{
        
        Connection con=null;
        PreparedStatement ps=null;
                //release all resources 
        try{
            int toUserID=getUserID(username);
            if(toUserID == -1){
                throw new Exception("to_user_id not found");
            }
            String query1="select * from friendRequests where to_user_id=? and from_user_id=?";
            con=datasource.getConnection();
            ps=con.prepareStatement(query1);
            ps.setInt(1,toUserID);
            ps.setInt(2,fromUserID);
    
            ResultSet rs=ps.executeQuery();

            if(rs.next()){
                //request exists
                throw new Exception("Request already exists.");
            }
            ps.close();

            String query2="insert into friendRequests(to_user_id,from_user_id) values(?,?)";
            
            ps=con.prepareStatement(query2); 

            ps.setInt(1,toUserID);
            ps.setInt(2,fromUserID);
            
            ps.executeUpdate();

        }catch(Exception e){
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


        // String query="select from_user_id,created_at from friendRequests where to_user_id=?";
        Connection con=null;
        PreparedStatement ps=null;
        List<FriendRequest> list=new LinkedList<FriendRequest>();

        try{
            String query1="select fr.from_user_id,u.created_at,u.username from friendRequests fr join users u on fr.from_user_id=u.user_id where to_user_id=?";
            
            con=datasource.getConnection();
            ps=con.prepareStatement(query1);
            ps.setInt(1,userid);
        
            ps.execute(); 
        
            ResultSet rs=ps.getResultSet();
        
         
            while(rs.next()){
                int from_userId=(int) rs.getObject("from_user_id");
                Timestamp sentTime=(Timestamp) rs.getObject("created_at");
                String username=(String)rs.getObject("username");

                FriendRequest frObj=new FriendRequest();
                frObj.setFromUserID(from_userId);
                frObj.setToUserID(userid);
                frObj.setCreatedAt(sentTime);
                frObj.setSenderUsername(username);
                list.add(frObj);
            }

        }catch(Exception e){
            throw e;
        }finally{
            if(ps != null){
                ps.close();
            }
            if(con != null){
                con.close();
            }
        }
        
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

    //refactor:postgres function
    public List<Friend> getAllFriends(int userid)throws SQLException{
        String query1="select user_id,username,online,email from friends f join users u on f.user2id = u.user_id where f.user1id =?";
        String query2="select user_id,username,online,email from friends f join users u on f.user1id = u.user_id where f.user2id =?"; 
        // String query="select getfriends(?)";
        
        Connection con=null;
        PreparedStatement ps=null;

        List<Friend> friendsList=new LinkedList<Friend>();

        try{
            con=datasource.getConnection();
            ps=con.prepareStatement(query1);

            ps.setInt(1,userid);
            ps.execute();
       
            ResultSet rs=ps.getResultSet();
        
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
            rs.close();
            ps.close();

            ps=con.prepareStatement(query2);
            ps.setInt(1,userid);

            ps.execute();
       
            rs=ps.getResultSet();
            
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
            if( ps != null){
                ps.close();
            }
            if(con != null){
                con.close();
            }
        } 
        return friendsList;
    }
    public void acceptFriendRequest(String username,int ownerID)throws Exception{
        Connection con=null;
        PreparedStatement ps=null;

        try{
            int fromUserID=getUserID(username);
            if(fromUserID == -1){
                throw new Exception("fromUser not found");
            }

            String query3="select * from friends where user1ID=? and user2ID=?";
            con=datasource.getConnection();
            ps=con.prepareStatement(query3);
            ps.setInt(1,ownerID);
            ps.setInt(2,fromUserID);

            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                throw new Exception("Already friends.");
            }
            rs.close();
            ps.close();

            String query4="select * from friends where user1ID=? and user2ID=?";
            ps=con.prepareStatement(query4);
            ps.setInt(1,fromUserID);
            ps.setInt(2,ownerID);

            rs=ps.executeQuery();
            if(rs.next()){
                throw new Exception("Already friends.");
            }
            rs.close();
            ps.close();
            

            String query1="delete from friendRequests where from_user_id=?"; 
            ps=con.prepareStatement(query1);
            ps.setInt(1,fromUserID);
            ps.executeUpdate();

            ps.close();

            String query2="insert into friends(user1ID,user2ID) values(?,?)";
            ps=con.prepareStatement(query2);
            ps.setInt(1,ownerID);
            ps.setInt(2,fromUserID);
            ps.executeUpdate();

            String query5="select username,email,online from users where user_id=?";
            ps=con.prepareStatement(query5);
            ps.setInt(1,fromUserID);

            rs=ps.executeQuery();
            rs.next();

            Friend friend =new Friend();
            String usernameF=(String) rs.getObject("username");
            String email=(String)rs.getObject("email");
            boolean online=(boolean)rs.getObject("online");
            
            friend.setUsername(usernameF);
            friend.setEmailID(email);
            friend.setOnline(online);
            friend.setUserID(fromUserID);
            
        }catch(Exception e){
            throw e;
        }finally{
            if(ps != null){
                ps.close();
            }
            if(con != null){
                con.close();
            }
        }
    }

    public void denyFriendRequest(int fromUserID,int toUserID)throws Exception{
        Connection con=null;
        PreparedStatement ps=null;

        try{
            String query="delete from friendRequests where from_user_id=? and to_user_id=?";
            con=datasource.getConnection();
            ps=con.prepareStatement(query);
            ps.setInt(1,fromUserID);
            ps.setInt(2,toUserID);

            ps.executeUpdate();
        }catch(Exception e){
            throw e;
        }finally{
            if(ps != null){
                ps.close();
            }
            if(con != null){
                con.close();
            }
        }
    }

    public Friend getFriend(int userID)throws Exception{
        Connection con=null;
        PreparedStatement ps=null;

        try{
            String query="select username,email,online from users where user_id=?";
            con=datasource.getConnection();
            ps=con.prepareStatement(query);
            ps.setInt(1,userID);

            ResultSet rs=ps.executeQuery();
            rs.next();

            Friend friend=new Friend();
            String usernameF=(String) rs.getObject("username");
            String email=(String)rs.getObject("email");
            boolean online=(boolean)rs.getObject("online");
            
            friend.setUsername(usernameF);
            friend.setEmailID(email);
            friend.setOnline(online);
            friend.setUserID(userID);
            
            return friend;
        
        }catch(Exception e){
            throw e;
        }finally{
            if(ps != null){
                ps.close();
            }
            if(con != null){
                con.close();
            }
        }
           
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
    
    // public void getUser(int id)throws SQLException{
    //     try{
    //         Connection con=datasource.getConnection();
    //         String query="select (username,online) from users where userID=?";
    //         PreparedStatement ps=con.prepareStatement(query);
    //         ps.setString(1,Integer.toString(id));
    //         ResultSet rs=ps.executeQuery();
    //         System.out.println(rs);
    //     }catch(SQLException e){
    //         e.printStackTrace();
    //     }
    // }

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
