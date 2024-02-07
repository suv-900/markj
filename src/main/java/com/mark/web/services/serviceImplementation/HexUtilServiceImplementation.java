package com.mark.web.services.serviceImplementation;

import java.math.BigInteger;
import com.mark.web.services.HexUtilsService;

public class HexUtilServiceImplementation implements HexUtilsService {
    public HexUtilServiceImplementation(){}
    
    public byte[] byteFromHex(String s){
        
        // return HexUtils.fromHexString(s);
        
        int hexLength=s.length();
        char[] charArray=s.toCharArray();
        byte[] bytes=new byte[hexLength/2];
        System.out.println("String length: "+hexLength);
        System.out.println("byteArray length: "+bytes.length);
        
        for(int i=0;i<bytes.length;i++){
            if(2*i<hexLength){
                try{
                    int upperNibble=getDigit(charArray[2*i]) ;
                    int lowerNibble=getDigit(charArray[2*i+1]);
                    bytes[i]=(byte) (upperNibble << 4 + lowerNibble); 
                }catch(IllegalArgumentException e){
                    e.printStackTrace();
                }
            }else{
                break;
            }
            
        }
        return bytes; 
    }
    
    private int getDigit(char c){
        int digit=Character.digit(c,16);
        if(digit==-1){
            throw new IllegalArgumentException();
        }
        return digit;
    }
    
    
    

    public String byteToHex(byte[] array){
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<array.length;i++){
            sb.append(Character.forDigit((array[i]>>4 & 0xF), 16));
        }
        return sb.toString();
    }


    public String byteToHexString(byte[] array){
        BigInteger bi=new BigInteger(1,array);
        String hex=bi.toString(16);
        int paddingLength=(array.length*2)-hex.length();
        if(paddingLength>0){
            return String.format("%0"+paddingLength+"d",0)+hex;
        }else{
            return hex;
        }
    }
    // public String byteToHexString2(byte[] byteArray){
    //     //char array
    //     // BigInteger bigInteger=new BigInteger(1,byteArray);
    //     // String hexString=bigInteger.toString(16);
    //     // return hexString 
    //     char[] charArray=new char[2*byteArray.length];

    //     for(int i=0;i<byteArray.length;i++){
    //         byte b=byteArray[i];
    //         //c (int)lowerNibble
    //         //d (int)upperNibble 
    //         //char[2*i]=intToHex(c);
    //         //char[2*i+1]=intToHex(d);
    //         //return charArray.toString(); 
    //     } 
    // }
   
}
