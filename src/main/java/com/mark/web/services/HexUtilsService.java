package com.mark.web.services;

public interface HexUtilsService {
   public String byteToHexString(byte[] byteArray);
   // public String byteToHexString2(byte[] byteArray);
   public byte[] byteFromHex(String hex); 
   public byte[] byteFromHex2(String hex); 
}
