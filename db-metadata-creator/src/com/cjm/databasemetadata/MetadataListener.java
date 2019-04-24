package com.cjm.databasemetadata;

public interface MetadataListener {
	public String getTableComment(String tableName);
	public String getColumnComment(String tableName, String columnName);
}
