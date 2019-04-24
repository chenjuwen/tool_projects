package com.cjm.core.doc;

public interface WordCreater extends DocCreater {
	public static final String ALIGN_LEFT = "3"; //左对齐
	public static final String ALIGN_CENTER = "1"; //居中
	public static final String ALIGN_RIGHT = "2"; //右对齐
	
	public static final int ORIENTATION_LANDSCAPE = 1; //横向
	public static final int ORIENTATION_PORTRAIT = 0; //纵向
	
	public static final double BASE_SIDE_SPACE = 30; //页边距基数值，大概相当于1厘米

	public void setOrientation(int value); //设置页面方向
	public void setAutoColumnWidth(boolean value); //自动调整表格列的宽度
	public void setIncludeSequenceColumn(boolean value); //是否包含序号列
}