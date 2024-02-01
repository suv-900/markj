package com.mark.web.services;

public interface HexUtilsService {
   public String byteToHexString(byte[] byteArray);
   public byte[] byteFromHex(String hex); 
   public String byteToHex(byte[] array); 
}
