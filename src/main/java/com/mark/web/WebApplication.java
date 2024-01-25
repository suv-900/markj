package com.mark.web;

import java.sql.SQLException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//- connect to DB
//- create controllers,services
//- handle errors
//- loginuser,createuser,viewuser,deleteuser
//- createpost,updatepost,deletepost,viewpostwithouttoken
@SpringBootApplication
public class WebApplication {
	
	public static void main(String[] args) throws SQLException {
		SpringApplication.run(WebApplication.class, args);
	}

}
