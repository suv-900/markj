package com.mark.web.db;

// import java.util.Properties;

// import org.postgresql.Driver;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.DriverConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import org.postgresql.Driver;


import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DatabaseConnections {
    // public ConnectDB(){}
    //race condition
  
    //see below
    // public static PoolingDataSource  poolingDataSource; 

    public static Connection connection=null;
    private static Logger logger=LogManager.getLogger(); 
    public static BasicDataSource dataSource;    
    private static String dbURL="jdbc:postgresql:mark-db";
    private static String dbUser="core";
    private static String dbPassword="12345678";

    // @Autowired
    // private static Environment env;  

    //  jdbc:postgres://localhost/mark-db core 12345678
    public DatabaseConnections(){
    }

    static {
      
        //check driver on classpath
        try{
            
            System.out.println(DriverManager.getDriver("jdbc:postgresql:mark-db"));        
            System.out.println(Class.forName("org.postgresql.Driver").toString());
            System.out.println("Driver on classpath");
        }catch(ClassNotFoundException e){
            System.out.println("Class not found");
            e.printStackTrace();
        }catch(SQLException e){
            e.printStackTrace();
        }

        
        //read from application property files
        // dataSource.setUrl(env.getProperty("spring.datasource.url"));
        // dataSource.setUsername(env.getProperty("spring.datasource.username"));
        // dataSource.setPassword(env.getProperty("spring.datasource.password"));
        // Driver driver=org.postgresql.Driver; 
        
        
        //apparently "jdbc:postgresql://localhost:5432/mark-db" doesnt work
        
        dataSource=new BasicDataSource();
        dataSource.setUrl(dbURL);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPassword);
        // dataSource.setDriverClassName("org.postgresql.Driver");
        // dataSource.setDriver(new Driver());
        
        

        dataSource.setMaxTotal(2);//-1 for no limit
        dataSource.setMinIdle(2);
        dataSource.setMaxIdle(2);

        System.out.println("Driver to use: "+dataSource.getMaxTotal());
        System.out.println("DriverClassName: "+dataSource.getMinIdle());
        System.out.println("DriverClassLoader: "+dataSource.getMaxIdle());



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
    
    public static void ping(){
        // String dbURL="jdbc:postgres://localhost:5422/mark-db";
        String dbURL="jdbc:postgresql:mark-db";
        String dbUser="core";
        String dbPassword="12345678";

        try{
            Class.forName("org.postgresql.Driver");
            DriverManager.getDriver(dbURL);
            Connection con=DriverManager.getConnection(dbURL,dbUser,dbPassword);
        }catch(SQLException e){
            e.printStackTrace();
        }catch(ClassNotFoundException c){
            c.printStackTrace();
        }
        
    }
    
    public static BasicDataSource getDataSource(){
        return dataSource;
    }

    public static Connection getConnection(){
        try{
            connection=DriverManager.getConnection(dbURL,dbUser,dbPassword);

        }catch(SQLException e){
            e.printStackTrace();
        }
        return connection;
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
