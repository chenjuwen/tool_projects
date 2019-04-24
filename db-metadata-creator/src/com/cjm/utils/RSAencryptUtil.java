package com.cjm.utils;
/**
 *
 * @(#)Rsaencrypt.java 1.0.0  2014-12-5 by gske
 *
 * Copyright (c) 2013 TravelSky. 
 * All rights reserved.    
 * Travelsky CONFIDENTIAL   
 *
 **/


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.apache.commons.io.IOUtils;

/**
 * Description: <br/>
 * 加密解密算法 <br/>
 * 
 * 
 * <b>修改历史:</b> <br/>
 * version 1.0.0 2014-12-5 gske Initial Version <br/>
 */
public class RSAencryptUtil {
    private static RSAPublicKey pbk = null;
    private static RSAPrivateKey prk = null;
    
    /**
     * createRsaKey:生成密钥对 <br/>
     */
    public static void createRsaKey() {
        FileOutputStream f1 = null;
        ObjectOutputStream b1 = null;
        FileOutputStream f2 = null;
        ObjectOutputStream b2 = null;
        try {
            // 创建密钥对生成器，指定加密和解密算法为RSA
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            // 指定密钥的长度，初始化密钥对生成器
            kpg.initialize(512);
            // 生成密钥对
            KeyPair kp = kpg.genKeyPair();

            // 获取公钥
            PublicKey pbkey = kp.getPublic();
            // 获取私钥
            PrivateKey prkey = kp.getPrivate();

            // 保存公钥到文件
            f1 = new FileOutputStream("dep_rsa_pub.dat");
            b1 = new ObjectOutputStream(f1);
            b1.writeObject(pbkey);

            // 保存私钥到文件
            f2 = new FileOutputStream("dep_rsa_priv.dat");
            b2 = new ObjectOutputStream(f2);
            b2.writeObject(prkey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(b1);
            IOUtils.closeQuietly(f1);
            IOUtils.closeQuietly(b2);
            IOUtils.closeQuietly(f2);
        }
    }

    /**
     * EncRsa:加密字符串 <br/>
     * 
     * @param s
     *            需要加密的字符串
     * @return 返回加密后的字符串 <br/>
     */
    public static String EncRsa(String s) {
        FileInputStream f = null;
        BigInteger c = null;
        try {
            if(StringUtil.isEmpty(s)){
                return "";
            }
            
            if(pbk == null){
                // 从文件中读取公钥
                f = new FileInputStream("E:/study/db-metadata-creator/dep_rsa_pub.dat");
                ObjectInputStream b = new ObjectInputStream(f);
                pbk = (RSAPublicKey) b.readObject();
            }
            
            // RSA算法是使用整数进行加密的，在RSA公钥中包含有两个整数信息：e和n。对于明文数字m,计算密文的公式是m的e次方再与n求模。
            BigInteger e = pbk.getPublicExponent();
            BigInteger n = pbk.getModulus();

            // 获取明文的大整数
            byte ptext[] = s.getBytes("UTF-8");
            BigInteger m = new BigInteger(ptext);

            // 加密明文
            c = m.modPow(e, n);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(f);
        }

        return c != null ? c.toString() : null;
    }

    /**
     * DecRsa:解密字符串 <br/>
     * 
     * @param s
     *            需要解密的字符串
     * @return 解密后的字符串
     */
    public static String DecRsa(String s) {
        FileInputStream f = null;
        BigInteger c = null;
        try {
            if(StringUtil.isEmpty(s)){
                return "";
            }
            
            c = new BigInteger(s);

            if(prk == null){
                // 获取私钥
                f = new FileInputStream("E:/study/db-metadata-creator/dep_rsa_priv.dat");
                ObjectInputStream b = new ObjectInputStream(f);
                prk = (RSAPrivateKey) b.readObject();
            }
            
            // 获取私钥的参数d,n
            BigInteger d = prk.getPrivateExponent();
            BigInteger n = prk.getModulus();

            // 解密明文
            BigInteger m = c.modPow(d, n);

            // 计算明文对应的字符串并输出。
            byte[] mt = m.toByteArray();
            // System.out.println("PlainText is ");
            // for(int i=0;i<mt.length;i++){
            // System.out.print((char) mt[i]);
            // }
            String str = new String(mt, "UTF-8");

            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(f);
        }
    }

    public static void main(String[] args) {
        System.out.println(RSAencryptUtil.EncRsa("1"));
        //createRsaKey();
    }
    
}
