package com.cjm.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TableUtil {
	private static Connection connection;
	private static String catalog = null; //sqlserver
	private static String schemaPattern = "DIYOU"; //oracle
	
	public static void main(String[] args) {
		try{
			String driver = "oracle.jdbc.driver.OracleDriver";
	   		String url = "jdbc:oracle:thin:@120.25.81.131:1521:POD";
	   		String uid = "diyou";
	   		String pwd = "123456";
	
	   		Class.forName(driver);
		   	connection = DriverManager.getConnection(url, uid, pwd);
		   	
		   	showTablenameList();
		   	printTableExtentSQL();
		   	
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				if(connection != null){
					connection.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 打印所有表名
	 */
	private static void showTablenameList() {
		try{
			DatabaseMetaData dbmd = connection.getMetaData();
			String[] types = {"TABLE", "VIEW"}; //TABLE, VIEW
			ResultSet rs = dbmd.getTables(catalog, schemaPattern, null, types);
			while(rs.next()){
				String tableName = rs.getString("TABLE_NAME");
				System.out.println(tableName);
			}
			rs.close();
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 生成为空表分配空间的SQL语句，比如
	 * alter table DIYOU_WEIXIN_MESSAGE allocate extent;
	 */
	private static void printTableExtentSQL(){
		try{
			DatabaseMetaData dbmd = connection.getMetaData();
			String[] types = {"TABLE"}; //TABLE, VIEW
			ResultSet rs = dbmd.getTables(catalog, schemaPattern, null, types);
			while(rs.next()){
				String tableName = rs.getString("TABLE_NAME");
				
				Statement stm = connection.createStatement();
				stm.setMaxRows(1);
				ResultSet rsCount = stm.executeQuery("select count(*) as count from " + tableName);
				if(rsCount != null && rsCount.next() && rsCount.getInt("count") <= 0) {
					System.out.println("alter table " + tableName + " allocate extent;");
				}
				rsCount.close();
				stm.close();
			}
			rs.close();
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
