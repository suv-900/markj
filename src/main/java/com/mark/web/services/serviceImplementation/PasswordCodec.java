package com.mark.web.services.serviceImplementation;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.tomcat.util.codec.binary.Base64;

// import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;

import com.mark.web.services.PasswordCodecMethods;

public class PasswordCodec implements PasswordCodecMethods{
    
    // private static final Logger logger=LogManager.getLogger();
    // private static final HexUtilServiceImplementation hexUtils=new HexUtilServiceImplementation();
    
    private String token;
    private String salt; 
    private int iterations=10; 
    private int keyLength=128;
    private byte[] ivBytes=new byte[16];
    private String keyAlgorithm="AES";
    private String encryptAlgorithm="AES/CBC/PKCS5Padding";
    private String secretKeyFactoryAlgorithm="PBKDF2WithHmacSHA1";

    //PBE-password based encryption generates
    //password based key by looking at the bits of each character

    public PasswordCodec(){
        this.salt=getSalt();
        this.token=getSalt();
    }

    private String getSalt(){
        SecureRandom random=new SecureRandom();
        byte[] bytes=new byte[16];
        random.nextBytes(bytes);
        String salt=new String(bytes);
        return salt;
    }
    
    // public IvParameterSpec getIVBytes(){
    //     byte[] ivBytes=new byte[16];
    //     new SecureRandom().nextBytes(ivBytes);
    //     return new IvParameterSpec(ivBytes);
    // }
    
    public boolean stringEmpty(String s){
        return s.isEmpty();
    }
    public String encrypt(String password)throws Exception{
        byte[] saltBytes=salt.getBytes("UTF-8");

        SecretKeyFactory skf=SecretKeyFactory.getInstance(this.secretKeyFactoryAlgorithm);

        PBEKeySpec spec=new PBEKeySpec(this.token.toCharArray(),saltBytes,iterations,keyLength);
        SecretKey secretKey=skf.generateSecret(spec);
        SecretKeySpec key=new SecretKeySpec(secretKey.getEncoded(),keyAlgorithm);

        Cipher cipher=Cipher.getInstance(encryptAlgorithm);
        cipher.init(Cipher.ENCRYPT_MODE,key,new IvParameterSpec(ivBytes));

        byte[] encryptedText=cipher.doFinal(password.getBytes("UTF-8"));
        
        String encodedPass=new Base64().encodeAsString(encryptedText);
        System.out.println("encodedPass: "+encodedPass);
        
        return encodedPass;

    }

    // public String encrypt2(String algorithm,String key,SecureRandom iv,String input)throws NoSuchPaddingException,
    // NoSuchAlgorithmException,InvalidKeyException,BadPaddingException,IllegalBlockSizeException,InvalidAlgorithmParameterException{
        
    //     Cipher cipher=Cipher.getInstance(algorithm);
    //     cipher.init(Cipher.ENCRYPT_MODE,key,iv);
    // }

