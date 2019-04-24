package com.cjm.qc;

import java.util.LinkedHashMap;

/**
 * 类模型类
 */
public class QCDataClass {
	private String className; //类名
	private String classChineseName; //类中文名
	private String tableName; //表名
	private LinkedHashMap<String, String> fieldsMap = new LinkedHashMap<>(); //字段集合：字段名，字段描述
	
	public QCDataClass(String className, String classChineseName, String tableName){
		this.className = className;
		this.classChineseName = classChineseName;
		this.tableName = tableName;
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassChineseName() {
		return classChineseName;
	}

	public void setClassChineseName(String classChineseName) {
		this.classChineseName = classChineseName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public LinkedHashMap<String, String> getFieldsMap() {
		return fieldsMap;
	}

	public void addField(String fieldName, String fieldDesc){
		fieldsMap.put(fieldName, fieldDesc);
	}
	
}
