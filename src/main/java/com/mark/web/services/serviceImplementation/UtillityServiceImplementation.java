package com.mark.web.services.serviceImplementation;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.mark.web.services.UtillityServices;

public class UtillityServiceImplementation implements UtillityServices{
    
    private static final Logger logger=LogManager.getLogger();
    private static final HexUtilServiceImplementation hexUtils=new HexUtilServiceImplementation();

    private static final int iterations=1000; 
    private static final int keyLength=128;
    //PBE-password based encryption generates
    //password based key by looking at the bits of each character


    public String generateHashedPassword(String rawPassword){
        char[] charArray=rawPassword.toCharArray();
        SecureRandom random=new SecureRandom();
        byte[] salt=new byte[16];
        random.nextBytes(salt);

        
        KeySpec specs=new PBEKeySpec(charArray,salt,iterations,keyLength);
        //bug
        byte[] hashedPasswordBytes=null;
        try{
            SecretKeyFactory factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hashedPasswordBytes=factory.generateSecret(specs).getEncoded();
        }catch(NoSuchAlgorithmException n){
            logger.error("cryptographic algorithm is not availaible in the env.");
            n.printStackTrace();
        }catch(InvalidKeySpecException i){
            logger.error("invalid key specifications.");
            i.printStackTrace();
        }

        String finalString=null;
        finalString=iterations+":"+hexUtils.byteToHexString(salt)+":"+hexUtils.byteToHexString(hashedPasswordBytes);

        return finalString;
    } 

    

    // private String generateHashFromKnownSalt(byte[] salt,String loginPassword){
    //     byte[] hash=null;
    //     try{
    //         KeySpec specs=new PBEKeySpec(loginPassword.toCharArray(),salt,iterations,keyLength);
    //         SecretKeyFactory factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    //         hash=factory.generateSecret(specs).getEncoded();
    //     }catch(NoSuchAlgorithmException e){
    //         logger.error("cryptographic algorithm is not availaible in the env.");
    //         e.printStackTrace();
    //     }catch(InvalidKeySpecException i){
    //         logger.error("invalid key specifications.");
    //         i.printStackTrace();
    //     }
    //     return hexUtils.byteToHexString(hash);
    // }
    
    public boolean validatePassword(String loginPassword,String storedPassword){
        System.out.println("loginPassword: "+loginPassword+"\nstoredPassword:"+storedPassword);
        
        String[] parts=storedPassword.split(":");
        int storedIteration=Integer.parseInt(parts[0]);
        byte[] storedSalt=hexUtils.byteFromHex(parts[1]);
        char[] storedHash=parts[2].toCharArray();
        
        int keylen=UtillityServiceImplementation.keyLength; 
        System.out.println("keyLength: "+keylen);
        byte[] hash=null;
        try{
            //refactor the type(UtillityServiceImplemenetation.keyLength)
            KeySpec specs=new PBEKeySpec(loginPassword.toCharArray(),storedSalt,storedIteration,keylen);
            SecretKeyFactory factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hash=factory.generateSecret(specs).getEncoded();
        }catch(NoSuchAlgorithmException e){
            logger.error("cryptographic algorithm is not availaible in the env.");
            e.printStackTrace();
        }catch(InvalidKeySpecException i){
            logger.error("invalid key specifications.");
            i.printStackTrace();
        }

        char[] newHashedPass=hexUtils.byteToHexString(hash).toCharArray();

        boolean valid=true;

        if(newHashedPass.length==storedHash.length){
            for(int i=0;i<storedHash.length;i++){
                if(newHashedPass[i]!=storedHash[i]){
                    valid=false;
                    break;
                }
            }
        }else{
            return false;
        }
        System.out.println("valid: "+valid);
        return valid;
    }

    public boolean stringEmpty(String s){
        return s.isEmpty();
    }

    // public String byteToHex(byte[] array){
    //     StringBuilder sb=new StringBuilder();

    //     for(byte b:array){
    //         sb.append(String.format("%02X",b));
    //     }

    //     return sb.toString();
    // }

}
