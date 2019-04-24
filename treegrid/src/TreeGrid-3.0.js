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
		
		var _seqRowIndex = 0; //������
		var _dataPool = {}; //���ݳ�

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

		//��ʾ��ͷ��
		drowHeader = function(){
			s += "<tr class='header' height='25px'>";
			s += "<td width='25' align='center'></td>"; //�кŵ���ͷ

			if(showCheckbox){
				s += "<td width='25' align='center'></td>"; //checkbox����ͷ
			}

			var cols = settings.columns;
			for(var i=0; i<cols.length; i++){
				var col = cols[i];
				s += "<td align='center' width='" + (col.width || "") + "'>" + (col.headerText || "") + "</td>";
			}
			s += "</tr>";
		}
		
		//�ݹ���ʾ������
		drowData = function(){
			var rows = settings.dataset;
			var cols = settings.columns;
			drowRowData(rows, cols, 1, "");
		}
		
		//�ֲ�����i��j����Ҫ�� var �����������򣬺����������޷�������ʾ
		drowRowData = function(_rows, _cols, _level, _pid){
			for(var i=0; i<_rows.length; i++){
				var trid = _pid + "_" + i; //��id
				var ptrid = ((_pid=="")?"":("r"+_pid)); //����id
				var _rowIndex = ++_seqRowIndex;

				var row = _rows[i];

				var opened = false;
				if(_level<=expandLayer) opened = true;

				var dispaly = "none";
				if(_level<=expandLayer+1) dispaly = ""; //��ʾ�����չ����+1��չ������Ӳ�Ҫ��ʾ
				
				s += "<tr id='r" + trid + "' pid='" + ptrid + "' rowIndex='" + _rowIndex + "' style='display:" + dispaly + "'>";
				
				//�������ݷŽ����ݳ���
				_dataPool["r" + trid] = row; 

				//�к���
				s += "<td align='center'>" + _rowIndex + "</td>";

				
				//checkbox��
				if(showCheckbox){
					var checkboxValue = "";
					if(chkValueField != ""){
						checkboxValue = row[chkValueField];
					}
					s += "<td align='center'><input trid='r" + trid + "' pid='" + ptrid + "' type='checkbox' value='" + checkboxValue + "'></td>";
				}

				//ѭ����ʾ������
				for(var j=0; j<_cols.length; j++){
					var col = _cols[j];
					s += "<td align='" + (col.dataAlign || settings.dataAlign || "left") + "'";

					//�������
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
					
					//��Ԫ������
					if(col.handler){
						s += (eval(col.handler + ".call(new Object(), 'r'+trid, ptrid, row, col)") || "") + "</td>";
					}else{
						//��ʾ�ӽڵ�����
						if(j==0){
							s += (row[col.dataField] || "") + "<font color=red class='count'></font></td>";
						}else{
							s += (row[col.dataField] || "") + "</td>";
						}
					}
				}
				s += "</tr>";

				//�ݹ���ʾ�¼�����
				if(row.children){
					drowRowData(row.children, _cols, _level+1, trid);
				}
			}
		}

		//�ݹ�����һ���ڵ��Ƿ���Ҫ��ʾ
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

		//��ʾ�������ӽڵ�����
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
				_root.find("img[trid='"+_trid+"']").prop("src", closeIcon); //html��������ֵ��prop
				_root.find("tr[id^=" + _trid + "_]").css("display", "none");
			}
		}

		init = function(){
			//���±���ɫ��ʶ�����ָ��
			_root.find("tr[pid]").hover(
				function(){
					$(this).addClass("row_hover");
				},
				function(){
					$(this).removeClass("row_hover");
				}
			);

			//�������¼��󶨵�tr��ǩ
			_root.on("click", "tr[pid]", function(){
				//�����б���������ʾ
				if(lastActiveRow != null){
					$(lastActiveRow).removeClass("row_active");
				}
				$(this).addClass("row_active");
				lastActiveRow = this;
				
				//��ȡ��ǰ�е�����
				_selectedId = $(this).attr("id");
				_selectedIndex = $(this).attr("rowIndex");
				_selectedData = _dataPool[_selectedId]; 

				//�е����󴥷����¼�
				if(settings.rowClickEvent){
					eval(settings.rowClickEvent + "(_selectedId, _selectedIndex, _selectedData)");
				}
			});

			//չ�����۵��¼��ڵ�
			_root.on("click", "img[folder]", function(){
				var trid = $(this).attr("trid"); //html�Զ�������ֵ��attr
				
				var opened = false;
				if($(this).prop("src").indexOf(closeIcon) >= 0){
					opened = true;
				}else{
					opened = false;
				}
				
				showHiddenRow(trid, opened, true);
			});

			//checkbox�¼���
			_root.on("click", "input[trid]:checkbox", function(){
				var o = $(this);
				var trid = o.attr("trid");
				var isChecked = o.is(":checked"); 

				if(isChecked){
					//����ѡ�����и��ڵ�
					if(chkCascadeSelectParent){
						var pid = o.attr("pid");
						while(pid != null && pid != ""){
							o = _root.find("input[trid='"+pid+"']:checkbox");
							o.prop("checked", true);
							pid = o.attr("pid");
						}
					}

					//����ѡ�������ӽڵ�
					if(chkCascadeSelectChildren){
						_root.find("input[trid^='" + trid + "_']:checkbox").prop("checked", true);
					}
				}else{
					//������ѡ�����ӽڵ�
					if(chkCascadeSelectChildren){
						_root.find("input[trid^='" + trid + "_']:checkbox:checked").prop("checked", false);
					}
				}
			});
		}

		//����ڵ���ӽڵ���
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

		//##### �����ǹ������󷽷� #####
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

		//չ�����нڵ�
		this.expandAll = function(){
			var trs = _root.find("tr[pid='']");
			for(var i=0; i<trs.length; i++){
				var trid = jQuery(trs[i]).attr("id");
				showHiddenRow(trid, true, false);
			}
		}

		//�۵����нڵ�
		this.collapseAll = function(){
			var trs = _root.find("tr[pid='']");
			for(var i=0; i<trs.length; i++){
				var trid = jQuery(trs[i]).attr("id");
				showHiddenRow(trid, false, false);
			}
		}

		//��ȡ���ݳ�
		this.getDataPool = function(){
			return _dataPool;
		}

		//�����ݳػ�ȡĳһ�е�����
		this.getRowData = function(trid){
			return _dataPool[trid];
		}

		//��������İ汾��
		this.version = function(){
			return "TreeGrid-3.0";
		}

		//ˢ��
		this.refresh = function(){
			//�Ƴ��¼�
			_root.off("click", "tr[pid]");
			_root.off("click", "img[folder]");
			_root.off("click", "input[trid]:checkbox");

			$(target).empty();

			//����reset
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
	
		//ȡ�õ�ǰѡ�е��м�¼
		this.getSelected = function(){
			return new TreeGridItem(_root, this, _selectedId, _selectedIndex, _selectedData);
		}

		//ȡ������ѡ�е�checkbox��ֵ
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

		//����dataset
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
