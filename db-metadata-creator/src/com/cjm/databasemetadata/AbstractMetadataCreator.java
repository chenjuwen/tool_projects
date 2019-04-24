package com.cjm.databasemetadata;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.cjm.databasemetadata.bean.MetadataBean;
import com.cjm.databasemetadata.bean.TableHeaderBean;

public abstract class AbstractMetadataCreator {
	protected MetadataListener listener;
	protected Connection connection = null;
	protected String outputFile;
	protected List<MetadataBean> metadataList;
	
	protected List<String> STRING_TYPES = new ArrayList<String>();
	protected List<String> NUMBER_TYPES = new ArrayList<String>();
	
	public AbstractMetadataCreator(){
		initColumnTypes();
	}
	
	private void initColumnTypes(){
		STRING_TYPES.add("CHAR");
		STRING_TYPES.add("NCHAR");
		STRING_TYPES.add("NVARCHAR");
		STRING_TYPES.add("VARCHAR");
		STRING_TYPES.add("VARCHAR2");
		
		NUMBER_TYPES.add("NUMBER");
		NUMBER_TYPES.add("NUMERIC");
		NUMBER_TYPES.add("DOUBLE");
		NUMBER_TYPES.add("FLOAT");
		NUMBER_TYPES.add("SMALLINT");
		NUMBER_TYPES.add("INT");
		NUMBER_TYPES.add("BIGINT");
		NUMBER_TYPES.add("BIGINT");
	}
	
	protected boolean isStringType(String fieldType){
		return STRING_TYPES.contains(fieldType.toUpperCase());
	}
	
	protected boolean isNumberType(String fieldType){
		return NUMBER_TYPES.contains(fieldType.toUpperCase());
	}
	
	public abstract List<TableHeaderBean> getTableHeaderList();
	
	public final void execute() throws Exception{
		metadataList = generateMetadataList();
		exportMetadata();
	}
	
	/**
	 * 生成元数据信息
	 */
	public abstract List<MetadataBean> generateMetadataList() throws Exception;
	
	/**
	 * 导出元数据信息
	 */
	public abstract void exportMetadata() throws Exception;

	public String getOutputFile() {
		return outputFile;
	}

	public void setListener(MetadataListener listener) {
		this.listener = listener;
	}
	
}