    public String decrypt(String dbPassword)throws Exception{
        byte[] saltBytes = salt.getBytes("UTF-8");

        System.out.println("dbPassword: "+dbPassword);
        String[] parts=dbPassword.split(":");
        String encryptPassword=parts[0];
        byte[] ivBytes=parts[1].getBytes();
        System.out.println("IVBytes length: "+ivBytes.length);
		byte[] encryptTextBytes = new Base64().decode(encryptPassword);
		
		SecretKeyFactory skf = SecretKeyFactory.getInstance(this.secretKeyFactoryAlgorithm);
		PBEKeySpec spec = new PBEKeySpec(this.token.toCharArray(), saltBytes, this.iterations, this.keyLength);
		SecretKey secretKey = skf.generateSecret(spec);
		SecretKeySpec key = new SecretKeySpec(secretKey.getEncoded(), keyAlgorithm);
		
		Cipher cipher = Cipher.getInstance(encryptAlgorithm);
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivBytes));
		
		byte[] decyrptTextBytes = null;
		try {
			decyrptTextBytes = cipher.doFinal(encryptTextBytes);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		String text = new String(decyrptTextBytes);
		return text;
    }
    
    // public String generateHashedPassword(String rawPassword){
    //     char[] charArray=rawPassword.toCharArray();
    //     SecureRandom random=new SecureRandom();
    //     byte[] salt=new byte[16];
    //     random.nextBytes(salt);

        
        
    //     KeySpec specs=new PBEKeySpec(charArray,salt,iterations,keyLength);
    //     //bug
    //     byte[] hashedPasswordBytes=null;
    //     try{
    //         SecretKeyFactory factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    //         hashedPasswordBytes=factory.generateSecret(specs).getEncoded();
    //     }catch(NoSuchAlgorithmException n){
    //         logger.error("cryptographic algorithm is not availaible in the env.");
    //         n.printStackTrace();
    //     }catch(InvalidKeySpecException i){
    //         logger.error("invalid key specifications.");
    //         i.printStackTrace();
    //     }

    //     String finalString=null;
    //     finalString=iterations+":"+hexUtils.byteToHexString(salt)+":"+hexUtils.byteToHexString(hashedPasswordBytes);

    //     return finalString;
    // } 

    

    // // private String generateHashFromKnownSalt(byte[] salt,String loginPassword){
    // //     byte[] hash=null;
    // //     try{
    // //         KeySpec specs=new PBEKeySpec(loginPassword.toCharArray(),salt,iterations,keyLength);
    // //         SecretKeyFactory factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    // //         hash=factory.generateSecret(specs).getEncoded();
    // //     }catch(NoSuchAlgorithmException e){
    // //         logger.error("cryptographic algorithm is not availaible in the env.");
    // //         e.printStackTrace();
    // //     }catch(InvalidKeySpecException i){
    // //         logger.error("invalid key specifications.");
    // //         i.printStackTrace();
    // //     }
    // //     return hexUtils.byteToHexString(hash);
    // // }
    
    // public boolean validatePassword(String loginPassword,String storedPassword){
    //     System.out.println("loginPassword: "+loginPassword+"\nstoredPassword:"+storedPassword);
        
    //     String[] parts=storedPassword.split(":");
    //     int storedIteration=Integer.parseInt(parts[0]);
    //     byte[] storedSalt=hexUtils.byteFromHex(parts[1]);
    //     char[] storedHash=parts[2].toCharArray();
        
        

    //     int keylen=this.keyLength; 
    //     System.out.println("keyLength: "+keylen);
    //     System.out.println("storedSalt: "+parts[1]);
    //     byte[] hash=null;
    //     try{
    //         //refactor the type(UtillityServiceImplemenetation.keyLength)
    //         KeySpec specs=new PBEKeySpec(loginPassword.toCharArray(),storedSalt,storedIteration,keylen);
    //         SecretKeyFactory factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    //         hash=factory.generateSecret(specs).getEncoded();
    //     }catch(NoSuchAlgorithmException e){
    //         logger.error("cryptographic algorithm is not availaible in the env.");
    //         e.printStackTrace();
    //     }catch(InvalidKeySpecException i){
    //         logger.error("invalid key specifications.");
    //         i.printStackTrace();
    //     }
        
    //     char[] newHashedPass=hexUtils.byteToHexString(hash).toCharArray();

    //     boolean valid=true;

    //     if(newHashedPass.length==storedHash.length){
    //         for(int i=0;i<storedHash.length;i++){
    //             if(newHashedPass[i]!=storedHash[i]){
    //                 valid=false;
    //                 break;
    //             }
    //         }
    //     }else{
    //         return false;
    //     }
    //     System.out.println("valid: "+valid);
    //     return valid;
    // }

    

    // public String byteToHex(byte[] array){
    //     StringBuilder sb=new StringBuilder();

    //     for(byte b:array){
    //         sb.append(String.format("%02X",b));
    //     }

    //     return sb.toString();
    // }

}
