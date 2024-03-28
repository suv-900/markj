package com.mark.web.utils;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//single instacne and with lock

//writes log messages to a file
public class LogFileWriter {
    private static LogFileWriter logWriter;
    
    private String fileName="logs.txt";
    private Logger log=LogManager.getLogger(); 
    private DateTimeFormatter dtf=DateTimeFormatter.ofPattern("HH:mm:ss");    
    private Path path;

    private LogFileWriter(){
        path=Paths.get(fileName);
    }
    // static{
    //     logWriter=new LogFileWriter();
    //     path=Paths.get(fileName);

    //     System.out.println("path "+path);
    //     if(path == null){
    //         log.info("LogFile path is null abort.");
    //     }
    // }

    public static LogFileWriter getInstance(){
        if(logWriter == null){
            logWriter=new LogFileWriter();
        }
        return logWriter;
    }

    public synchronized void write(String message){
        LocalDateTime now=LocalDateTime.now();
        String time=dtf.format(now);
        String total=time+" "+message+"\n";
        
        try{
            Files.write(path,total.getBytes(),StandardOpenOption.APPEND);
        }catch(Exception e){
            System.out.println("Error occured while writing to log file.");
        }
        
    }
}
