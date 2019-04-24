package com.cjm.utils;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Travelsky(chenjm@travelsky.com) 2013-2-4
 * @version 1.0
 * @modifyed by Travelsky(chenjm@travelsky.com) description
 * @Function 使用AES进行数据加解密
 */
public class AESencrptUtil {
    private static final String ALGO = "AES";
    private static final byte[] PWD = "DEP@travelsky.com".getBytes(); // 密钥
    private static final String CHARTSET = "UTF-8";

    /**
     * 加密字符串
     */
    public static String encrypt(String data) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encVal = cipher.doFinal(data.getBytes(CHARTSET));
        String encryptedValue = byte2HexStr(encVal);

        return encryptedValue;
    }

    /**
     * 解密字符串
     */
    public static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] b2 = cipher.doFinal(hexStr2Byte(encryptedData));
        String decordedValue = new String(b2, CHARTSET);
        
        return decordedValue;
    } 

    /**
     * generate Key
     */
    private static Key generateKey() throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(ALGO);  
        kgen.init(128, new SecureRandom(PWD));  
        
        SecretKey secretKey = kgen.generateKey();  
        byte[] encodeFormat = secretKey.getEncoded();  
        
        SecretKeySpec key = new SecretKeySpec(encodeFormat, ALGO);
        
        return key;
    }
    
    /**
     * 将二进制转换成16进制
     */  
    private static String byte2HexStr(byte buf[]) {  
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
     * 将16进制转换为二进制 
     */  
    private static byte[] hexStr2Byte(String hexStr) {  
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

    /**
      * @param args 参数
      * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception {
        String data = "451202198808261613451202198808261613"; //451202198808261613
        System.out.println("原始数据：" + data);

        String dataEnc = AESencrptUtil.encrypt(data);
        System.out.println("加密数据：" + dataEnc);

        String dataDec = AESencrptUtil.decrypt(dataEnc);
        System.out.println("解密数据: " + dataDec);

        System.out.println(data.equals(dataDec));
    }
}
