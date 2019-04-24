package com.cjm.core.doc;

import com.cjm.utils.StringUtil;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public abstract class AbstractWordCreater implements WordCreater {
	protected ActiveXComponent word;
	protected Dispatch documents;
	protected Dispatch document;
	protected Dispatch selection;
	protected Dispatch alignment;
	protected Dispatch font;
	
	private int orientation = ORIENTATION_PORTRAIT;
	private boolean autoColumnWidth = false;
	private boolean includeSequenceColumn = false;
	
	private void init(){
		if(word==null){
			//创建一个word对象
			ComThread.InitSTA();
			word = new ActiveXComponent("Word.Application");
			word.setProperty("Visible", new Variant(false)); //word不可见
			word.setProperty("AutomationSecurity", new Variant(3)); //禁用宏
		}
		
		if(documents==null){
			//获取文挡属性
			documents = word.getProperty("Documents").toDispatch();
			
			//添加一个新文挡
			document = Dispatch.call(documents, "Add").toDispatch(); 
			selection = word.getProperty("Selection").toDispatch();
			
			alignment = Dispatch.get(selection, "ParagraphFormat").toDispatch(); 
			font = Dispatch.get(selection, "Font").toDispatch();
		}
		
		//设置页边距
		Dispatch pageSetup = Dispatch.get(this.document, "PageSetup").toDispatch();
		Dispatch.put(pageSetup, "Orientation", orientation);
		Dispatch.put(pageSetup, "LeftMargin", BASE_SIDE_SPACE*1); //左边距
		Dispatch.put(pageSetup, "RightMargin", BASE_SIDE_SPACE*1); //右边距
		Dispatch.put(pageSetup, "TopMargin", BASE_SIDE_SPACE*1.5); //上边距
		Dispatch.put(pageSetup, "BottomMargin", BASE_SIDE_SPACE*1.5); //下边距
	}
	
	private void destroy(){
		try{
			if(document!=null){
				Dispatch.call(document, "Close", new Variant(true));
				document = null;
				selection = null;
				alignment = null;
				font = null;
			}
			
			if(word!=null){
				word.invoke("Quit", new Variant[]{});
				ComThread.Release();
				word = null;
				documents = null;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 新建表格
	 */
	protected Dispatch createTable(int rowCount, int colCount, boolean showBorder){
		Dispatch tables = Dispatch.get(document, "Tables").toDispatch();
		Dispatch range = Dispatch.get(selection, "Range").toDispatch();
		Dispatch table = Dispatch.
			call(tables, "Add", range, new Variant(rowCount), new Variant(colCount), new Variant((showBorder)?1:0)).toDispatch();
		return table;
	}
	
	/**
	 * 获取文档的表格集合
	 */
	protected Dispatch getTables(){
		return Dispatch.get(document, "Tables").toDispatch();
	}
	
	/**
	 * 获取文档中表格的数量
	 */
	protected int getTablesCount(){
		int count = 0; 
        try{ 
            this.getTables(); 
        }catch(Exception ex){ 
            ex.printStackTrace();
        } 
        count = Dispatch.get(getTables(), "Count").toInt();
        return count; 
	}
	
	/**
	 * 获取指定表格
	 * @param tableIndex 表格索引(从1开始)
	 */
	protected Dispatch getTable(int tableIndex){
		return Dispatch.call(getTables(), "Item", new Variant(tableIndex)).toDispatch();
	}
	
	/**
	 * 获取表格行集
	 * @param tableIndex 表格索引(从1开始)
	 */
	protected Dispatch getRows(Dispatch table){
		return Dispatch.get(table, "Rows").toDispatch();
	}
	
	/**
	 * 获取表格的总行数
	 */
	protected int getRowsCount(Dispatch table){
		return Dispatch.get(getRows(table), "Count").getInt(); 
	}
	
	/**
	 * 获取表格的一行
	 * @param tableIndex 表格索引(从1开始)
	 * @param rowIndex 行索引(从1开始)
	 */
	protected Dispatch getRow(Dispatch table, int rowIndex){
		return Dispatch.call(getRows(table), "Item", new Variant(rowIndex)).toDispatch();
	}
	
	/**
	 * 设置行的高度
	 */
	protected void setRowHeight(Dispatch table, int rowIndex, int height){
		if(height<0) height = 0;
		Dispatch row = getRow(table, rowIndex);
		Dispatch.put(row, "Height", new Variant(height));
	}
	
	/**
	 * 设置行的背景色
	 */
	protected void setRowBgColor(Dispatch table, int rowIndex, int color){
		if(color<1 || color>16) color = 16;
		Dispatch shading = Dispatch.get(getRow(table, rowIndex), "Shading").toDispatch();
		Dispatch.put(shading, "BackgroundPatternColorIndex", new Variant(color)); 
	}
	
	/**
	 * 获取表格列集合
	 */
	protected Dispatch getColumns(Dispatch table){
		return Dispatch.get(table, "Columns").toDispatch();
	}
	
	/**
	 * 获取表格的总列数
	 */
	protected int getColumnsCount(Dispatch table){
		Dispatch columns = getColumns(table);
		return Dispatch.get(columns, "Count").toInt();
	}
	
	/**
	 * 获取表格的一列
	 */
	protected Dispatch getColumn(Dispatch table, int colIndex){
		Dispatch columns = getColumns(table);
		return Dispatch.call(columns, "Item", new Variant(colIndex)).toDispatch(); 
	}
	
	/**
	 * 设置列的宽度
	 */
	protected void setColumnWidth(Dispatch table, int colIndex, int width){
		if(autoColumnWidth) return;
		if(width<11) width = 11;
		Dispatch column = getColumn(table, colIndex);
		Dispatch.put(column, "Width", new Variant(width)); 
	}
	
	/**
	 * 设置整列的背景色
	 */
	protected void setColumnBgColor(Dispatch table, int colIndex, int color){
		if(color<1 || color>16) color = 16;
		Dispatch shading = Dispatch.get(getColumn(table, colIndex), "Shading").toDispatch();
		Dispatch.put(shading, "BackgroundPatternColorIndex", new Variant(color)); 
	}
	
	/**
	 * 获取表格的一个单元格
	 * @param tableIndex 表格索引(从1开始)
	 * @param rowIndex 行索引(从1开始)
	 * @param colIndex 列索引(从1开始)
	 */
	protected Dispatch getCell(Dispatch table, int rowIndex, int colIndex){
		return Dispatch.call(table, "Cell", new Variant(rowIndex), new Variant(colIndex)).toDispatch();
	}
	
	/**
	 * 获取单元格的内容
	 */
	protected String getCellContent(Dispatch table, int rowIndex, int colIndex){
		Dispatch cell = Dispatch.call(table, "Cell", new Variant(rowIndex), new Variant(colIndex)).toDispatch();
		Dispatch range = Dispatch.get(cell, "Range").toDispatch(); 
		return StringUtil.trim(Dispatch.get(range, "Text").toString());
	}
	
	/**
	 * 设置单元格的内容
	 */
	protected void setCellContent(Dispatch table, int rowIndex, int colIndex, String text){
		Dispatch cell = Dispatch.call(table, "Cell", new Variant(rowIndex), new Variant(colIndex)).toDispatch();
		Dispatch.call(cell, "Select");
		insertText(text);
	}
	
	/**
	 * 设置单元格的背景色
	 */
	protected void setCellBgColor(Dispatch table, int rowIndex, int colIndex, int color){
		if(color<1 || color>16) color = 16;
		Dispatch shading = Dispatch.get(getCell(table, rowIndex, colIndex), "Shading").toDispatch();
		Dispatch.put(shading, "BackgroundPatternColorIndex", new Variant(color)); 
	}
	
	/**
	 * 合并整行
	 */
	protected void mergeRow(Dispatch table, int rowIndex){
		Dispatch row = getRow(table, rowIndex);
		Dispatch cells = Dispatch.get(row, "Cells").toDispatch(); 
        Dispatch.call(cells, "Merge"); 
	}
	
	/**
	 * 合并整列
	 */
	protected void mergeColumn(Dispatch table, int colIndex){
		Dispatch column = getColumn(table, colIndex);
		Dispatch cells = Dispatch.get(column, "Cells").toDispatch();
        Dispatch.call(cells, "Merge"); 
	}
	
	/**
	 * 合并单元格
	 */
	protected void mergeCells(Dispatch table, int fromRowIndex, int toRowIndex, int fromColIndex, int toColIndex){
		Dispatch fromtCell = Dispatch.
			call(table, "Cell",new Variant(fromRowIndex), new Variant(fromColIndex)).toDispatch();
		Dispatch toCell = Dispatch.
    		call(table, "Cell",new Variant(toRowIndex), new Variant(toColIndex)).toDispatch();
		Dispatch.call(fromtCell, "Merge", toCell);
	}
	
	protected void setFont(boolean bold, boolean italic, boolean underline, String fontSize, String fontName){
		Dispatch.put(font, "Bold", (bold==true)?"1":"0"); //租体
		Dispatch.put(font, "Italic", (italic==true)?"1":"0"); //斜体
		Dispatch.put(font, "Underline", (underline==true)?"1":"0"); //下划线
		Dispatch.put(font, "Size", (StringUtil.isEmpty(fontSize))?"10":fontSize); //字体大小
		Dispatch.put(font, "Name", new Variant((StringUtil.isEmpty(fontName))?"宋体":fontName)); //字体名称
	}
	
	protected void setAlignment(String align){
		Dispatch.put(alignment, "Alignment", align); // (1:置中 2:靠右 3:靠左)
	}
	
	protected void insertText(String text){
		Dispatch.put(selection, "Text", text);
	}
	
	protected void newLine(){
		Dispatch.call(selection, "TypeParagraph"); //空一行段落
	}
	
	protected void moveDown(){
		Dispatch.call(selection, "MoveDown"); //游标往下一行
	}
	
	protected void moveRight(){
		Dispatch.call(selection, "MoveRight"); //光标到行末
	}
	
	public void create(){
		try{
			init();
			
			doCreater();
			
		}finally{
			destroy();
		}
	}
	
	protected abstract void doCreater();

	public void setOrientation(int value){
		this.orientation = value;
	}

	public void setAutoColumnWidth(boolean value) {
		this.autoColumnWidth = value;
	}

	public void setIncludeSequenceColumn(boolean value) {
		this.includeSequenceColumn = value;
	}

	public boolean isShowSequenceColumn() {
		return includeSequenceColumn;
	}
	
}