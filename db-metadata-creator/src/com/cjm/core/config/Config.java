package com.cjm.core.config;

public class Config {
	private String driver = "com.mysql.jdbc.Driver";
	private String url;
	private String uid;
	private String pwd;
	private String tableSchema;
	private String outputFile = "c:\\meatdata.doc";
	private String quickCreateDataPath;
	
	public String getDriver() {
		return driver;
	}
	
	public void setDriver(String driver) {
		this.driver = driver;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUid() {
		return uid;
	}
	
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public String getPwd() {
		return pwd;
	}
	
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	public String getTableSchema() {
		return tableSchema;
	}
	
	public void setTableSchema(String tableSchema) {
		this.tableSchema = tableSchema;
	}
	
	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public String getQuickCreateDataPath() {
		return quickCreateDataPath;
	}
	
	public void setQuickCreateDataPath(String quickCreateDataPath) {
		this.quickCreateDataPath = quickCreateDataPath;
	}
	
}
