package com.cjm.databasemetadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cjm.core.doc.MetadataWordCreater;
import com.cjm.core.doc.WordCreater;
import com.cjm.databasemetadata.bean.MetadataBean;
import com.cjm.databasemetadata.bean.TableHeaderBean;
import com.cjm.utils.StringUtil;

/**
 * 生成Oracle数据库表的数据字典
 */
public class OracleMetadataCreator extends AbstractMetadataCreator {
	private String schemaPattern;
	
	public OracleMetadataCreator(Connection connection, String schemaPattern){
		this.connection = connection;
		this.schemaPattern = schemaPattern;
	}
	
	public OracleMetadataCreator(Connection connection, String schemaPattern, String outputFile){
		this.connection = connection;
		this.schemaPattern = schemaPattern;
		this.outputFile = outputFile;
	}
	
	@Override
	public List<MetadataBean> generateMetadataList() throws Exception {
		List<MetadataBean> beanList = new ArrayList<MetadataBean>();
		
		try{
			LinkedHashMap<String, String> tableMap = getTableComments();
			
			DatabaseMetaData dbmd = connection.getMetaData();
			String[] types = {"TABLE", "VIEW"}; //TABLE, VIEW
			ResultSet rs = dbmd.getTables(null, schemaPattern, null, types);
			
			int tableIndex = 0;
			while(rs.next()){
				try{
					String tableName = rs.getString("TABLE_NAME");
					
					tableIndex++;
					
					System.out.println(tableIndex + ". " + tableName + " doing...");
					
					//表字段备注信息
					LinkedHashMap<String, String> columnMap = getColumnComments(tableName);
					
					//表备注
					String tableComment = StringUtil.trim(tableMap.get(tableName));
					
					//主键
					String strPrimaryKeys = getTablePrimaryKeys(tableName);
					
					MetadataBean bean = new MetadataBean();
					bean.setTableName(tableName);
					bean.setTableComnent(tableComment);
					
					Statement stm = connection.createStatement();
					stm.setMaxRows(1);
					ResultSet rsColumn = stm.executeQuery("select * from " + tableName);
					ResultSetMetaData rsmd = rsColumn.getMetaData();
					for(int i=1; i<=rsmd.getColumnCount(); i++){
						Map<String, String> fieldMap = new HashMap<String, String>();
						
						//字段名
						fieldMap.put("f1", rsmd.getColumnName(i) );
						
						//字段备注
						fieldMap.put("f2", columnMap.get(rsmd.getColumnName(i).toUpperCase()) );
						
						//字段类型
						String fieldType = rsmd.getColumnTypeName(i).toUpperCase();
						if(isStringType(fieldType)){
							fieldType += "(" + String.valueOf(rsmd.getColumnDisplaySize(i)) + ")";
						}else if(isNumberType(fieldType)){
							if(rsmd.getColumnDisplaySize(i) > rsmd.getPrecision(i)){
								fieldType += "(" + rsmd.getPrecision(i) + "," + rsmd.getScale(i) + ")";
							}
						}
						fieldMap.put("f3", fieldType);
						
						//是否主键
						if(strPrimaryKeys.indexOf("," + rsmd.getColumnName(i) + ",") != -1){
							fieldMap.put("f4", "Y" );
						}else{
							fieldMap.put("f4", "" );
						}
						
						//是否可空
						fieldMap.put("f5", (rsmd.isNullable(i)==1)?"":"N" );
						
						//备注
						fieldMap.put("f6", "" );
						
						bean.addField(fieldMap);
					}
					
					beanList.add(bean);
					
					rsColumn.close();
					stm.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			rs.close();
			
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
		
		return beanList;
	}
	
	/**
	 * 取得表的备注信息
	 */
	private LinkedHashMap<String, String> getTableComments()throws Exception{
		LinkedHashMap<String, String> columnMap = new LinkedHashMap<String, String>();
		
		StringBuffer sb = new StringBuffer();
		sb.append("select TABLE_NAME,TABLE_TYPE,COMMENTS from user_tab_comments order by TABLE_NAME asc");
		
		PreparedStatement pstm = connection.prepareStatement(sb.toString());
		ResultSet rs = pstm.executeQuery();
		while(rs.next()){
			String tableName = rs.getString("TABLE_NAME").toUpperCase();
			String tableDesc = StringUtil.trim(rs.getString("COMMENTS"));
			columnMap.put(tableName, tableDesc);
		}
	    rs.close();
	    pstm.close();
		
		return columnMap;
	}
	
	/**
	 * 取得表字段的备注信息
	 */
	private LinkedHashMap<String, String> getColumnComments(String tableName)throws Exception{
		LinkedHashMap<String, String> columnMap = new LinkedHashMap<String, String>();
		
		StringBuffer sb = new StringBuffer();
		sb.append(" select TABLE_NAME,COLUMN_NAME,COMMENTS from user_col_comments ");
		sb.append(" where upper(TABLE_NAME)=upper('" + tableName + "') ");
		
		PreparedStatement pstm = connection.prepareStatement(sb.toString());
		ResultSet rs = pstm.executeQuery();
		while(rs.next()){
			columnMap.put(rs.getString("COLUMN_NAME").toUpperCase(), rs.getString("COMMENTS"));
		}
	    rs.close();
	    pstm.close();
		
		return columnMap;
	}
	
	/**
	 * 获取表的主键字段
	 * @param tableName 表名
	 */
	private String getTablePrimaryKeys(String tableName){
		try{
			DatabaseMetaData dbmd = connection.getMetaData();
			ResultSet rs = dbmd.getPrimaryKeys(null, schemaPattern, tableName);
			StringBuffer sb = new StringBuffer(",");
			while(rs.next()){
				sb.append(rs.getString("COLUMN_NAME").toUpperCase() + ",");
			}
			rs.close();
			return sb.toString();
		}catch(Exception ex){
			return "";
		}
	}
	
	@Override
	public void exportMetadata() throws Exception {
		if(metadataList != null && metadataList.size() > 0){
			WordCreater creater = new MetadataWordCreater(outputFile, getTableHeaderList(), metadataList);
			creater.setOrientation(WordCreater.ORIENTATION_PORTRAIT);
			creater.setAutoColumnWidth(false);
			creater.setIncludeSequenceColumn(false);
			creater.create();
		}
	}
	
	@Override
	public List<TableHeaderBean> getTableHeaderList() {
		List<TableHeaderBean> headerList = new ArrayList<TableHeaderBean>();
		headerList.add(new TableHeaderBean("f1", "字段名", 100));
		headerList.add(new TableHeaderBean("f2", "字段中文名", 120));
		headerList.add(new TableHeaderBean("f3", "字段类型", 80));
		headerList.add(new TableHeaderBean("f4", "主键", 35));
		headerList.add(new TableHeaderBean("f5", "可空", 35));
		headerList.add(new TableHeaderBean("f6", "备注", 170));
		return headerList;
	}
	
	public static void main(String[] args) {
		try{
			System.out.println("start...");
			
			String driver = "oracle.jdbc.driver.OracleDriver";
	   		String url = "jdbc:oracle:thin:@120.25.81.131:1521:POD";
	   		String uid = "diyou";
	   		String pwd = "123456";

	   		Class.forName(driver);
		   	Connection connection = DriverManager.getConnection(url, uid, pwd);
		   	
			OracleMetadataCreator creator = new OracleMetadataCreator(connection, "DIYOU");
			creator.execute();
			
			System.out.println("end");
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
