package com.cjm.qc;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

public class QCDataService {
	private String rootPath;
	
	public QCDataService(String rootPath){
		this.rootPath = rootPath;
	}
	
	public void parse() throws Exception {
		parseModule();
	}
	
	/**
	 * 解析模块目录
	 */
	private void parseModule() throws Exception {
		File rootDir = new File(rootPath);
		File[] files = rootDir.listFiles();
		for(File file : files){
			if(file.isDirectory()){
				String moduleName = file.getName();
				DataFactory.addModule(new QCDataModule(moduleName));
				System.out.println(file.getPath());
				
				parseClassFile(moduleName, file);
			}
		}
	}
	
	/**
	 * 解析模块目录的xml文件
	 * @param moduleName 模块名
	 * @param file 模块目录File对象
	 */
	private void parseClassFile(String moduleName, File file) throws Exception {
		File[] files = file.listFiles();
		for(File f : files){
			if(".xml".equalsIgnoreCase(f.getName()) || !f.getName().endsWith(".xml")){
				continue;
			}
			
			if(!f.getName().startsWith("know")){
				continue;
			}
			
			parseFile(f);
			System.out.println("\t" + f.getPath());
		}
	}
	
	/**
	 * 解析xml文件
	 * @param file 文件对象
	 */
	private void parseFile(File file) throws Exception {
		Document document = DataUtil.readFile(file.getPath());

		String className = DataUtil.getNodeText(document, "/字段数据/基本数据/实例名");
		String classChineseName = DataUtil.getNodeText(document, "/字段数据/基本数据/中文名");
		String tableName = DataUtil.getNodeText(document, "/字段数据/基本数据/表名");
		String moduleName = DataUtil.getNodeText(document, "/字段数据/基本数据/模块名");
		String moduleChineseName = DataUtil.getNodeText(document, "/字段数据/基本数据/模块中文名");
		
		DataFactory.setModuleComment(moduleName, moduleChineseName);
		
		QCDataClass dataClass = new QCDataClass(className, classChineseName, tableName);
		
		List nodeList = document.selectNodes("/字段数据/字段列表/字段");
		if(nodeList != null){
			Iterator it = nodeList.iterator();
			while(it.hasNext()){
				Element element = (Element)it.next();
				String fieldName = DataUtil.getNodeText(element, "字段");
				String fieldComment = DataUtil.getNodeText(element, "描述");
				dataClass.addField(fieldName, fieldComment);
			}
		}
		
		DataFactory.getDataModule(moduleName).addDataClass(dataClass);
		DataFactory.addTable(dataClass);
	}
	
}
