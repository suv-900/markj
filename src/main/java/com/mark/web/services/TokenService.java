package com.mark.web.services;

import com.auth0.jwt.exceptions.JWTVerificationException;

public interface TokenService {
   public String createToken(int user_id);
   public int verifyToken(String token)throws JWTVerificationException,NumberFormatException; 
}
