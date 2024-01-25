package com.mark.web.db;

// import java.util.Properties;


import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.SQLException;

@Component
public class DatabaseConnections {
    // public ConnectDB(){}
    //race condition
  
    //see below
    // public static PoolingDataSource  poolingDataSource; 

    public static Connection connection=null;
    private static Logger logger=LogManager.getLogger(); 
    public static BasicDataSource dataSource;    
    
    // @Autowired
    // private static Environment env;  

    //  jdbc:postgres://localhost/mark-db core 12345678
    public DatabaseConnections(){}

    static {
 
        logger.info("Setting DataBase Properties."); 
        
        dataSource=new BasicDataSource();
        // dataSource.setUrl(env.getProperty("spring.datasource.url"));
        // dataSource.setUsername(env.getProperty("spring.datasource.username"));
        // dataSource.setPassword(env.getProperty("spring.datasource.password"));
        
        dataSource.setUrl("jdbc:postgres://localhost/mark-db");
        dataSource.setUsername("core");
        dataSource.setPassword("12345678");
        dataSource.setDriverClassName("org.postgres.Driver"); 
        dataSource.setMaxTotal(2);//-1 for no limit
        dataSource.setMinIdle(2);
        dataSource.setMaxIdle(2);
        
        logger.info("Database operation set.");       
        
        //-->reserved for rare needs
        
        //object pool
        // GenericObjectPool connectionPool=new GenericObjectPool(null);
       
        // //final vars
        // final String databaseURL=env.getProperty("spring.datasource.url");
        // final String username=env.getProperty("spring.datasource.username");
        // final String password=env.getProperty("spring.datasource.password");

        // //final args
        // //creating connections
        // ConnectionFactory connectionFactory=new DriverManagerConnectionFactory(databaseURL,username,password);
      
        // //idk javax.management.ObjectName
        // PoolableConnectionFactory poolableConnectionFactory=new PoolableConnectionFactory(connectionFactory,connectionPool);
        // poolingDataSource=new PoolingDataSource(connectionPool); 


    }
    
    public static BasicDataSource getDataSource(){
        return dataSource;
    }

    // public static boolean connectDB() throws SQLException{
    //     String databaseURL="jdbc:postgresql://localhost/mark-db";
    //     Properties prop=new Properties();
    //     prop.setProperty("user","core");
    //     prop.setProperty("password","12345678");
        
    //     boolean isValidConnection=false;
    //     try(Connection conn=DriverManager.getConnection(databaseURL,prop)){
    //        isValidConnection=conn.isValid(0);
    //        if(isValidConnection){ 
    //         connection=conn;  
    //     } 
    //     }
    //     return isValidConnection;
    // }
    
    // public static Connection getConnection(){
    //     System.out.println("requesting for DBconnection");
    //     System.out.println("connection status: "+connection);
    //     if(connection!=null){
    //         return connection;
    //     }else{
    //         return null;
    //     }
    // }

    // public Connection getNewConnection()throws SQLException{
    //     String databaseURL="jdbc:postgresql://localhost/mark-db";
    //     Properties prop=new Properties();
    //     prop.setProperty("user","core");
    //     prop.setProperty("password","12345678");
    //     Connection con=null;
        
    //     //JNI
    //     //Pool 

    //     DriverManager.setLoginTimeout(2);
    //     boolean validConnection=false;
    //     try(Connection c=DriverManager.getConnection(databaseURL,prop)){
    //         validConnection=c.isValid(0);
    //         if(validConnection){
    //             System.out.println("connecion is valid");
    //            con=c; 
    //         }else{
    //             System.out.println("invalid connection");
    //         }
    //     }catch(SQLException e){
    //         e.printStackTrace();
    //         return null;
    //     }
    //     return con;
    // }
    //connection pool
    //each db operation uses one of the connection from the connection pool 
    // public static DataSource createDataSource(){

    // }
}
