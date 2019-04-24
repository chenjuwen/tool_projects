package com.cjm.databasemetadata;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.cjm.core.config.Config;
import com.cjm.core.config.ConfigLoader;
import com.cjm.core.doc.MetadataWordCreater2;
import com.cjm.core.doc.WordCreater;
import com.cjm.databasemetadata.bean.MetadataBean;
import com.cjm.databasemetadata.bean.MysqlMetadataBean;
import com.cjm.databasemetadata.bean.TableHeaderBean;
import com.cjm.utils.StringUtil;

/**
 * 生成Mysql数据库表的数据字典
 */
public class MysqlMetadataCreator extends AbstractMetadataCreator {
	private String tableSchema;
	
	public MysqlMetadataCreator(Connection connection, String tableSchema, String outputFile){
		this.connection = connection;
		this.tableSchema = tableSchema;
		this.outputFile = outputFile;
	}
	
	private boolean checkTable(String tableName){
		if(tableName.startsWith("know_")){
			return true;
		}
		return false;
	}
	
	@Override
	public List<MetadataBean> generateMetadataList() throws Exception {
		List<MetadataBean> beanList = new ArrayList<MetadataBean>();
		
		try{
			LinkedHashMap<String, String> tableMap = getTableComments();
			
			int tableIndex = 0;
			for(Iterator<Entry<String, String>> it=tableMap.entrySet().iterator(); it.hasNext();){
				String tableName = it.next().getKey();
				if(!checkTable(tableName)){
					continue;
				}
				
				System.out.println(++tableIndex + ". " + tableName + " doing...");
				
				//表字段元数据信息
				List<MysqlMetadataBean> columnList = getColumnMetadataList(tableName);
				
				//表备注
				String tableComment = StringUtil.trim(tableMap.get(tableName));
				if(StringUtil.isEmpty(tableComment) && listener != null){
					tableComment = StringUtil.trim(listener.getTableComment(tableName));
				}
				
				MetadataBean bean = new MetadataBean();
				bean.setTableName(tableName);
				bean.setTableComnent(tableComment);
				
				int count  = columnList.size();
				for(int i=0; i<count; i++){
					MysqlMetadataBean fieldBean = columnList.get(i);
					String columnName = fieldBean.getColumnName();
					
					Map<String, String> fieldMap = new HashMap<String, String>();

					//字段名
					fieldMap.put("f1", columnName);
					
					//字段备注
					String columnComment = StringUtil.trim(fieldBean.getColumnComment());
					if(StringUtil.isEmpty(columnComment) && listener != null){
						columnComment = StringUtil.trim(listener.getColumnComment(tableName, columnName));
					}
					fieldMap.put("f2", columnComment);
					
					//字段类型
					fieldMap.put("f3", fieldBean.getColumnType());
					
					//是否主键
					if("PRI".equalsIgnoreCase(fieldBean.getColumnKey())){
						fieldMap.put("f4", "Y");
					}else{
						fieldMap.put("f4", "");
					}
					
					//是否可空
					fieldMap.put("f5", ("NO".equalsIgnoreCase(fieldBean.getIsNullable()))?"N":"");
					
					//备注
					fieldMap.put("f6", StringUtil.isNotEmpty(StringUtil.trim(fieldBean.getColumnDefault()))? ("默认值为"+fieldBean.getColumnDefault()) : "");
					
					bean.addField(fieldMap);
				}
				
				beanList.add(bean);
			}
			
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
		sb.append("SELECT table_name,table_comment FROM INFORMATION_SCHEMA.TABLES WHERE table_schema='" + tableSchema + "'");
		
		PreparedStatement pstm = connection.prepareStatement(sb.toString());
		ResultSet rs = pstm.executeQuery();
		while(rs.next()){
			String tableName = rs.getString("table_name");
			String tableComment = StringUtil.trim(rs.getString("table_comment"));
			columnMap.put(tableName, tableComment);
		}
	    rs.close();
	    pstm.close();
		
		return columnMap;
	}
	
	/**
	 * 取得表字段的元数据信息
	 */
	private List<MysqlMetadataBean> getColumnMetadataList(String tableName)throws Exception{
		List<MysqlMetadataBean> dataList = new ArrayList<>();
		
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT column_name,column_type,column_default, ");
		sb.append(" column_key,is_nullable,column_comment ");
		sb.append(" FROM INFORMATION_SCHEMA.Columns ");
		sb.append(" WHERE table_schema='" + tableSchema + "' ");
		sb.append(" AND table_name='" + tableName + "' ");
		sb.append(" ORDER BY ordinal_position ASC ");
		
		PreparedStatement pstm = connection.prepareStatement(sb.toString());
		ResultSet rs = pstm.executeQuery();
		while(rs.next()){
			MysqlMetadataBean bean = new MysqlMetadataBean();
			bean.setColumnName(rs.getString("column_name"));
			bean.setColumnType(rs.getString("column_type"));
			bean.setColumnDefault(rs.getString("column_default"));
			bean.setColumnKey(rs.getString("column_key"));
			bean.setIsNullable(rs.getString("is_nullable"));
			bean.setColumnComment(rs.getString("column_comment"));
			dataList.add(bean);
		}
	    rs.close();
	    pstm.close();
		return dataList;
	}
	
	@Override
	public void exportMetadata() throws Exception {
		if(metadataList != null && metadataList.size() > 0){
			WordCreater creater = new MetadataWordCreater2(outputFile, getTableHeaderList(), metadataList);
			creater.setOrientation(WordCreater.ORIENTATION_PORTRAIT);
			creater.setAutoColumnWidth(false);
			creater.setIncludeSequenceColumn(false);
			creater.create();
		}
	}
	
	@Override
	public List<TableHeaderBean> getTableHeaderList() {
		List<TableHeaderBean> headerList = new ArrayList<TableHeaderBean>();
		headerList.add(new TableHeaderBean("f1", "字段名", 2200));
		headerList.add(new TableHeaderBean("f2", "字段中文名", 2200));
		headerList.add(new TableHeaderBean("f3", "字段类型", 1500));
		headerList.add(new TableHeaderBean("f4", "主键", 700));
		headerList.add(new TableHeaderBean("f5", "可空", 700));
		headerList.add(new TableHeaderBean("f6", "备注", 3500));
		return headerList;
	}
	
	public static void main(String[] args) {
		try{
			System.out.println("start...");
			
			//load config
			Config config = ConfigLoader.load();

			//parse data
			MysqlMetadataListener listner = new MysqlMetadataListener(config.getQuickCreateDataPath());
			
	   		Class.forName(config.getDriver());
		   	Connection connection = DriverManager.getConnection(config.getUrl(), config.getUid(), config.getPwd());
		   	
		   	//create and export table metadata
			MysqlMetadataCreator creator = new MysqlMetadataCreator(connection, config.getTableSchema(), config.getOutputFile());
			creator.setListener(listner);
			creator.execute();
			
			System.out.println("end");
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
