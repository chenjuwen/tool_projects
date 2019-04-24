package com.cjm.qc;
import java.io.File;
import java.io.FileInputStream;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class DataUtil {
	/**
	 * 加载xml文件
	 * @param filePath 文件路径
	 */
	public static Document readFile(String filePath) throws Exception {
		SAXReader reader = new SAXReader();  
		Document document = reader.read(new FileInputStream(new File(filePath)));
		return document;
	}
	
	/**
	 * 获取某个节点的文本信息
	 * @param document 
	 * @param nodePath 节点全路径，比如 /字段数据/基本数据/表名
	 */
	public static String getNodeText(Document document, String nodePath){
		Node node = document.selectSingleNode(nodePath);
		if(node != null){
			return node.getText();
		}
		return "";
	}

	/**
	 * 获取某个节点的文本信息
	 * @param element 
	 * @param nodeName 节点名，比如 字段
	 */
	public static String getNodeText(Element element, String nodeName){
		Node node = element.selectSingleNode(nodeName);
		if(node != null){
			return node.getText();
		}
		return "";
	}
	
}
