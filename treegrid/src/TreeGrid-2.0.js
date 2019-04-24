/**
 * @author 陈举民
 * @version 1.0
 * @link http://chenjumin.javaeye.com
 */
TreeGrid = function(_config){
	_dataPool = {}; //数据池
	var _seqRowIndex = 0; //行索引序列
	
	var s = "";

	var _root;
	var _selectedData = null;
	var _selectedId = null;
	var _selectedIndex = null;

	_config = _config || {};

	var expandLayer = _config.expandLayer || 1;
	var openIcon = (_config.openIcon || TreeGrid.OPEN_ICON);
	var closeIcon = (_config.closeIcon || TreeGrid.CLOSE_ICON);
	var leafIcon = (_config.leafIcon || TreeGrid.LEAF_ICON);

	//显示表头行
	drowHeader = function(){
		s += "<tr class='header' height='" + (_config.headerHeight || "25") + "'>";
		var cols = _config.columns;
		for(i=0;i<cols.length;i++){
			var col = cols[i];
			s += "<td align='" + (col.headerAlign || _config.headerAlign || "center") + "' width='" + (col.width || "") + "'>" + (col.headerText || "") + "</td>";
		}
		s += "</tr>";
	}
	
	//递归显示数据行
	drowData = function(){
		var rows = _config.data;
		var cols = _config.columns;
		drowRowData(rows, cols, 1, "");
	}
	
	//局部变量i、j必须要用 var 来声明，否则，后续的数据无法正常显示
	drowRowData = function(_rows, _cols, _level, _pid){
		var folderColumnIndex = (_config.folderColumnIndex || 0);

		for(var i=0;i<_rows.length;i++){
			var trid = _pid + "_" + i; //行id
			var ptrid = ((_pid=="")?"":("r"+_pid)); //父行id

			var row = _rows[i];
			
			var opened = "N";
			if(_level<=expandLayer) opened = "Y";

			var dispaly = "none";
			if(_level<=expandLayer+1) dispaly = ""; //显示层等于展开层+1，展开层的子层要显示
			
			s += "<tr id='r" + trid + "' pid='" + ptrid + "' rowIndex='" + _seqRowIndex++ + "' style='display:" + dispaly + "'>";
			
			_dataPool["r" + trid] = row; //行数据

			//循环显示数据列
			for(var j=0;j<_cols.length;j++){
				var col = _cols[j];
				s += "<td align='" + (col.dataAlign || _config.dataAlign || "left") + "'";

				//显示节点图标的列，该列需要层次缩进
				if(j==folderColumnIndex){
					s += " style='text-indent:" + (parseInt((_config.indentation || "20"))*(_level-1)) + "px;'> ";
				}else{
					s += ">";
				}

				if(j==folderColumnIndex){
					if(row.children){
						if(opened=="Y"){
							s += "<img folder='Y' trid='r" + trid + "' src='" + openIcon + "' class='image_hand'>";
						}else{
							s += "<img folder='Y' trid='r" + trid + "' src='" + closeIcon + "' class='image_hand'>";
						}
					}else{
						s += "<img src='" + leafIcon + "' class='image_nohand'>";
					}
				}
				
				//单元格内容
				if(col.handler){
					s += (eval(col.handler + ".call(new Object(), 'r'+trid, ptrid, row, col)") || "") + "</td>";
				}else{
					s += (row[col.dataField] || "") + "</td>";
				}
			}
			s += "</tr>";

			//递归显示下级数据
			if(row.children){
				drowRowData(row.children, _cols, _level+1, trid);
			}
		}
	}
	
	//主函数
	this.show = function(){
		this.id = _config.id || ("TreeGrid" + TreeGrid.COUNT++);

		s += "<table id='" + this.id + "' cellspacing=0 cellpadding=0 width='" + (_config.width || "100%") + "' class='TreeGrid'>";
		drowHeader();
		drowData();
		s += "</table>";
		
		_root = jQuery("#"+_config.renderTo);
		_root.empty();
		_root.append(s);
		
		//初始化动作
		init();
	}

	this.getDataPool = function(){
		return JSON.stringify(_dataPool);
	}

	init = function(){
		//以新背景色标识鼠标所指行
		if((_config.rowBackgroundHover || "true") == "true"){
			_root.find("tr[pid]").hover(
				function(){
					jQuery(this).addClass("row_hover");
				},
				function(){
					jQuery(this).removeClass("row_hover");
				}
			);
		}

		//将单击事件绑定到tr标签
		_root.find("tr[pid]").bind("click", function(){
			//单击行北京高亮显示
			_root.find("tr[pid]").removeClass("row_active");
			jQuery(this).addClass("row_active");
			
			//获取当前行的数据
			_selectedId = jQuery(this).attr("id");
			_selectedIndex = jQuery(this).attr("rowIndex");
			_selectedData = _dataPool[_selectedId]; 

			//行单击后触发的事件
			if(_config.rowClickEvent){
				eval(_config.rowClickEvent + "(_selectedId, _selectedIndex, _selectedData)");
			}
		});

		//展开、关闭下级节点
		_root.find("img[folder='Y']").bind("click", function(){
			var trid = jQuery(this).attr("trid"); //html自定义属性值用attr

			var opened = false;
			if(jQuery(this).prop("src").indexOf(closeIcon) >= 0){
				opened = true; //收起变展开
			}else{
				opened = false; //展开变收起
			}

			showHiddenRow(trid, opened, true);
		});
	}

	//显示或隐藏子节点数据
	showHiddenRow = function(_trid, opened, partShow){
		if(opened){ //显示子节点
			if(partShow){
				_root.find("img[folder='Y'][trid='"+_trid+"']").prop("src", openIcon);
				showSubRows(_trid);
			}else{
				_root.find("img[folder='Y'][trid^='"+_trid+"']").prop("src", openIcon);
				_root.find("tr[id^=" + _trid + "_]").css("display", "");
			}
		}else{ //隐藏子节点
			_root.find("img[folder='Y'][trid='"+_trid+"']").prop("src", closeIcon); //html固有属性值用prop
			_root.find("tr[id^=" + _trid + "_]").css("display", "none");
		}
	}

	//递归检查下一级节点是否需要显示
	showSubRows = function(_trid){
		var trs = _root.find("tr[pid='" + _trid + "']");
		trs.css("display", "");

		for(var i=0;i<trs.length;i++){
			var _src = jQuery(trs[i]).find("img[folder='Y']").prop("src");
			if(_src != null && _src.indexOf(openIcon) >= 0){
				showSubRows(jQuery(trs[i]).attr("id"));
			}
		}
	}

	//展开所有节点
	this.expandAll = function(){
		var trs = _root.find("tr[pid='']");
		for(var i=0;i<trs.length;i++){
			var trid = jQuery(trs[i]).attr("id");
			showHiddenRow(trid, true, false);
		}
	}

	//折叠所有节点
	this.collapseAll = function(){
		var trs = _root.find("tr[pid='']");
		for(var i=0;i<trs.length;i++){
			var trid = jQuery(trs[i]).attr("id");
			showHiddenRow(trid, false, false);
		}
	}
	
	//取得当前选中的行记录
	this.getSelected = function(){
		return new TreeGridItem(_root, _selectedId, _selectedIndex, _selectedData);
	}

};

//公共静态变量
TreeGrid.OPEN_ICON = "images/tgopen.gif";
TreeGrid.CLOSE_ICON = "images/tgclose.gif";
TreeGrid.LEAF_ICON = "images/tgleaf.gif";
TreeGrid.COUNT = 1;

//数据行对象
function TreeGridItem (_root, _rowId, _rowIndex, _rowData){
	this.id = _rowId;
	this.index = _rowIndex;
	this.data = _rowData;
	
	this.getParent = function(){
		var pid = jQuery("#" + this.id).attr("pid");
		if(pid!=""){
			var pRowIndex = jQuery("#" + pid).attr("rowIndex");
			var pData = _dataPool[pid]; 
			return new TreeGridItem(_root, pid, pRowIndex, pData);
		}
		return null;
	}
	
	this.getChildren = function(){
		var arr = [];
		var trs = jQuery(_root).find("tr[pid='" + this.id + "']");
		for(var i=0;i<trs.length;i++){
			var tr = trs[i];
			var data = _dataPool[tr.id]; 
			arr.push(new TreeGridItem(_root, tr.id, tr.rowIndex, data));
		}
		return arr;
	}
};