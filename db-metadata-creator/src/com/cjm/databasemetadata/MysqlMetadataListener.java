package com.cjm.databasemetadata;
import com.cjm.qc.DataFactory;
import com.cjm.qc.QCDataService;
import com.cjm.utils.StringUtil;

public class MysqlMetadataListener implements MetadataListener {
	public MysqlMetadataListener(String quickCreateDataPath){
		try{
			if(StringUtil.isNotEmpty(quickCreateDataPath)){
				QCDataService dataService = new QCDataService(quickCreateDataPath);
				dataService.parse();
			}
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public String getTableComment(String tableName) {
		return DataFactory.getTableComment(tableName);
	}

	@Override
	public String getColumnComment(String tableName, String columnName) {
		return DataFactory.getFieldComment(tableName, columnName);
	}

}
