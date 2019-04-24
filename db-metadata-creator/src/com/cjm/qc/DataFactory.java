package com.cjm.qc;

import java.util.LinkedHashMap;

public class DataFactory {
	private static LinkedHashMap<String, QCDataModule> modulesMap = new LinkedHashMap<>(); //模块集合
	private static LinkedHashMap<String, QCDataClass> tablesMap = new LinkedHashMap<>(); //表集合，key=tableName
	
	/**
	 * 添加模块
	 * @param module 模块对象
	 */
	public static void addModule(QCDataModule module){
		if(!modulesMap.containsKey(module.getModuleName())){
			modulesMap.put(module.getModuleName(), module);
		}
	}
	
	/**
	 * 设置模块的中文名
	 * @param moduleName 模块名
	 * @param moduleComment 模块中文名
	 */
	public static void setModuleComment(String moduleName, String moduleComment){
		if(modulesMap.containsKey(moduleName)){
			modulesMap.get(moduleName).setModuleChineseName(moduleComment);
		}
	}

	/**
	 * 根据模块名取得一个模块对象
	 * @param moduleName 模块名
	 */
	public static QCDataModule getDataModule(String moduleName){
		return modulesMap.get(moduleName);
	}

	/**
	 * 获取表的中文名称
	 * @param tableName 表名
	 */
	public static String getTableComment(String tableName){
		QCDataClass dataClass = tablesMap.get(tableName);
		if(dataClass != null){
			return dataClass.getClassChineseName();
		}
		return "";
	}

	/**
	 * 获取字段的中文名称
	 * @param tableName 表名
	 * @param fieldName 字段名
	 */
	public static String getFieldComment(String tableName, String fieldName){
		QCDataClass dataClass = tablesMap.get(tableName);
		if(dataClass != null){
			return dataClass.getFieldsMap().get(fieldName);
		}
		return "";
	}
	
	/**
	 * 添加一个QCDataClass对象到tablesMap集合
	 */
	public static void addTable(QCDataClass dataClass){
		if(!tablesMap.containsKey(dataClass.getTableName())){
			tablesMap.put(dataClass.getTableName(), dataClass);
		}
	}
	
}
