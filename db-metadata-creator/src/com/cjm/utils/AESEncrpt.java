package com.cjm.utils;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加解密
 */
public class AESEncrpt {
    private static final String ALGO = "AES";
    private static final byte[] PWD = "Administrator@163.com".getBytes(); // 密钥
    private static final String CHARTSET = "UTF-8";

    private static Key generateKey() throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(ALGO);  
        kgen.init(128, new SecureRandom(PWD));  //128, 192 or 256
        
        SecretKey secretKey = kgen.generateKey();  
        byte[] encodeFormat = secretKey.getEncoded();  
        
        SecretKeySpec key = new SecretKeySpec(encodeFormat, ALGO);
        
        return key;
    }
    
    /**
     * 加密字符串
     */
    public static String encrypt(String data) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encVal = cipher.doFinal(data.getBytes(CHARTSET));
        String encryptedValue = StringUtil.byte2HexStr(encVal);

        return encryptedValue;
    }

    /**
     * 解密字符串
     */
    public static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] b2 = cipher.doFinal(StringUtil.hexStr2Byte(encryptedData));
        String decordedValue = new String(b2, CHARTSET);
        
        return decordedValue;
    } 
    
    public static void main(String[] args) throws Exception {
        String data = "4512021988082616134512021988082616139090"; //40
        System.out.println("原始数据：" + data);

        String dataEnc = AESEncrpt.encrypt(data);
        System.out.println("加密数据：" + dataEnc);

        String dataDec = AESEncrpt.decrypt(dataEnc);
        System.out.println("解密数据: " + dataDec);

        System.out.println(data.equals(dataDec));
    }
    
}
