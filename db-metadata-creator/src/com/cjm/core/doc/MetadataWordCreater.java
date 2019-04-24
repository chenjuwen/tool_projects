package com.cjm.core.doc;

import java.util.List;
import java.util.Map;

import com.cjm.databasemetadata.bean.MetadataBean;
import com.cjm.databasemetadata.bean.TableHeaderBean;
import com.jacob.com.Dispatch;

public class MetadataWordCreater extends AbstractWordCreater {
	private static final String titleFontSize = "12";
	private static final String dataFontSize = "10";
	private static final String fontName = "宋体";
	
	private String filePath;
	private List<TableHeaderBean> headerList;
	private List<MetadataBean> metaDataBeanList;
	
	public MetadataWordCreater(String filePath, List<TableHeaderBean> headerList, 
			List<MetadataBean> metaDataBeanList){
		this.filePath = filePath;
		this.headerList = headerList;
		this.metaDataBeanList = metaDataBeanList;
	}

	@Override
	protected void doCreater(){
		//创建表名的列表清单
		int rows = metaDataBeanList.size();
		
		setFont(false, false, false, dataFontSize, fontName);
		
		//行数 = 表数量行数 + 一行标题行
		//列数：序号、表名、功能说明
		Dispatch table1 = createTable(rows+1, 3, true);
		
		setRowBgColor(table1, 1, 16); //行背景
		
		//设置单元格内容和样式
		setCellContent(table1, 1, 1, "序号");
		setFont(false, false, false, dataFontSize, fontName);
		setAlignment(ALIGN_CENTER);
		setColumnWidth(table1, 1, 40);
		
		setCellContent(table1, 1, 2, "表名");
		setFont(false, false, false, dataFontSize, fontName);
		setAlignment(ALIGN_CENTER);
		setColumnWidth(table1, 2, 180);
		
		setCellContent(table1, 1, 3, "说明");
		setFont(false, false, false, dataFontSize, fontName);
		setAlignment(ALIGN_CENTER);
		setColumnWidth(table1, 3, 250);
		
		for(int i=0;i<metaDataBeanList.size();i++){
			MetadataBean bean = metaDataBeanList.get(i);
			
			setCellContent(table1, i+2, 1, String.valueOf(i+1));
			setFont(false, false, false, dataFontSize, fontName);
			setAlignment(ALIGN_CENTER);
			
			setCellContent(table1, i+2, 2, bean.getTableName());
			setFont(false, false, false, dataFontSize, fontName);
			setAlignment(ALIGN_LEFT);
			
			setCellContent(table1, i+2, 3, bean.getTableComnent());
			setFont(false, false, false, dataFontSize, fontName);
			setAlignment(ALIGN_LEFT);
		}
		moveDown();
		newLine();
		moveDown();
		
		//生成每个表结构的table数据
		for(int t=0;t<metaDataBeanList.size();t++){
			MetadataBean bean = metaDataBeanList.get(t);
			
			System.out.println((t+1) + "、" + bean.getTableName() + ", " + bean.getTableComnent());
			
			int totalRow = bean.getFieldsList().size(); //表元数据的行数
			int totalCol = headerList.size(); //表元数据的列数
			
			int seqCount = 0;
			if(isShowSequenceColumn()){ //显示序号列
				++totalCol; //列数要再加1
				seqCount = 1; //实际写数据的列号需要往右移一列
			}
			
			//标题
			//insertText((t+1) + "、" + bean.getTableName() + "：" + bean.getTableComnent());
			insertText(bean.getTableName() + "：" + bean.getTableComnent());
			setFont(true, false, false, titleFontSize, fontName);
			setAlignment(ALIGN_LEFT);
			moveRight();
			newLine();
			
			setFont(false, false, false, dataFontSize, fontName);
			
			//创建表格
			Dispatch table = createTable(totalRow+1, totalCol, true);
			
			//表头
			setRowBgColor(table, 1, 16); //背景
			if(isShowSequenceColumn()){
				setCellContent(table, 1, 1, "序号");
				setFont(true, false, false, dataFontSize, fontName);
				setAlignment(ALIGN_CENTER);
				setColumnWidth(table, 1, 40);
			}
			for(int i=0;i<headerList.size();i++){
				TableHeaderBean md = headerList.get(i);
				setCellContent(table, 1, i+1+seqCount, md.getLabel());
				setFont(true, false, false, dataFontSize, fontName);
				setAlignment(ALIGN_CENTER);
				setColumnWidth(table, i+1+seqCount, md.getWidth());
			}
			
			//字段明细
			for(int row=0;row<bean.getFieldsList().size();row++){
				if(isShowSequenceColumn()){
					setCellContent(table, row+2, 1, String.valueOf(row+1));
					setFont(false, false, false, dataFontSize, fontName);
					setAlignment(ALIGN_CENTER);
				}
				
				Map<String, String> rowValue = (Map<String, String>)bean.getFieldsList().get(row);
				for(int col=0;col<headerList.size();col++){
					TableHeaderBean md = headerList.get(col);
					setCellContent(table, row+2, col+1+seqCount, (String)rowValue.get(md.getName()));
					setFont(false, false, false, dataFontSize, fontName);
					setAlignment(ALIGN_LEFT);
				}
			}
			
			moveDown();
			newLine();
			moveDown();
		}
		
		//文件另存为
		Dispatch.call(document, "SaveAs", filePath);
	}
	
}
