package com.mark.web.services.serviceImplementation;

import java.math.BigInteger;

import org.apache.tomcat.util.buf.HexUtils;

import com.mark.web.services.HexUtilsService;

public class HexUtilServiceImplementation implements HexUtilsService {
    public HexUtilServiceImplementation(){}
    
    public byte[] byteFromHex(String s){
        
        // return HexUtils.fromHexString(s);
        
        int length=s.length();
        char[] charArray=s.toCharArray();
        byte[] result=new byte[length/2];
        System.out.println("String length: "+length);
        System.out.println("byteArray length: "+result.length);
        
        for(int i=0;i<length;i++){
            try{
                int upperNibble=getDecimalFromHex(charArray[2*i]) ;
                int lowerNibble=getDecimalFromHex(charArray[2*i+1]);
                result[i]=(byte) (upperNibble << 4 + lowerNibble); 
            }catch(IllegalArgumentException e){
                e.printStackTrace();
            }
        }
        return result; 
    }
   
    public String byteToHexString(byte[] array){
        BigInteger bi=new BigInteger(1,array);
        String hex=bi.toString(16);
        System.out.println("byte to hex: "+hex);
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

        public byte[] byteFromHex2(String s){
            return HexUtils.fromHexString(s);
    }

    //unhandled Exception
    private int getDecimalFromHex(char c)throws IllegalArgumentException{
        switch(c){
            case '0':
            return 0;
            case '1':
            return 1;
            case '2':
            return 2;
            case '3':
            return 3;
            case '4':
            return 4;
            case '5':
            return 5;
            case '6':
            return 6;
            case '7':
            return 7;
            case '8':
            return 8;
            case '9':
            return 9;
            case 'a':
            return 10;
            case 'b':
            return 11;
            case 'c':
            return 12;
            case 'd':
            return 13;
            case 'e':
            return 14;
            case 'f':
            return 15;
            default:
                System.out.println("input: "+c);
                throw new IllegalArgumentException("Invalid Hex character");
        }
    }



    
}
