;(function($){
	var defaults = {
		width: "100%",
		dataAlign: "left",
		openIcon: "images/tgopen.gif",
		closeIcon: "images/tgclose.gif",
		leafIcon: "images/tgleaf.gif",
		showCheckbox: false,
		chkValueField: "",
		chkCascadeSelectChildren: false,
		chkCascadeSelectParent: false,
		expandLayer: 0,
		columns: [],
		dataset: []
	};

	var TreeGrid = function(_target, options){
		var target = _target;

		var settings = $.extend({}, defaults, options||{});
		
		var _seqRowIndex = 0; //行索引
		var _dataPool = {}; //数据池

		var s = "";

		var _root = null;
		var _selectedId = null;
		var _selectedIndex = null;
		var _selectedData = null;
		var lastActiveRow = null;

		var expandLayer = settings.expandLayer;
		var openIcon = settings.openIcon;
		var closeIcon = settings.closeIcon;
		var leafIcon = settings.leafIcon;
		var showCheckbox = settings.showCheckbox || false;
		var chkValueField = settings.chkValueField || "";
		var chkCascadeSelectChildren = settings.chkCascadeSelectChildren || false;
		var chkCascadeSelectParent = settings.chkCascadeSelectParent || false;

		//显示表头行
		drowHeader = function(){
			s += "<tr class='header' height='25px'>";
			s += "<td width='25' align='center'></td>"; //行号的列头

			if(showCheckbox){
				s += "<td width='25' align='center'></td>"; //checkbox的列头
			}

			var cols = settings.columns;
			for(var i=0; i<cols.length; i++){
				var col = cols[i];
				s += "<td align='center' width='" + (col.width || "") + "'>" + (col.headerText || "") + "</td>";
			}
			s += "</tr>";
		}
		
		//递归显示数据行
		drowData = function(){
			var rows = settings.dataset;
			var cols = settings.columns;
			drowRowData(rows, cols, 1, "");
		}
		
		//局部变量i、j必须要用 var 来声明，否则，后续的数据无法正常显示
		drowRowData = function(_rows, _cols, _level, _pid){
			for(var i=0; i<_rows.length; i++){
				var trid = _pid + "_" + i; //行id
				var ptrid = ((_pid=="")?"":("r"+_pid)); //父行id
				var _rowIndex = ++_seqRowIndex;

				var row = _rows[i];

				var opened = false;
				if(_level<=expandLayer) opened = true;

				var dispaly = "none";
				if(_level<=expandLayer+1) dispaly = ""; //显示层等于展开层+1，展开层的子层要显示
				
				s += "<tr id='r" + trid + "' pid='" + ptrid + "' rowIndex='" + _rowIndex + "' style='display:" + dispaly + "'>";
				
				//将行数据放进数据池中
				_dataPool["r" + trid] = row; 

				//行号列
				s += "<td align='center'>" + _rowIndex + "</td>";

				
				//checkbox列
				if(showCheckbox){
					var checkboxValue = "";
					if(chkValueField != ""){
						checkboxValue = row[chkValueField];
					}
					s += "<td align='center'><input trid='r" + trid + "' pid='" + ptrid + "' type='checkbox' value='" + checkboxValue + "'></td>";
				}

				//循环显示数据列
				for(var j=0; j<_cols.length; j++){
					var col = _cols[j];
					s += "<td align='" + (col.dataAlign || settings.dataAlign || "left") + "'";

					//层次缩进
					if(j==0){
						s += " style='text-indent:" + (20 * (_level-1)) + "px;'> ";
					}else{
						s += ">";
					}

					if(j==0){
						if(row.children){
							if(opened){
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
						//显示子节点数量
						if(j==0){
							s += (row[col.dataField] || "") + "<font color=red class='count'></font></td>";
						}else{
							s += (row[col.dataField] || "") + "</td>";
						}
					}
				}
				s += "</tr>";

				//递归显示下级数据
				if(row.children){
					drowRowData(row.children, _cols, _level+1, trid);
				}
			}
		}

		//递归检查下一级节点是否需要显示
		showSubRows = function(_trid){
			var trs = _root.find("tr[pid='" + _trid + "']");
			trs.css("display", "");

			for(var i=0; i<trs.length; i++){
				var _src = _root.find("img[trid='" + $(trs[i]).attr("id") + "']").prop("src");
				if(_src != null && _src.indexOf(openIcon) >= 0){
					showSubRows($(trs[i]).attr("id"));
				}
			}
		}

		//显示或隐藏子节点数据
		showHiddenRow = function(_trid, opened, partShow){
			if(opened){ 
				if(partShow){
					_root.find("img[trid='"+_trid+"']").prop("src", openIcon);
					showSubRows(_trid);
				}else{
					_root.find("img[trid^='"+_trid+"']").prop("src", openIcon);
					_root.find("tr[id^=" + _trid + "_]").css("display", "");
				}
			}else{ 
				_root.find("img[trid='"+_trid+"']").prop("src", closeIcon); //html固有属性值用prop
				_root.find("tr[id^=" + _trid + "_]").css("display", "none");
			}
		}

		init = function(){
			//以新背景色标识鼠标所指行
			_root.find("tr[pid]").hover(
				function(){
					$(this).addClass("row_hover");
				},
				function(){
					$(this).removeClass("row_hover");
				}
			);

			//将单击事件绑定到tr标签
			_root.on("click", "tr[pid]", function(){
				//单击行背景高亮显示
				if(lastActiveRow != null){
					$(lastActiveRow).removeClass("row_active");
				}
				$(this).addClass("row_active");
				lastActiveRow = this;
				
				//获取当前行的数据
				_selectedId = $(this).attr("id");
				_selectedIndex = $(this).attr("rowIndex");
				_selectedData = _dataPool[_selectedId]; 

				//行单击后触发的事件
				if(settings.rowClickEvent){
					eval(settings.rowClickEvent + "(_selectedId, _selectedIndex, _selectedData)");
				}
			});

			//展开或折叠下级节点
			_root.on("click", "img[folder]", function(){
				var trid = $(this).attr("trid"); //html自定义属性值用attr
				
				var opened = false;
				if($(this).prop("src").indexOf(closeIcon) >= 0){
					opened = true;
				}else{
					opened = false;
				}
				
				showHiddenRow(trid, opened, true);
			});

			//checkbox事件绑定
			_root.on("click", "input[trid]:checkbox", function(){
				var o = $(this);
				var trid = o.attr("trid");
				var isChecked = o.is(":checked"); 

				if(isChecked){
					//级联选择所有父节点
					if(chkCascadeSelectParent){
						var pid = o.attr("pid");
						while(pid != null && pid != ""){
							o = _root.find("input[trid='"+pid+"']:checkbox");
							o.prop("checked", true);
							pid = o.attr("pid");
						}
					}

					//级联选择所有子节点
					if(chkCascadeSelectChildren){
						_root.find("input[trid^='" + trid + "_']:checkbox").prop("checked", true);
					}
				}else{
					//级联反选所有子节点
					if(chkCascadeSelectChildren){
						_root.find("input[trid^='" + trid + "_']:checkbox:checked").prop("checked", false);
					}
				}
			});
		}

		//计算节点的子节点数
		calculateCount = function (){
			$.each(_root.find("tr[pid='']"), function(i, o){
				var trid = $(o).attr("id");
				var children = _root.find("tr[id^='" + trid + "_']");
				if(children){
					$(o).find("font.count:first").html("[" + children.length + "]");

					calculateSubCount(trid);
				}
			});
		}

		calculateSubCount = function(pid){
			$.each(_root.find("img[folder][trid^='" + pid + "_']"), function(i, o){
				var tr = $(o).parent().parent();
				var trid = tr.attr("id");
				var children = _root.find("tr[id^='" + trid + "_']");
				if(children){
					tr.find("font.count:first").html("[" + children.length + "]");

					calculateSubCount(trid);
				}
			});
		}

		//##### 以下是公共对象方法 #####
		this.show = function(){
			s += "<table cellspacing=0 cellpadding=0 width='" + (settings.width || "100%") + "' class='TreeGrid'>";
			drowHeader();
			drowData();
			s += "</table>";

			_root = $(target);
			_root.empty().append(s);
			
			init();
			calculateCount();
		}

		//展开所有节点
		this.expandAll = function(){
			var trs = _root.find("tr[pid='']");
			for(var i=0; i<trs.length; i++){
				var trid = jQuery(trs[i]).attr("id");
				showHiddenRow(trid, true, false);
			}
		}

		//折叠所有节点
		this.collapseAll = function(){
			var trs = _root.find("tr[pid='']");
			for(var i=0; i<trs.length; i++){
				var trid = jQuery(trs[i]).attr("id");
				showHiddenRow(trid, false, false);
			}
		}

		//获取数据池
		this.getDataPool = function(){
			return _dataPool;
		}

		//从数据池获取某一行的数据
		this.getRowData = function(trid){
			return _dataPool[trid];
		}

		//返回组件的版本号
		this.version = function(){
			return "TreeGrid-3.0";
		}

		//刷新
		this.refresh = function(){
			//移除事件
			_root.off("click", "tr[pid]");
			_root.off("click", "img[folder]");
			_root.off("click", "input[trid]:checkbox");

			$(target).empty();

			//变量reset
			_seqRowIndex = 0;
			_dataPool = {};

			s = "";
			_root = null;
			_selectedId = null;
			_selectedIndex = null;
			_selectedData = null;
			lastActiveRow = null;

			this.show();
		}
	
		//取得当前选中的行记录
		this.getSelected = function(){
			return new TreeGridItem(_root, this, _selectedId, _selectedIndex, _selectedData);
		}

		//取得所有选中的checkbox的值
		this.getSelectedCheckboxValues = function(){
			if(showCheckbox){
				var checkboxValues = new Array();
				$.each(_root.find("input[trid]:checkbox:checked"), function(i, o){
					checkboxValues.push($(o).val());
				});
				return checkboxValues.join(",");
			}else{
				return "";
			}
		}

		//设置dataset
		this.setDataset = function(ds){
			var _ds = ds || [];
			settings.dataset = _ds;
		}
	};

	$.fn.showTreeGrid = function(options){
		var treegrid = new TreeGrid(this, options);
		treegrid.show();
		return treegrid;
	};
})(jQuery);

var TreeGridItem = function(_root, _treeGrid, _rowId, _rowIndex, _rowData){
	this.root = _root;
	this.treeGrid = _treeGrid;
	this.id = _rowId;
	this.index = _rowIndex;
	this.data = _rowData;
	
	this.getParent = function(){
		var pid = this.root.find("#" + this.id).attr("pid");
		if(pid != ""){
			var pRowIndex = this.root.find("#" + pid).attr("rowIndex");
			var pData = this.treeGrid.getRowData(pid); 
			return new TreeGridItem(this.root, this.treeGrid, pid, pRowIndex, pData);
		}
		return null;
	}
	
	this.getChildren = function(){
		var arr = [];
		var trs = this.root.find("tr[pid='" + this.id + "']");
		for(var i=0; i<trs.length; i++){
			var tr = trs[i];
			var trData = this.treeGrid.getRowData(tr.id); 
			arr.push(new TreeGridItem(this.root, this.treeGrid, tr.id, tr.rowIndex, trData));
		}
		return arr;
	}
};
