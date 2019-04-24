package com.cjm.databasemetadata.bean;

/**
 * 元数据表格header信息
 */
public class TableHeaderBean {
	private String name; //字段名
	private String label; //字段标签
	private int width; //字段宽度

	public TableHeaderBean(){

	}
	
	public TableHeaderBean(String name, String label, int width){
		this.name = name;
		this.label = label;
		this.width = width;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

}