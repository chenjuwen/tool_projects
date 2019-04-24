package com.cjm.core.doc;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;

import com.cjm.databasemetadata.bean.MetadataBean;
import com.cjm.databasemetadata.bean.TableHeaderBean;
import com.cjm.utils.StringUtil;

/**
 * 基于POI实现word文档的创建
 */
public class MetadataWordCreater2 implements WordCreater {
	private boolean includeSequenceColumn = false;
	
	private XWPFDocument doc;

	private String fileSavePath;
	private List<TableHeaderBean> headerList;
	private List<MetadataBean> metaDataBeanList;
	
	public MetadataWordCreater2(String fileSavePath, List<TableHeaderBean> headerList, 
			List<MetadataBean> metaDataBeanList){
		this.fileSavePath = fileSavePath;
		this.headerList = headerList;
		this.metaDataBeanList = metaDataBeanList;
	}

	@Override
	public void create() throws Exception {
		init();
		
		//表数量
		int rows = metaDataBeanList.size();
		
		//创建表名列表
		createTablenameList(rows);
		
		//生成每个表结构的table数据
		for(int t=0; t<metaDataBeanList.size(); t++){
			MetadataBean bean = metaDataBeanList.get(t);
			
			System.out.println((t+1) + "、" + bean.getTableName() + ", " + bean.getTableComnent());
			
			int totalRow = bean.getFieldsList().size(); //表元数据的行数
			int totalCol = headerList.size(); //表元数据的列数
			
			//标题
			createTableTitle(bean.getTableName() + "：" + bean.getTableComnent());
			
			//创建表格
			XWPFTable dataTable = createTable(totalRow+1, totalCol);
			
			//表头
			XWPFTableRow tableRow = dataTable.getRow(0);
			for(int i=0; i<headerList.size(); i++){
				TableHeaderBean md = headerList.get(i);
				setCellContent(tableRow.getCell(i), md.getLabel());
				tableRow.getCell(i).setColor("CCCCCC");
				setCellStyle(tableRow.getCell(i), String.valueOf(md.getWidth()), STVerticalJc.CENTER, STJc.CENTER);
			}
			
			//字段明细
			for(int row=0; row<bean.getFieldsList().size(); row++){
				Map<String, String> rowValue = (Map<String, String>)bean.getFieldsList().get(row);
				XWPFTableRow fieldRow = dataTable.getRow(row+1);
				
				for(int col=0; col<headerList.size(); col++){
					TableHeaderBean md = headerList.get(col);
					setCellContent(fieldRow.getCell(col), (String)rowValue.get(md.getName()));
					setCellStyle(fieldRow.getCell(col), "", STVerticalJc.CENTER, STJc.LEFT);
				}
			}
		}
		
		saveFile();
	}

	private void init() {
		doc = new XWPFDocument();

		//设置页边距
		CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
		CTPageMar pageMar = sectPr.addNewPgMar();
		pageMar.setLeft(BigInteger.valueOf(720L));
		pageMar.setRight(BigInteger.valueOf(720L));
		pageMar.setTop(BigInteger.valueOf(1440L));
		pageMar.setBottom(BigInteger.valueOf(1440L));
		
		//标题
		XWPFParagraph p = doc.createParagraph();
		p.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun run = p.createRun();
		run.setText("数据库字典");
		run.setBold(true);
		run.setFontSize(18);
		
		newLine(run);
	}

	private void createTablenameList(int rows) {
		XWPFTable table = createTable(rows+1, 2);
		
		//表头
		table.getRow(0).getCell(0).setText("表名");
		table.getRow(0).getCell(1).setText("说明");
		table.getRow(0).getCell(0).setColor("CCCCCC");
		table.getRow(0).getCell(1).setColor("CCCCCC");
		setCellStyle(table.getRow(0).getCell(0), "3000", STVerticalJc.CENTER, STJc.CENTER);
		setCellStyle(table.getRow(0).getCell(1), "8000", STVerticalJc.CENTER, STJc.CENTER);
		
		//表行数据
		for(int i=0; i<metaDataBeanList.size(); i++){
			MetadataBean bean = metaDataBeanList.get(i);
			
			XWPFTableRow row = table.getRow(i+1);
			
			setCellContent(row.getCell(0), bean.getTableName());
			setCellStyle(row.getCell(0), "", STVerticalJc.CENTER, STJc.LEFT);
			
			setCellContent(row.getCell(1), bean.getTableComnent());
			setCellStyle(row.getCell(1), "", STVerticalJc.CENTER, STJc.LEFT);
		}
	}
	
	private void createTableTitle(String text){
		XWPFParagraph p = doc.createParagraph();
		p.setAlignment(ParagraphAlignment.LEFT);
		
		XWPFRun run = p.createRun();
		newLine(run);
		run.setText(text);
		run.setBold(true);
		run.setColor("000000");
	}

	private void saveFile() throws FileNotFoundException, IOException {
		FileOutputStream out = new FileOutputStream(fileSavePath);
		doc.write(out);
		out.close();
	}
	
	private XWPFTable createTable(int rows, int cols){
		XWPFTable table = doc.createTable(rows, cols);
		return table;
	}
	
	/**
	 * 设置单元格的内容
	 */
	private void setCellContent(XWPFTableCell cell, String content){
		cell.setText(content);
	}
	
	/**
	 * 设置单元格的样式
	 * @param cell 单元格
	 * @param width 宽度
	 * @param vAlign 垂直对齐
	 * @param align 水平对齐
	 */
	private void setCellStyle(XWPFTableCell cell, String width, STVerticalJc.Enum vAlign, STJc.Enum align) {
		//垂直对齐
		CTTcPr cellPr = cell.getCTTc().getTcPr() != null ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
		cellPr.addNewVAlign().setVal(vAlign);
		
		//水平对齐
		CTTc cttc = cell.getCTTc();
		cttc.getPList().get(0).addNewPPr().addNewJc().setVal(align);
		
		//单元格宽度
		CTTblWidth tblWidth = cellPr.isSetTcW() ? cellPr.getTcW() : cellPr.addNewTcW();
		if(StringUtil.isNotEmpty(width)){
			tblWidth.setW(new BigInteger(width));
			tblWidth.setType(STTblWidth.DXA);
		}
	}
	
	protected void newLine(XWPFRun run){
		run.addCarriageReturn(); //换行
	}
	
	@Override
	public void setAutoColumnWidth(boolean value) {
		
	}
	
	@Override
	public void setIncludeSequenceColumn(boolean value) {
		
	}
	
	@Override
	public void setOrientation(int value) {
		
	}
	
}
