package com.mark.web.services.serviceImplementation;

import java.util.Date;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mark.web.services.TokenService;

public class TokenServiceImplementation implements TokenService{
    private static Logger log=LogManager.getLogger();
    private static String serverSecret="abhilasha";
    private static JWTVerifier verifier=null; 
    private static Algorithm algorithm=null;

    static{
        algorithm=Algorithm.HMAC256(serverSecret.getBytes());
        verifier=JWT.require(algorithm).build();
    }

    public String createToken(int user_id){
       
        String token=JWT.create()
            .withIssuer("mark")
            .withClaim("user_id",user_id)
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis()+1800000))
            .withJWTId(UUID.randomUUID().toString())
            // .withNotBefore(new Date(System.currentTimeMillis()+1000L))
            .sign(algorithm);

        return token;
    }

    public int verifyToken(String token)throws JWTVerificationException,NumberFormatException{
        try{
            DecodedJWT decodedJWT=verifier.verify(token);
            String s=decodedJWT.getClaim("user_id").toString();
            int userid=Integer.parseInt(s);
            return userid;
        }catch(JWTVerificationException e){
            throw e;
        }catch(NumberFormatException e){
            throw e;
        }
    }
}
