package com.cjm.utils;

import java.util.Random;

public class StringUtil {
	public static boolean isEmpty(String value){
		if(value == null || value.trim().length() == 0){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isNotEmpty(String value){
		if(value == null || value.trim().length() == 0){
			return false;
		}else{
			return true;
		}
	}
	
	public static String trim(String value){
		if(isEmpty(value)){
			return "";
		}else{
			return value.trim();
		}
	}
	
	/**
	 * 功能：取得指定长度的随机数字字符串
	 */
	public static String getRandomNumber(int length, Random rnd){
		String tmpStr = "";
		for(int i=1;i<=length;i++){
			tmpStr += String.valueOf(rnd.nextInt(10));
		}
		
		return tmpStr;
	}
	
	/**
	 * 补充前缀
	 */
	public static String makeupPrefix(String text, String prefix, int len){
		String tmp = trim(text);
		try{
			if(isEmpty(prefix)) prefix = " ";
			if(tmp.getBytes().length >= len) return tmp;
			
			int spareLength = len - tmp.getBytes().length;
			for(int i=1;i<=spareLength;i++){
				tmp = prefix + tmp;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return tmp;
	}
	
	/**
	 * 补充后缀
	 */
	public static String makeupSuffix(String text, String suffix, int len){
		String tmp = trim(text);
		try{
			if(isEmpty(suffix)) suffix = " ";
			if(tmp.getBytes().length >= len) return tmp;
			
			int spareLength = len - tmp.getBytes().length;
			for(int i=1;i<=spareLength;i++){
				tmp = tmp + suffix;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return tmp;
	}

	public static void main(String[] args) {
	    Random rnd = new Random();
        for(int i=0;i<50;i++){
            System.out.println(getRandomNumber(6, rnd));
        }
    }
	
	/**
	 * 将字节数组转换成16进制字符串
	 * @param buf 字节数组
	 * @return 16进制字符串
	 */
	public static String byte2HexStr(byte buf[]) {  
	    StringBuffer sb = new StringBuffer();  
	    for (int i = 0; i < buf.length; i++) {  
	            String hex = Integer.toHexString(buf[i] & 0xFF);  
	            if (hex.length() == 1) {  
	                    hex = '0' + hex;  
	            }  
	            sb.append(hex.toUpperCase());  
	    }  
	    return sb.toString();  
	}

	/**
	 * 将16进制字符串转换为字节数组 
	 * @param hexStr 16进制字符串
	 * @return 字节数组
	 */
	public static byte[] hexStr2Byte(String hexStr) {  
	    if (hexStr.length() < 1)  
	            return null;  
	    byte[] result = new byte[hexStr.length()/2];  
	    for (int i = 0;i< hexStr.length()/2; i++) {  
	            int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);  
	            int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);  
	            result[i] = (byte) (high * 16 + low);  
	    }  
	    return result;
	}
	
}
