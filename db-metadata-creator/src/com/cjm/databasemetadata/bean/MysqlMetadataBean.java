package com.cjm.databasemetadata.bean;

public class MysqlMetadataBean {
	private String columnName;
	private String columnType;
	private String columnDefault;
	private String columnKey;
	private String isNullable;
	private String columnComment;
	
	public String getColumnName() {
		return columnName;
	}
	
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	public String getColumnType() {
		return columnType;
	}
	
	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}
	
	public String getColumnDefault() {
		return columnDefault;
	}
	
	public void setColumnDefault(String columnDefault) {
		this.columnDefault = columnDefault;
	}
	
	public String getColumnKey() {
		return columnKey;
	}
	
	public void setColumnKey(String columnKey) {
		this.columnKey = columnKey;
	}
	
	public String getIsNullable() {
		return isNullable;
	}
	
	public void setIsNullable(String isNullable) {
		this.isNullable = isNullable;
	}
	
	public String getColumnComment() {
		return columnComment;
	}
	
	public void setColumnComment(String columnComment) {
		this.columnComment = columnComment;
	}
	
}
