package com.mark.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.mark.web.xmpp.XMPPConnect;
import com.mark.web.utils.LogFileWriter;

//- connect to DB
//- create controllers,services
//- handle errors
//- loginuser,createuser,viewuser,deleteuser
//- createpost,updatepost,deletepost,viewpostwithouttoken
@SpringBootApplication
public class WebApplication {
	private static LogFileWriter logwriter=LogFileWriter.getInstance();
    private static final Logger log=LogManager.getLogger(); 

	public static void main(String[] args)throws Exception{
		try{
			XMPPConnect.connect();		
		}catch(Exception e){
			log.info("Couldnt connect to XMPP server");
				
			if(logwriter != null){
				logwriter.write(e.getMessage());
			}else{
				log.info("logwriter is null getInstance() failed.");
			}
		}
		// XMPPConnect.connect();
		SpringApplication.run(WebApplication.class, args);
	}

	// @Bean
	// public WebMvcConfigurer corsConfigurer(){
	// 	return new WebMvcConfigurer(){
	// 		@Override
	// 		public void addCorsMappings(CorsRegistry registry) {
	// 			registry.addMapping("/").allowedOrigins("http://localhost:3000");
	// 		}
	// 	};
	// }

}
