package com.mark.web.services;

public interface PasswordCodecMethods{
   // public String generateHashedPassword(String rawPassword);
   // public boolean validatePassword(String rawPassword,String encryptedPassword);
   public boolean stringEmpty(String s);
   public String encrypt(String password)throws Exception;
   public String decrypt(String encryptedText)throws Exception;
}
