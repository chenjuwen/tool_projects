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
public class OracleEntityCreator {
	private Connection cn = null;
	
    String tableName = "T_DEP_CF_FLIGHT_OP_LOG"; 
    String fieldPrefix = ""; //字段前缀，在生成bean属性时需要去掉
	
	public OracleEntityCreator()throws Exception{
//		String driver = "oracle.jdbc.driver.OracleDriver";
//   		String url = "jdbc:oracle:thin:@172.29.24.63:1521:crm";
//   		String uid = "huanggy";
//   		String pwd = "huanggy";
	    
//	     String driver = "oracle.jdbc.driver.OracleDriver";
//	     String url = "jdbc:oracle:thin:@localhost:1521:orcl11";
//	     String uid = "dep";
//	     String pwd = "dep";
        
      String driver = "oracle.jdbc.driver.OracleDriver";
      String url = "jdbc:oracle:thin:@172.29.24.13:1521:dep1";
      String uid = "depkf15";
      String pwd = "depkf15";
   		
   		Class.forName(driver);
	   	this.cn = DriverManager.getConnection(url,uid,pwd);
	}
    
    public static void main(String[] args) {
        try{
            OracleEntityCreator creator = new OracleEntityCreator();
            creator.execute();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
	
	private static List<String> numberTypes;
	static{
		numberTypes = new ArrayList<String>();
		numberTypes.add("NUMERIC");
		numberTypes.add("FLOAT");
	}
	
	private boolean isNumberType(String fieldType){
		return numberTypes.contains(fieldType.toUpperCase());
	}
	
	private LinkedHashMap<String, String> getColumnComments(String tableName)throws Exception{
		LinkedHashMap<String, String> colMap = new LinkedHashMap<String, String>();
		
		StringBuffer sb = new StringBuffer();
		sb.append(" select TABLE_NAME,COLUMN_NAME,COMMENTS from user_col_comments ");
		sb.append(" where upper(TABLE_NAME)=upper('" + tableName + "') ");
		
		PreparedStatement pstm = cn.prepareStatement(sb.toString());
		ResultSet rs = pstm.executeQuery();
		while(rs.next()){
			colMap.put(rs.getString("COLUMN_NAME").toUpperCase(), rs.getString("COMMENTS"));
		}
	    rs.close();
	    pstm.close();
		
		return colMap;
	}
	
	private String getPKField(String tableName) throws Exception {
		String fieldName = "";
		
		StringBuffer sb = new StringBuffer();
		sb.append("select column_name from user_cons_columns where constraint_name=");
		sb.append("(select constraint_name from user_constraints where table_name='"+tableName+"' and constraint_type='P')");
		
		PreparedStatement pstm = cn.prepareStatement(sb.toString());
		ResultSet rs = pstm.executeQuery();
		if(rs.next()){
			fieldName = rs.getString("column_name").toUpperCase();
		}
	    rs.close();
	    pstm.close();
	    return fieldName;
	}
	
	public void execute(){
	    String position = "R";  //R右边，T上面, 空：无注释
		try{
			LinkedHashMap<String, String> columnRemarkMap = getColumnComments(tableName);
			
			//所有字段code，之间逗号分隔。用于生成select和insert语句
			StringBuffer sbSelect = new StringBuffer(); 
			StringBuffer sbSelect2 = new StringBuffer(); 
			StringBuffer sbInsert = new StringBuffer(); //insert语句的values后面部分
			StringBuffer sbResultMap = new StringBuffer();
			StringBuffer sbUpdate = new StringBuffer();
			
			//主键字段
			String pkFieldName = getPKField(tableName);
			pkFieldName = formatFieldCode(pkFieldName, fieldPrefix);
			
			Statement stm = cn.createStatement();
			stm.setMaxRows(1);
			ResultSet rsColumn = stm.executeQuery("select * from " + tableName);
			ResultSetMetaData rsmd = rsColumn.getMetaData();
			for(int i=1; i<=rsmd.getColumnCount(); i++){
			    String columnName = rsmd.getColumnName(i).toUpperCase();
                String fieldCode = formatFieldCode(columnName, fieldPrefix);
				
				if(i>1){
					sbSelect.append(", " + columnName);
					sbSelect2.append(",\n\t" + columnName + " as " + fieldCode);
					sbInsert.append(", #{" + fieldCode + "}");
                    sbUpdate.append(",\n\t" + columnName + " = " + "#{" + fieldCode + "}");
				}else{
					sbSelect.append(columnName);
					sbSelect2.append("\n\t" + columnName + " as " + fieldCode);
					sbInsert.append("#{" + fieldCode + "}");
					sbUpdate.append("\t"+columnName + " = " + "#{" + fieldCode + "}");
				}
				
				//字段备注
				String fieldRemark = columnRemarkMap.get(columnName);
				
				String jdbcType = "String";
				String mapperJdbcType = "VARCHAR";
				String fieldType = rsmd.getColumnTypeName(i).toUpperCase();
				
				if(fieldType.equals("INT") || fieldType.equals("INTEGER")
						|| fieldType.equals("SMALLINT") || fieldType.equals("LONG")
						|| fieldType.equals("NUMBER")){
					jdbcType = "Long";
					mapperJdbcType = "BIGINT";
				}else if(isNumberType(fieldType)){
					jdbcType = "Double";
					mapperJdbcType = "DOUBLE";
				}else if(fieldType.equals("DATE")){
					jdbcType = "Date";
					mapperJdbcType = "DATE";
				}
				
				//mybatis result map
				if(fieldCode.equalsIgnoreCase(pkFieldName)){
				    sbResultMap.append("<id property=\"id\" column=\"" + columnName + "\" jdbcType=\"" + mapperJdbcType + "\" />\n");
				}else{
                    sbResultMap.append("<result property=\"" + fieldCode + "\" column=\"" + columnName + "\" jdbcType=\"" + mapperJdbcType + "\" />\n");
				}
				
				if("T".equals(position)){
				    System.out.println("/**");
				    System.out.println(" * " + fieldCode + ": " + StringUtil.trim(fieldRemark) + "。 对应数据库字段:" + columnName);
				    System.out.println(" */");
				    System.out.println("private " + jdbcType + " " + fieldCode + ";");
					System.out.println();
				}else if("R".equals(position)){
				    System.out.println("private " + jdbcType + " " + fieldCode + "; \t//" + StringUtil.trim(fieldRemark));
				}else{
					System.out.println("private " + jdbcType + " " + fieldCode + ";");
				}
			}
			
			//result map
			System.out.println();
			System.out.println(sbResultMap.toString());
			
			//select sql
			System.out.println();
			System.out.println("select " + sbSelect.toString() + " from " + tableName);
			
			System.out.println();
			System.out.println("select " + sbSelect2.toString() + " \nfrom " + tableName);
			
			//insert sql
            System.out.println();
			System.out.println("insert into " + tableName  + " (" + sbSelect.toString() + ") \n values (" + sbInsert.toString() + ")");
			
			//delete sql
            System.out.println();
			System.out.println("delete from " + tableName);
			
			//update sql
			System.out.println();
			System.out.println("update " + tableName + " set \n" + sbUpdate.toString());
			
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
