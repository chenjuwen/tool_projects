package com.cjm.utils;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 3DES加解密
 */
public class DES3Encrpt {
    private static final String TRANSFORMATION = "DESede/ECB/PKCS5Padding";
	private static final String ALGO = "DESede"; //加密算法,可用 DES,DESede,Blowfish
    private static final byte[] PWD = "Administrator@163.com".getBytes(); // 密钥
    private static final String CHARTSET = "UTF-8";

    private static Key generateKey() throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(ALGO);  
        kgen.init(168, new SecureRandom(PWD));  //112 or 168
        
        SecretKey secretKey = kgen.generateKey();  
        byte[] encodeFormat = secretKey.getEncoded();  
        
        SecretKeySpec key = new SecretKeySpec(encodeFormat, ALGO);
        
        return key;
    }

    /**
     * 加密
     */
    public static String encrypt(String data) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encData = cipher.doFinal(data.getBytes(CHARTSET));
        String encryptedValue = StringUtil.byte2HexStr(encData);

        return encryptedValue;
    }

    /**
     * 解密
     */
    public static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decData = cipher.doFinal(StringUtil.hexStr2Byte(encryptedData));
        String decryptedValue = new String(decData, CHARTSET);
        
        return decryptedValue;
    } 
    
    public static void main(String[] args) throws Exception {
        String data = "4512021988082616134512021988082616139090"; //40
        System.out.println("原始数据：" + data);

        String dataEnc = DES3Encrpt.encrypt(data);
        System.out.println("加密数据：" + dataEnc);

        String dataDec = DES3Encrpt.decrypt(dataEnc);
        System.out.println("解密数据: " + dataDec);

        System.out.println(data.equals(dataDec));
    }
    
}
