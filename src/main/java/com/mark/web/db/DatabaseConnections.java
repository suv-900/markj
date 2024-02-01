package com.mark.web.db;


import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.stereotype.Component;



import java.sql.Connection;


@Component
public class DatabaseConnections {
   
    public static Connection connection=null;
    
    public static BasicDataSource dataSource;    
   
    private static String dbURL="jdbc:postgresql:mark-db";
    private static String dbUser="core";
    private static String dbPassword="12345678";

    public DatabaseConnections(){
    }

    static {
         
        //apparently "jdbc:postgresql://localhost:5432/mark-db" doesnt work
        
        dataSource=new BasicDataSource();
        dataSource.setUrl(dbURL);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPassword);
        
        dataSource.setMaxTotal(5);//-1 for no limit
        dataSource.setMinIdle(2);
        dataSource.setMaxIdle(3);
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

   
}
