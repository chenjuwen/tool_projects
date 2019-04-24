package com.cjm.databasemetadata.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 表的元数据信息
 */
public class MetadataBean {
	private String tableName;
	private String tableComnent;
	private List<Map<String, String>> fieldsList = new ArrayList<Map<String,String>>();
	
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getTableComnent() {
		return tableComnent;
	}
	
	public void setTableComnent(String tableComnent) {
		this.tableComnent = tableComnent;
	}
	
	public List<Map<String, String>> getFieldsList() {
		return fieldsList;
	}
	
	public void addField(Map<String, String> fieldMap){
		fieldsList.add(fieldMap);
	}
	
}
