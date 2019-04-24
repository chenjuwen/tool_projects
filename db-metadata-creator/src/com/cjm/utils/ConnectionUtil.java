package com.cjm.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Copyright (c) 2015,TravelSky. 
 * All Rights Reserved.
 * TravelSky CONFIDENTIAL
 * 
 * Project Name:db-metadata-creator
 * Package Name:
 * File Name:ConnectionUtil.java
 * Date:2015-2-5 上午11:24:29
 * 
 */

/**
 * 数据库连接工具类
 */
public class ConnectionUtil {
    public static Connection getConnection(String ip, String serviceName, 
            String uid, String pwd) throws Exception {
        String driver = "oracle.jdbc.driver.OracleDriver";
        String url = "jdbc:oracle:thin:@"+ip+":1521:"+serviceName;
        
//        String url = "jdbc:oracle:thin:@localhost:1521:ORCL11";
//        String uid = "dep";
//        String pwd = "dep";
        
//        String url = "jdbc:oracle:thin:@172.29.24.13:1521:dep1";
//        String uid = "deptestnew";
//        String pwd = "deptestnew";
        
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url,uid,pwd);
        
        return conn;
    }
    
    public static void close(Connection conn){
        try{
            if(conn != null){
                conn.close();
                conn = null;
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public static void close(PreparedStatement pstm){
        try{
            if(pstm != null){
                pstm.close();
                pstm = null;
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public static void close(ResultSet rs){
        try{
            if(rs != null){
                rs.close();
                rs = null;
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
}
