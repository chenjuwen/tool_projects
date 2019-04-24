package com.cjm.qc;

import java.util.LinkedHashMap;

/**
 * 模块模型类
 */
public class QCDataModule {
	private String moduleName; //模块名
	private String moduleChineseName; //模块中文名
	private LinkedHashMap<String, QCDataClass> dataClassMap = new LinkedHashMap<>(); //对象集合，key=className
	
	public QCDataModule(String moduleName){
		this.moduleName = moduleName;
	}
	
	public String getModuleName() {
		return moduleName;
	}
	
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public String getModuleChineseName() {
		return moduleChineseName;
	}
	
	public void setModuleChineseName(String moduleChineseName) {
		this.moduleChineseName = moduleChineseName;
	}

	public LinkedHashMap<String, QCDataClass> getDataClassMap() {
		return dataClassMap;
	}
	
	public QCDataClass getDataClass(String className){
		return dataClassMap.get(className);
	}

	public void addDataClass(QCDataClass dataClass) {
		this.dataClassMap.put(dataClass.getClassName(), dataClass);
	}

	public void removeDataClass(String instanceName) {
		this.dataClassMap.remove(instanceName);
	}
	
}
