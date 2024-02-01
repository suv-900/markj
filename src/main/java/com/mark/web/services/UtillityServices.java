package com.mark.web.services;

public interface UtillityServices {
   public String generateHashedPassword(String rawPassword);
   public boolean validatePassword(String rawPassword,String encryptedPassword);
   public boolean stringEmpty(String s);
}
