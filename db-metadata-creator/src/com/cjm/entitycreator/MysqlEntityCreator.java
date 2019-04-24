package com.cjm.entitycreator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cjm.utils.StringUtil;

/**
 * 为指定的表 生成pojo类属性和SQL语句
 */
public class MysqlEntityCreator {
	private Connection cn = null;
	
	String tableSchema = "sftvisit";
    String tableName = "core_task_deal"; 
    String fieldPrefix = ""; //字段前缀，在生成bean属性时需要去掉
	
	public MysqlEntityCreator()throws Exception{
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://192.168.1.199/sftvisit?useUnicode=true&amp;characterEncoding=GBK";
		String uid = "root";
		String pwd = "qk123456";
   		
   		Class.forName(driver);
	   	this.cn = DriverManager.getConnection(url,uid,pwd);
	}
    
    public static void main(String[] args) {
        try{
            //System.out.println("start...");
            MysqlEntityCreator creator = new MysqlEntityCreator();
            creator.execute();
            //System.out.println("end");
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
	
	private static List<String> stringTypes;
	private static List<String> numberTypes;
	static{
		stringTypes = new ArrayList<String>();
		stringTypes.add("blob");
		stringTypes.add("char");
		stringTypes.add("text");
		stringTypes.add("varchar");
		
		numberTypes = new ArrayList<String>();
		numberTypes.add("NUMERIC");
		numberTypes.add("FLOAT");
	}
	
	private LinkedHashMap<String, String> getColumnComments(String tableName)throws Exception{
		LinkedHashMap<String, String> colMap = new LinkedHashMap<String, String>();
		
		StringBuffer sb = new StringBuffer();
		sb.append(" select column_name,column_type,column_key,is_nullable,column_comment from INFORMATION_SCHEMA.Columns ");
		sb.append(" where table_schema='" + tableSchema + "' and table_name='" + tableName + "' ");
		sb.append(" ORDER BY ordinal_position ASC");
		
		PreparedStatement pstm = cn.prepareStatement(sb.toString());
		ResultSet rs = pstm.executeQuery();
		while(rs.next()){
			colMap.put(rs.getString("column_name").toUpperCase(), rs.getString("column_comment"));
		}
	    rs.close();
	    pstm.close();
		
		return colMap;
	}
	
	public void execute(){
	    String position = "T";  //L右边，T上面
		try{
			LinkedHashMap<String, String> columnRemarkMap = getColumnComments(tableName);
			System.out.println(columnRemarkMap);
			
			//所有字段code，之间逗号分隔。用于生成select和insert语句
			StringBuffer sbSelect = new StringBuffer(); 
			StringBuffer sbInsert = new StringBuffer(); //insert语句的values后面部分
			StringBuffer sbResultMap = new StringBuffer();
			StringBuffer sbUpdate = new StringBuffer();
			
			Statement stm = cn.createStatement();
			ResultSet rsColumn = stm.executeQuery("select * from " + tableName);
			ResultSetMetaData rsmd = rsColumn.getMetaData();
			for(int i=1; i<=rsmd.getColumnCount(); i++){
			    String columnName = rsmd.getColumnName(i).toUpperCase();
                String fieldCode = formatFieldCode(columnName, fieldPrefix);
				
				if(i>1){
					sbSelect.append(", " + columnName);
					sbInsert.append(", #{" + fieldCode + "}");
                    sbUpdate.append(",\n" + columnName + "=" + "#{" + fieldCode + "}");
				}else{
					sbSelect.append(columnName);
					sbInsert.append("#{" + fieldCode + "}");
					sbUpdate.append(columnName + "=" + "#{" + fieldCode + "}");
				}
				
				String javaType = "String";
				String mapperJavaType = "VARCHAR";
				String fieldType = rsmd.getColumnTypeName(i).toLowerCase();
				
				if(fieldType.equals("bigint")){
					javaType = "Long";
					mapperJavaType = "BIGINT";
				}else if(fieldType.equals("int") || fieldType.equals("integer")){
					javaType = "Integer";
					mapperJavaType = "INTEGER";
				}else if(fieldType.equals("double") || fieldType.equals("float") || fieldType.equals("numeric")){
					javaType = "Double";
					mapperJavaType = "DOUBLE";
				}else if(fieldType.equals("date") || fieldType.equals("datetime") || fieldType.equals("timestamp")){
					javaType = "Date";
					mapperJavaType = "DATE";
				}
				
				//mybatis result map
				if("id".equalsIgnoreCase(fieldCode)){
				    sbResultMap.append("<id column=\"" + columnName + "\" property=\"" + fieldCode + "\" jdbcType=\"" + mapperJavaType + "\" />\n");
				}else{
                    sbResultMap.append("<result column=\"" + columnName + "\" property=\"" + fieldCode + "\" jdbcType=\"" + mapperJavaType + "\" />\n");
				}
				
				//String fieldRemark = columnRemarkMap.get(columnName);
				
				if("T".equals(position)){
				    System.out.println("private " + javaType + " " + fieldCode + ";");
				}else{
					System.out.println("private " + javaType + " " + fieldCode + ";");
				}
			}
			
			//select sql
			System.out.println();
			System.out.println("select " + sbSelect.toString() + " from " + tableName);
			
			//insert sql
            System.out.println();
			System.out.println("insert into " + tableName  + " (" + sbSelect.toString() + ") values (" + sbInsert.toString() + ")");
			
			//delete sql
            System.out.println();
			System.out.println("delete from " + tableName);
			
			//update sql
			System.out.println();
			System.out.println("update " + tableName + " set \n" + sbUpdate.toString());
			
			//result map
			System.out.println();
			System.out.println(sbResultMap.toString());
			
			rsColumn.close();
			stm.close();
			
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				if(cn != null)cn.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public String formatFieldCode(String fieldCode, String fieldPrefix){
		//去掉字段前缀
		if(StringUtil.isNotEmpty(fieldPrefix)){
			fieldPrefix = fieldPrefix.toUpperCase();
			fieldCode = fieldCode.replaceFirst(fieldPrefix, "");
		}
		
		//找出字符串中所有的满足正则的部分（_x）进行处理：去掉下划线，字母变大写。第二个单词开始首字母变大写。
		String regEx = "(_[a-z])";  
	    Pattern p = Pattern.compile(regEx);  
	    Matcher m = p.matcher(fieldCode.toLowerCase());  
	    
	    StringBuffer sb = new StringBuffer("");  
	    while(m.find()){  
	        m.appendReplacement(sb,m.group().replaceFirst("_","").toUpperCase());  
	    }  
	    m.appendTail(sb);  
	    String returnString = sb.toString(); 
	    
	    return returnString;
	}
	
}
