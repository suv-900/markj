package com.mark.web.services.serviceImplementation;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import com.mark.web.services.LoggingService;

public class LoggingServiceImplementation implements LoggingService{
    @Autowired
    private Logger logger; 

    public Logger getLogger(){
        return this.logger;
    }
}
