/**
 * Created by dmkits on 20.04.16.
 */
Handsontable.cellTypes['text'].editor.prototype.setValue = function(value) {
    var cellPropFormat = this.cellProperties["format"];
    if (this.cellProperties["type"]=="numeric"&&cellPropFormat&&cellPropFormat.indexOf('%')>=0){
        var val = ''; if(value) val = Math.round(value.replace(',','.')*100)+'%'; this.TEXTAREA.value= val;
    } else if (this.cellProperties["type"]=="numeric") {
        this.TEXTAREA.value = value.replace('.',',');
    } else this.TEXTAREA.value=value;
};
Handsontable.cellTypes['text'].editor.prototype.getValue = function() {
    var cellPropFormat = this.cellProperties["format"];
    if (this.cellProperties["type"]=="numeric"&&cellPropFormat&&cellPropFormat.indexOf('%')>=0){
        var val = this.TEXTAREA.value;
        if(!val) return this.TEXTAREA.value;
        if(val.indexOf('%')>=0) val = val.replace('%','');
        if (isNaN(val/100)) return this.TEXTAREA.value;
        return val/100;
    } else if (this.cellProperties["type"]=="numeric") {
        return this.TEXTAREA.value.replace('.',',');
    }
    return this.TEXTAREA.value;
};
define(["dojo/_base/declare", "dijit/layout/ContentPane", "dojo/dom-construct"], function(declare, ContentPane, domConstruct){
    return declare("HTable", [ContentPane], {
        handsontable: null,
        htcolumns: [], htviscolumns: [],
        htdata: [], htfiltereddata: [],
        enableComments:false, htComments: [],
        readOnly: true, useFilters: false,
        htaddRows: 0,
        allowFillHandle: false,
        wordWrap: false,
        persistentState: false,
        //showIdentifiers:false,
        getURL: "/", postChanges: false, postURL: "/",
        constructor: function(args,parentName){
            declare.safeMixin(this,args);
            this.srcNodeRef = document.getElementById(parentName);
            this.domNode = this.srcNodeRef;
            this.htcolumns= []; this.htviscolumns= []; this.htdata= []; this.htfiltereddata=[];
            this.enableComments=false; this.htComments=[];
            this.readOnly= true; this.filtered= false;
            this.htaddRows= 0;
            //this.showIdentifiers=false;
            this.allowFillHandle= false;
            this.wordWrap= false;
            this.persistentState= false;
            if (args.enableComments) { this.enableComments=args.enableComments; }
            if (args.data) { this.setData(args.data); }
            if (args.readOnly) { this.readOnly=args.readOnly; }
            if (args.useFilters) { this.useFilters=args.useFilters; }
            if (args.htaddRows) { this.htaddRows=args.htaddRows; }
            if (args.allowFillHandle) this.allowFillHandle=args.allowFillHandle;
            if (args.wordWrap) this.wordWrap=args.wordWrap;
            if (args.persistentState) this.persistentState=args.persistentState;
            if (args.getURL) this.getURL=args.getURL;
            if (args.postChanges) this.postChanges=args.postChanges;
            if (args.postURL) this.postURL=args.postURL;
        },
        setData: function(data) {
            if (!data) { data={ identifier:null, columns:[], items:[] }; }
            if(data.identifier) { this.handsontable.rowIDName=data.identifier; }
            if(data.columns) {
                this.htcolumns = data.columns;
                this.htviscolumns = []; var vc=0;
                for(var c=0;c<this.htcolumns.length;c++){
                    var colItem=this.htcolumns[c];
                    if(colItem["visible"]!==false) {
                        var newColData = {};
                        this.htviscolumns[vc++]= newColData;
                        Object.assign(newColData,colItem);
                        var colTitle = newColData["name"], colWidth = newColData["width"];
                        if(colWidth&&colTitle&&colTitle.length*7>colWidth) {
                            colTitle = colTitle.replace(" ","<br>"); newColData["name"]=colTitle;
                        }
                        if(!newColData["filterValues"]) newColData["filterValues"]= [];
                    }
                }
            } else { this.htcolumns=[]; this.htviscolumns = []; }
            if(data.items) {
                this.htdata = data.items;
                for(var i in data.items) data.items[i]['___$loaded$___']= true;
            } else this.htdata=[];
            if(data.comments) this.htComments = data.comments; else this.htComments = [];
        },
        getRowIDName: function(){
            return this.handsontable.rowIDName;
        },
        getColumns: function(){ return this.htcolumns; },
        getVisibleColumns: function(){ return this.htviscolumns; },
        postCreate : function(){
            var content = document.createElement('div');
            content.parentNode = this.domNode; content.parent = this.domNode; content.style="width:100%;height:100%;margin0;padding:0;";
            this.set("content",content);
            var isAllowInsertRow= this.htaddRows==0;
            var parent=this;
            this.handsontable = new Handsontable(content, {
                columns: parent.htviscolumns,
                colHeaders: function(colIndex){
                    if(!parent.htviscolumns||!parent.htviscolumns[colIndex])return colIndex;
                    var addToColHeader = "";
                    if(this.colHeadersFilterButton) addToColHeader= this.colHeadersFilterButton(colIndex);
                    return parent.htviscolumns[colIndex]["name"]+addToColHeader;
                },
                data: parent.htdata, comments: parent.enableComments,//copyPaste: true,default
                rowIDName:null, selRowIDValue: null, selRowValues: null, selRowIndex: null,
                rowHeaders: false,
                useFilters: parent.useFilters, filterMenu:null,
                //filterMethod: function(queryStr, value){                                                                console.log("HTable handsontable filterMethod queryStr=",queryStr,value);
                //    return queryStr.toString() === value.toString();
                //},
                search: { queryMethod: function(queryStr, value){                                                       console.log("HTable handsontable queryMethod queryStr=",queryStr,value);
                    if(queryStr==null) queryStr=""; if(value==null) value="";
                    return queryStr.toString() === value.toString();
                } },
                //stretchH: "all",
                autoWrapRow: true,
                //maxRows: 20,
                //width: 0, height: 0,
                minSpareCols:0, minSpareRows: parent.htaddRows,
                allowInsertRow:isAllowInsertRow,
                fillHandle: parent.allowFillHandle,
                startRows: 1,
                fixedColumnsLeft: 0, fixedRowsTop: 0,
                manualColumnResize: true, manualRowResize: false,
                persistentState: parent.persistentState,
                readOnly: parent.readOnly,
                wordWrap: parent.wordWrap,
                enterMoves:{row:0,col:1}, tabMoves:{row:0,col:1},
                multiSelect: true,
                beforeOnCellMouseDown: function(event, coords, element) {
                    if(event.target.id.indexOf("filter_menu")<0)/*filter menu closed if filter button focusout*/
                        parent.handsontable.hideFilterMenu();
                    if(coords.row < 0) { event.stopImmediatePropagation(); }//disable column header click event
                },
                colHeadersFilterButton: function (colIndex) {
                    if(this.useFilters!=true) return "";
                    var filterButton = document.createElement('BUTTON');
                    filterButton.id = "filter_button_for_col_"+colIndex; filterButton.innerHTML = "\u25BC"; filterButton.className = "changeType";
                    if (this.columns[colIndex]["filtered"]==true) filterButton.style.color = 'black'; else filterButton.style.color = '#bbb';
                    filterButton.setAttribute("colindex",colIndex);                                                     //console.log("HTable.handsontable.colHeadersFilterButton colIndex=",colIndex);
                    return filterButton.outerHTML;
                },
                cellValueRenderer:function (instance, td, row, col, prop, value, cellProperties) {                      //console.log("HTable cellValueRenderer prop=",prop," row=",row," col=",col," cellProperties=",cellProperties);
                    Handsontable.cellTypes[cellProperties.type].renderer.apply(this, arguments);
                    if(cellProperties["html"]){
                        Handsontable.renderers.HtmlRenderer.apply(this, arguments);
                    } else if (cellProperties["type"]==="text"&&cellProperties["dateFormat"]){
                        if(value!==null&&value!==undefined)
                            td.innerHTML= moment(new Date(value) /*value,"YYYY-MM-DD"*/).format(cellProperties["dateFormat"]);
                        else td.innerHTML="";
                    }

                    var rowIndex=instance.selRowIndex, rowSourceData= instance.getSourceDataAtRow(row);                 //console.log("HTable cellValueRenderer rowIndex=",rowIndex);
                    if(instance.rowIDName){
                        var rowID=rowSourceData[instance.rowIDName];                                                //console.log("HTable cellValueRenderer prop=",prop," row=",row," col=",col," rowID=",rowID);
                        if(rowIndex===undefined && rowID!==undefined&&rowID==instance.selRowIDValue) {
                            rowIndex= row; instance.selRowIndex= rowIndex;
                            if (instance.selRowValues===undefined) instance.selRowValues= rowSourceData;
                        }
                        if(rowIndex!==undefined&&rowIndex==row){
                            td.classList.add('hTableCurrentRow');
                            if(instance.selRowValues===undefined) instance.selRowValues= rowSourceData;
                        }
                    }
                    var markAsNotStored= (rowSourceData["___$loaded$___"]!==true);
                    //var markAsError= false;
                    //markAsError= (instance.rowIDName&&(rowID===undefined||rowID==null));
                    //if (!markAsError) {
                    //    for(var dataItemName in rowSourceData){
                    //        var rowItemData= rowSourceData[dataItemName];
                    //        markAsError= (rowItemData&&dataItemName.indexOf("$error_")>=0);
                    //        if (markAsError) break;
                    //    }
                    //}
                    if(markAsNotStored){ td.classList.add('hTableErrorRow'); }//markAsError
                    return cellProperties;
                },
                cells: function (row, col, prop) {
                    var readOnly = true, rowData;
                    readOnly = ((rowData=this.instance.getSourceData()[row])===undefined || rowData["___$loaded$___"]===true);
                    return {readOnly:readOnly, renderer:this.cellValueRenderer};
                },
                afterSelectionEnd: function(r,c,r2,c2) {
                    parent.onSelect(r, this.getDataAtRowProp(r,this.rowIDName), this.getSourceDataAtRow(r));
                },
                beforeChange: function (change, source) {
                    if (source === 'loadData') { return; }
                    if (source === 'paste') {
                        for (var i = 0; i < change.length; i++) {
                            var changeItem = change[i];
                            if (typeof changeItem[3] === 'string' || changeItem[3] instanceof String) changeItem[3] = changeItem[3].trim();
                        }
                    }
                },
                afterChange: function (change, source) {                                            //console.log("HTable afterChange source=",source," change=",change);
                    if (source === 'loadData') {
                        parent.onUpdateContent();
                        return;
                    }
                    if(change.length==1){//changed 1 cell
                        var row=change[0][0], changedrows=[];
                        var prop= change[0][1], oldval= change[0][2], newval= change[0][3];
                        var changedrowsItem = [], rowIDName= this.rowIDName;
                        if (rowIDName) changedrowsItem[rowIDName] = { oldValue:this.getSourceDataAtRow(row)[rowIDName] };
                        changedrowsItem[prop]={ oldValue:oldval, newValue:newval };
                        changedrows[row]=changedrowsItem;
                        if(parent.onChangeCells) parent.onChangeCells(changedrows);
                        parent.onUpdateContent();
                    } else {//changed many cells
                        var changedRowsData=[];
                        for (var i = 0; i < change.length; i++) {
                            var row=change[i][0];
                            var prop= change[i][1], oldval= change[i][2], newval= change[i][3];
                            var changedrowsItem= changedRowsData[row];
                            if (!changedrowsItem) changedrowsItem = [];
                            changedrowsItem[prop]={ oldValue:oldval, newValue:newval };
                            changedRowsData[row]=changedrowsItem;
                        }
                        var rowIDName= this.rowIDName;
                        if (rowIDName)
                            for (var row in changedRowsData) {
                                var changedRowsDataItem = changedRowsData[row];
                                if (!changedRowsDataItem[rowIDName])
                                    changedRowsDataItem[rowIDName] = { oldValue:this.getSourceDataAtRow(row)[rowIDName] };
                            }
                        if(parent.onChangeCells) parent.onChangeCells(changedRowsData);
                        parent.onUpdateContent();
                    }
                },
                afterRemoveRow: function(index,amount){
                    parent.onUpdateContent();
                }
            });
            Handsontable.Dom.addEvent(document, 'focusin', function (event) {                                           //console.log("document focus target=", event.target);
                if(event.target.id.indexOf("filter_menu_")<0)/*filter menu closed if filter menu item element focusout*/
                    parent.handsontable.hideFilterMenu();
            });
            Handsontable.Dom.addEvent(document, 'mousedown', function (event) {                                         //console.log("document mousedown target=",event.target);
                if(event.target.id.indexOf("filter_button_for_")>=0) event.stopPropagation();
                if(event.target.id.indexOf("filter_menu_")<0)/*filter menu closed if filter button focusout*/
                    parent.handsontable.hideFilterMenu();
            });
            Handsontable.Dom.addEvent(this.handsontable.rootElement, 'mouseup', function (event) {                      //console.log("mouseup ",event);
                if(event.target.id.indexOf("filter_button_for_")>=0){
                    var button= event.target, colProp= event.target.getAttribute("colprop");                            //console.log("mouseup filter_button_for_ ",colProp);
                    parent.handsontable.showFilterMenu(button);
                }
            });
            this.handsontable.showFilterMenu= function (button) {                                                       //console.log("handsontable.showFilterMenu ",this);
                var filterMenu = this.filterMenu;
                //if(filterMenu&&filterMenu.isOpen==true&&filterMenu.colProp==colProp){//close filter menu
                //    this.hideFilterMenu(); return;
                //}
                var colIndex = button.getAttribute("colindex");
                var colProp= this.colToProp(colIndex), colData= this.getDataAtCol(colIndex);
                var colProps = this.getSettings().columns[colIndex];
                var colType= colProps["type"], filterValue = colProps["filterValue"], filterValues = colProps["filterValues"];
                var filterItemsMap={};
                for(var i in colData){ var filterItemValue= colData[i]; if(filterItemValue==null)filterItemValue=""; filterItemsMap[filterItemValue]=true; }
                if(colProps["filtered"]==true&&filterValues){
                    for(var filterValueItem in filterValues)
                        if (filterValues[filterValueItem]==false){ filterItemsMap[filterValueItem]=false; }
                }
                var filterItems = [];
                for(var item in filterItemsMap){ filterItems[filterItems.length]=item; }
                if(colType=="text"&&filterItems.length==0) return;

                if(!filterMenu) {
                    filterMenu = document.createElement('UL');
                    filterMenu.id = "filter_menu"; filterMenu.className = "changeTypeMenu";
                    this.filterMenu = filterMenu;
                    document.body.appendChild(filterMenu);
                    Handsontable.Dom.addEvent(filterMenu, 'click', function (event) {/*menu item click*/                //console.log("handsontable.showFilterMenu filterMenu click ",event.target);
                        var eventTarget = event.target, eventTargetID = event.target.id;
                        if (eventTargetID.indexOf("filter_menu_item_elem_")==0&&eventTargetID.indexOf("buttonCancel")>0) {   //console.log("handsontable.showFilterMenu filterMenu click filter_menu_item_ buttonCancel", event.target);
                            var filterColProps= eventTarget.filterMenu.colProps;
                            var filterValues= filterColProps["filterValues"];
                            if(eventTarget.filterMenu.valueType=="text") {
                                for(var filterValueItem in filterValues) filterValues[filterValueItem]=true;
                                filterColProps["filterValue"]= null;
                            } else if(eventTarget.filterMenu.valueType=="numeric"){
                                for(var filterValueItem in filterValues) filterValues[filterValueItem]= null;
                            }
                            filterColProps["filtered"]= false;
                            eventTarget.filterButton.style.color = '#bbb';
                            parent.handsontable.hideFilterMenu();
                            parent.updateTableContent();
                        } else if (eventTargetID.indexOf("filter_menu_item_elem_")==0&&eventTargetID.indexOf("buttonOK")>0) {       //console.log("handsontable.showFilterMenu filterMenu click filter_menu_item_ buttonOK", event.target);
                            var filterColProps= eventTarget.filterMenu.colProps;
                            var filterValues= filterColProps["filterValues"];
                            var filterMenu= eventTarget.filterMenu;
                            var filtered = false;
                            if(eventTarget.filterMenu.valueType=="text") {
                                for(var filterValueItem in filterValues) filterValues[filterValueItem]=true;
                                for(var filterValueItemName in filterMenu.valueItems) {
                                    var filterValueItem = filterMenu.valueItems[filterValueItemName];
                                    if(filterValueItem.checked==false) {
                                        var value = filterValueItem["value"];
                                        filterValues[value]= false;
                                    }
                                }
                                for(var filterValueItem in filterValues)
                                    if(filterValues[filterValueItem]==false){ filtered=true; break; }
                                if(filterMenu.valueEdit) {
                                    if(filterMenu.valueEdit.value=="") filterColProps["filterValue"]=null;
                                    else { filterColProps["filterValue"]= filterMenu.valueEdit.value; filtered=true; }
                                }
                            } else if(eventTarget.filterMenu.valueType=="numeric") {
                                for(var filterValueItem in filterValues) filterValues[filterValueItem]= null;
                                if(filterMenu.valueEdit.value!="") {
                                    var filterEditValues = filterMenu.valueEdit.value, filterValueNum=1;
                                    while(filterEditValues.length>0){
                                        var posDelimiter = filterEditValues.indexOf(","); if(posDelimiter<0) posDelimiter=filterEditValues.length;
                                        var filterEditValueItem = filterEditValues.substring(0,posDelimiter);
                                        var posInterval= filterEditValueItem.indexOf("-");
                                        if(posInterval<0){
                                            var filterValueItem= parseFloat(filterEditValueItem);
                                            if(!isNaN(filterValueItem)) filterValues["value_"+filterValueNum] = filterValueItem;
                                        } else {
                                            filterValues["value_"+filterValueNum] = [];
                                            var filterEditValueItemFrom= filterEditValueItem.substring(0,posInterval);
                                            if(!isNaN(filterEditValueItemFrom)) filterValues["value_"+filterValueNum]["from"] = filterEditValueItemFrom;
                                            var filterEditValueItemTo= filterEditValueItem.substring(posInterval+1,filterEditValueItem.length);
                                            if(!isNaN(filterEditValueItemTo)) filterValues["value_"+filterValueNum]["to"] = filterEditValueItemTo;
                                        }
                                        filterEditValues= filterEditValues.substring(posDelimiter+1,filterEditValues.length);
                                        filterValueNum++;
                                    }
                                }
                                for(var filterValueItem in filterValues) if(filterValues[filterValueItem]!=null) { filtered= true; break; }
                            }
                            filterColProps["filtered"]= filtered;
                            var filterButton = event.target.filterButton;
                            if (filtered==true) filterButton.style.color = 'black'; else filterButton.style.color = '#bbb';
                            parent.handsontable.hideFilterMenu();
                            parent.updateTableContent();
                        } else if (eventTargetID.indexOf("filter_menu_item_elem_")==0&&eventTargetID.indexOf("buttonClearAll")>0) {
                            if(eventTarget.filterMenu.valueItems){
                                for(var filterValueItemName in eventTarget.filterMenu.valueItems)
                                    eventTarget.filterMenu.valueItems[filterValueItemName].checked = false;
                            }
                        } else if (eventTargetID.indexOf("filter_menu_item_elem_")==0&&eventTargetID.indexOf("buttonClear")>0) {
                            if(eventTarget.filterMenu.valueEdit) eventTarget.filterMenu.valueEdit.value="";
                        }
                    });
                }
                filterMenu.style.display = 'block'; filterMenu.isOpen = true;
                position = button.getBoundingClientRect();
                filterMenu.style.top = (position.top + (window.scrollY || window.pageYOffset)) + 2 + 'px';
                filterMenu.style.left = (position.left) + 'px';
                filterMenu.colProp = colProp; filterMenu.colType = colType; filterMenu.colProps = colProps;
                while(filterMenu.firstChild) filterMenu.removeChild(filterMenu.firstChild);
                filterMenu.valueType = colType; filterMenu.valueEdit = null; filterMenu.valueItems = [];

                filterItems = filterItems.sort();
                var createMenuItem = function(filterMenu,idPostfix,itemType, filterMenuItemData){
                    var filterMenuItem = document.createElement("LI");
                    filterMenuItem["id"]= "filter_menu_item_"+idPostfix; filterMenuItem["filterMenu"] = filterMenu;

                    filterMenu.appendChild(filterMenuItem);
                    var filterMenuElem,filterMenuElemLabel;
                    if(itemType=="button"){
                        filterMenuElem = document.createElement("input"); filterMenuElem.type = "button"; filterMenuElem.id = "filter_menu_item_elem_"+idPostfix;
                        filterMenuElem.value = filterMenuItemData.label;
                        filterMenuElem.style.width = "120px";
                        filterMenuElem.filterButton = button; filterMenuElem.filterMenu = filterMenu;
                        filterMenuItem.appendChild(filterMenuElem);
                    } else if(itemType=="edit") {
                        filterMenuElem = document.createElement("input"); filterMenuElem.type = "text"; filterMenuElem.id = "filter_menu_item_elem_"+idPostfix;
                        filterMenuElem.value= filterMenuItemData.value; filterMenuElem.filterMenu = filterMenu; filterMenu.valueEdit= filterMenuElem;
                        filterMenuElemLabel = document.createElement("label"); filterMenuElemLabel.id = "filter_menu_item_elem_label_"+idPostfix;
                        filterMenuElemLabel.htmlFor = filterMenuElem.id; filterMenuElemLabel.appendChild(document.createTextNode(filterMenuItemData.label));
                        filterMenuItem.appendChild(filterMenuElemLabel);filterMenuItem.appendChild(filterMenuElem);
                    } else if(itemType=="checkboxlist"){
                        var filterMenuItemElemsContainer = document.createElement("div"); filterMenuItemElemsContainer.id="filter_menu_item_elem_divcontainer"+idPostfix;
                        filterMenuItemElemsContainer.style.maxHeight = "300px";filterMenuItemElemsContainer.style.maxWidth = "350px";
                        filterMenuItemElemsContainer.style.overflow= "auto";
                        filterMenuItem.appendChild(filterMenuItemElemsContainer);
                        for(var i in filterMenuItemData.values){
                            var filterMenuItemElemDIV = document.createElement("div");filterMenuItemElemDIV.id="filter_menu_item_elem_div"+idPostfix+"_"+i;
                            filterMenuItemElemsContainer.appendChild(filterMenuItemElemDIV);
                            var elemValue = filterMenuItemData.values[i];
                            filterMenuElem = document.createElement("input"); filterMenuElem.type = "checkbox"; filterMenuElem.id = "filter_menu_item_elem_"+idPostfix+"_"+i;
                            filterMenuElem.value= elemValue; filterMenuElem.checked= filterMenuItemData.isValueChecked[elemValue]!=false;
                            filterMenuElem.filterMenu = filterMenu; filterMenu.valueItems[idPostfix+"_"+i] = filterMenuElem;
                            filterMenuElemLabel = document.createElement("label"); filterMenuElemLabel.id = "filter_menu_item_elem_label_"+idPostfix+"_"+i;
                            if(elemValue=="")elemValue="(Без значения)";
                            filterMenuElemLabel.htmlFor = filterMenuElem.id; filterMenuElemLabel.appendChild(document.createTextNode(elemValue));
                            filterMenuElem.label = filterMenuElemLabel;
                            filterMenuItemElemDIV.appendChild(filterMenuElem); filterMenuItemElemDIV.appendChild(filterMenuElemLabel);
                        }
                    }
                    return filterMenuItem;
                };
                createMenuItem(filterMenu,colProp+"_buttonCancel","button", {label:"Снять фильтр"});
                if(colType=="text"){
                    createMenuItem(filterMenu,colProp+"_buttonClear","button",{label:"Очистить значение"});
                    if(!filterValue)filterValue="";
                    createMenuItem(filterMenu,colProp+"edit","edit",{label:"Значение: ",value:filterValue});
                    createMenuItem(filterMenu,colProp+"_buttonClearAll","button",{label:"Снять все отметки"});
                    createMenuItem(filterMenu,colProp+"_checkboxlist","checkboxlist",{values:filterItems,isValueChecked:filterValues});
                } else if(colType=="numeric"){
                    createMenuItem(filterMenu,colProp+"_buttonClear","button",{label:"Очистить значение"});
                    var filterEditValue = "";
                    for(var filterMenuItem in filterValues){
                        var filterValue= filterValues[filterMenuItem];
                        if(filterMenuItem.indexOf("value_")>=0&&filterValue!=null) {
                            if(filterEditValue.length>0) filterEditValue=filterEditValue+",";
                            if(filterValue instanceof Array){
                                filterEditValue= filterEditValue+filterValue["from"]+"-"+filterValue["to"];
                            } else filterEditValue= filterEditValue+filterValue;
                        }
                    }
                    createMenuItem(filterMenu,colProp+"edit","edit",{label:"Значение: ",value:filterEditValue});
                }
                createMenuItem(filterMenu,colProp+"_buttonOK","button",{label:"Применить фильтр"});
            };
            this.handsontable.hideFilterMenu= function () {//close filter menu
                var filterMenu = this.filterMenu;
                if(filterMenu){ filterMenu.isOpen= false; filterMenu.style.display = 'none'; }
            };
            //var comments = this.handsontable.getPlugin('comments'); if(comments) comments.editor.setPosition = this.setCommentPosition;
            this.resizePane = this.resize; this.resize = this.resizeAll;
        },
        getHandsontable: function(){ return this.handsontable; },
        setHT: function(params){
            this.handsontable.updateSettings(params);
        },
        resizeAll: function(changeSize,resultSize){
            this.resizePane(changeSize,resultSize);
            this.handsontable.updateSettings({/*width:this.domNode.clientWidth,*/ height:changeSize.h-2});
        },
        filterTableContentData: function(){
            var htviscolumns= this.htviscolumns;
            this.htfiltereddata = this.htdata.filter(function(T,number,Arr){                                                //console.log("HTable filterTableContentData  filter ",T,number,Arr);
                for(var colIndex in htviscolumns){
                    var colProps = htviscolumns[colIndex];
                    if(colProps["filtered"]==true&&T){
                        var colProp = colProps["data"];
                        var dataItemVal = T[colProp]; if(dataItemVal==null) dataItemVal="";
                        if(colProps["type"]=="text"&&dataItemVal!==undefined){
                            if(colProps["filterValue"]&&dataItemVal.indexOf(colProps["filterValue"])<0) return false;
                            if(colProps["filterValues"]&&colProps["filterValues"][dataItemVal]==false) return false;
                        } else if(colProps["type"]=="numeric"&&dataItemVal!==undefined&&colProps["filterValues"]) {
                            var numericFilterValues = colProps["filterValues"];
                            var dataItemvisible = false;
                            for(var numericFilterValuesItem in numericFilterValues){
                                var numericFilterValue = numericFilterValues[numericFilterValuesItem];
                                if(numericFilterValue instanceof Array){
                                    if(numericFilterValue["from"]&&numericFilterValue["to"]
                                        &&dataItemVal>=numericFilterValue["from"]&&dataItemVal<=numericFilterValue["to"]){
                                        dataItemvisible= true; break;
                                    } else if(numericFilterValue["from"]&&!numericFilterValue["to"]&&dataItemVal>=numericFilterValue["from"]){
                                        dataItemvisible= true; break;
                                    } else if(!numericFilterValue["from"]&&numericFilterValue["to"]&&dataItemVal<=numericFilterValue["to"]){
                                        dataItemvisible= true; break;
                                    }
                                } else if(numericFilterValue==dataItemVal){
                                    dataItemvisible= true; break;
                                }
                            }
                            if(dataItemvisible==false) return dataItemvisible;
                        }
                    }
                }
                return true;
            });
            return this.htfiltereddata;
        },
        updateTableContent: function(newdata) {                                                                 //console.log("HTable updateTableContent htviscolumns=", this.htviscolumns,this.htdata);
            if(newdata!==undefined) this.setData(newdata);                                                      //console.log("HTable updateTableContent htdata=", this.htdata,this.getTableContent());
            if(this.htdata!==null) {//loadTableContent
                this.handsontable.updateSettings({columns:this.htviscolumns, comments:this.enableComments, readOnly:this.readOnly, minSpareRows:this.htaddRows});
                var filtereddata= this.filterTableContentData();
                this.handsontable.selRowIndex= undefined;
                this.handsontable.selRowValues= undefined;
                /*this.handsontable.selRowValues setted up in this.handsontable.cellValueRenderer
                when call this.handsontable.render() or call this.handsontable.loadData(..);*/
                this.handsontable.loadData(filtereddata);
                //if(this.enableComments){
                //    for(var iCurCommentRow in this.htComments){
                //        var curRowComments= this.htComments[iCurCommentRow];
                //        if(curRowComments){
                //            for(var col in curRowComments){
                //                var commentVal = curRowComments[col];
                //                if(commentVal&&commentVal!=null) this.showComment(iCurCommentRow,0,curCommentVal);
                //            }
                //        }
                //    }
                //}
            } else {//clearTableDataContent
                this.clearTableContent();
            }
        },
        setTableContent: function(newdata) {                                                                            //console.log("HTable setTableContent newdata=", newdata);
            this.updateTableContent(newdata);
        },
        clearTableContent: function() {                                                                                 //console.log("HTable clearTableContent");
            this.handsontable.updateSettings({columns:this.htviscolumns, comments:false, readOnly:false, minSpareRows:0});
            this.handsontable.selRowIndex= undefined;
            this.handsontable.loadData([]);
            this.handsontable.selRowValues=null;
        },
        getTableContent: function(){
            //return this.htfiltereddata;
            return this.handsontable.getSourceData();
        },
        onUpdateContent: function(){                                                                                    //console.log("HTable onUpdateContent");
            //TODO actions on/after update table content (after set/reset/reload/clear table content data or data item or after set/clear table filters)
        },
        loadContentFromUrl: function(condition,postaction){
            var instance = this;
            getJSONData({url:this.getURL, condition:condition, consoleLog:true},/*postaction*/function(success,result){
                if(success==true){
                    if(result.error) console.log("HTable.loadContentFromUrl getJSONData DATA ERROR! error=",result.error);
                    instance.updateTableContent(result);
                } else {
                    console.log("HTable.loadContentFromUrl getJSONData error=",result);/*IT'S FOR TESTING*/
                    instance.updateTableContent();
                }
                if (postaction) postaction(success,result);
            });
        },
        loadContentFromUrlByAction: function(condition,actionData,postaction){
            var instance = this;
            postJSONData({url:this.getURL, condition:condition, data:actionData, consoleLog:true},/*postaction*/function(success,result){
                if(success==true){
                    if(result.error) console.log("HTable.loadContentFromUrlByAction postJSONData DATA ERROR! error=",result.error);
                    instance.updateTableContent(result);
                } else {
                    console.log("HTable.loadContentFromUrlByAction postJSONData error=",result);/*IT'S FOR TESTING*/
                    instance.updateTableContent();
                }
                if (postaction) postaction(success,result);
            });
        },
        setSelectedRow: function(rowIndex){
            this.handsontable.selectCell(rowIndex,0,rowIndex,0);
        },
        onSelect: function (rowIndex, rowIDVal, rowDataValues){
            //TODO actions on/after row select by user or after call setSelectedRow/setSelectedRowByID
            this.setSelection(rowIndex, rowIDVal, rowDataValues);
        },
        getSelRowIndex:function(){return this.handsontable.selRowIndex;},
        getSelIDVal:function(){
            if(this.handsontable.selRowValues!==undefined&&this.handsontable.selRowValues!==null&&this.handsontable.rowIDName!==null){
                return this.handsontable.selRowValues[this.handsontable.rowIDName];
            } else return undefined;
        },
        getSelValues:function(){
            /*
             this.handsontable.selRowValues setted up in this.handsontable.cellValueRenderer
             when call this.handsontable.render() or call this.handsontable.loadData(..);
             */
            if(this.handsontable.selRowValues===undefined) return null;
            return this.handsontable.selRowValues;
        },
        setSelection:function(selRowIndex, selIDVal, selDataValues){//callback onSelect
            this.handsontable.selRowIndex=selRowIndex;
            this.handsontable.selRowIDValue=selIDVal;
            if(selDataValues)this.handsontable.selRowValues=selDataValues; else this.handsontable.selRowValues= undefined;
            this.handsontable.render();
        },
        setSelectionByID:function(selIDVal, selDataValues){
            this.handsontable.selRowIDValue=selIDVal;
            if(selDataValues)this.handsontable.selRowValues=selDataValues; else this.handsontable.selRowValues= undefined;
            this.handsontable.selRowIndex= undefined;
            this.handsontable.render();
        },
        //getDataAtRowProp: function(row, prop){
        //    return this.handsontable.getDataAtRowProp(row, prop);
        //},
        setDataAtRowProp: function(row, prop, value, setSource){
            this.handsontable.setDataAtRowProp(row, prop, value, setSource);
        },
        setRowData: function(row, newRowData, setSource){                                                                            //console.log("HTable.setRowData newRowData=",newRowData);
            var propsValues = [];
            for(var rowProp in newRowData){ propsValues[propsValues.length]= [row, rowProp, newRowData[rowProp] ]; }
            var rowSourceData = this.handsontable.getSourceDataAtRow(row);
            for(var item in rowSourceData){
                if(!newRowData[item]) propsValues[propsValues.length]= [ row, item, null ];
            }                                                                                                           //console.log("HTable.setRowData propsValues=",propsValues,row);
            this.handsontable.setDataAtRowProp(propsValues, setSource);
        },
        addRowData: function(row, newRowData, setSource){                                                               console.log("HTable.addRowData newRowData=",newRowData);
            var propsValues = [];
            for(var rowProp in newRowData){ propsValues[propsValues.length]= [row, rowProp, newRowData[rowProp] ]; }
            this.handsontable.setDataAtRowProp(propsValues, setSource);
        },
        getPosIndexForNewRow: function(posDataName){
            var selRowIndex = this.getSelRowIndex();
            var newRowIndex=0,newRowPOS = 1;
            if(selRowIndex!==undefined&&selRowIndex!=null) {
                newRowIndex=selRowIndex+1;
                var prevRow = this.getTableContent()[newRowIndex-1],
                    curRow = this.getTableContent()[newRowIndex];
                var prevRowPOS=1;                                                                   //console.log("Htable getPosIndexForNewRow prevRow=",prevRow," curRow=",curRow);
                if (prevRow!==undefined) prevRowPOS= prevRow[posDataName];
                newRowPOS = prevRowPOS+1;
                if (curRow!==undefined) newRowPOS= (prevRowPOS+curRow[posDataName])/2;
            } else {
                var curRow = this.getTableContent()[newRowIndex];
                if (curRow!==undefined) newRowPOS= curRow[posDataName]/2;
            }
            return newRowPOS;
        },
        insertRow: function(rowIndex, posIndexItemName, posItemName){
            var posIndex;
            if (posIndexItemName) {
                posIndex = this.getPosIndexForNewRow(posIndexItemName);
                if (isNaN(posIndex)){
                    console.log("HTable failed insertRow! Reason:PosIndex is NAN!");
                    return;
                }
            }                                                                                                           //console.log("HTable insertRow",rowIndex, posIndexItemName, posIndex);
            this.handsontable.alter('insert_row', rowIndex);

            if (this.htdata===undefined || this.htdata===null) this.htdata=[];
            this.htdata[this.htdata.length] = this.htfiltereddata[rowIndex];                                            //console.log("Htable insertRow ",this.htdata,this.htfiltereddata);
            var newRowData = [];
            if (posIndex) {
                newRowData[posIndexItemName] = posIndex;
                if (posItemName) newRowData[posItemName] = Math.floor(posIndex);
            }
            this.setRowData(rowIndex,newRowData);
            this.setSelectedRow(rowIndex);
        },
        deleteRow: function(rowIndex){
            var dataCount = this.getTableContent().length;
            this.handsontable.alter('remove_row', rowIndex);
            if (dataCount-1===rowIndex || (dataCount===1&&rowIndex===0) ) this.setSelection();
        },
        allowEditRow: function(rowIndex){
            this.setDataAtRowProp(rowIndex, "___$loaded$___", false, "loadData");
        },
        copySelRow: function(posIndexItemName,posItemName){
            var rowIndex = this.getSelRowIndex(), posIndex;
            if (rowIndex===undefined||rowIndex===null) return;
            var newRowData = [], selRowValues = this.getSelValues();
            if (selRowValues)
                for(var rowDataItemName in selRowValues){
                    if(rowDataItemName.indexOf("$error")<0) newRowData[rowDataItemName] = selRowValues[rowDataItemName];
                }
            newRowData["___$loaded$___"] = false;
            if (posIndexItemName) {
                posIndex = this.getPosIndexForNewRow(posIndexItemName);
                if (isNaN(posIndex)){
                    console.log("HTable failed copySelRow! Reason:PosIndex is NAN!");
                    return;
                }
                newRowData[posIndexItemName] = posIndex;                                                                //console.log("HTable copyRow",rowIndex, posIndexItemName, posIndex);
                if (posItemName) newRowData[posItemName] = Math.floor(posIndex);
            }
            if (this.getRowIDName()) newRowData[this.getRowIDName()]= null;
            this.handsontable.alter('insert_row', rowIndex+1);
            this.loadRowData(rowIndex+1,newRowData);
            this.setSelectedRow(rowIndex+1);
        },
        isExistsUnloadedData: function(){
            var tableContentData = this.getTableContent();
            if (tableContentData===undefined||tableContentData===null) return false;
            for(var row in tableContentData){
                var tableContentItem = tableContentData[row];
                if (tableContentItem["___$loaded$___"]!==true) return true;
            }
            return false;
        },
        //onChangeCell: function(row, prop, changedCellData){},
        onChangeCells: function(changedRowsData){                                                                       //console.log("HTable.onChangeCells changedRowsData=",changedRowsData);
            for(var changedRow in changedRowsData){
                var rowData = this.getTableContent()[changedRow], rowDataWithChanges=changedRowsData[changedRow];           //console.log("HTable.onChangeCells changedRowData=",changedRowData);
                rowData['___$loaded$___'] = false;
                for(var dataItemName in rowData){
                    if (dataItemName.indexOf("$error")<0){
                        var itemValue= rowData[dataItemName], changedValue= rowDataWithChanges[dataItemName];
                        if (changedValue!==undefined && typeof changedValue==='object') {
                            if (changedValue.oldValue===undefined) rowDataWithChanges[dataItemName] = itemValue;
                        } else if (changedValue===undefined) {
                            rowDataWithChanges[dataItemName] = { oldValue:itemValue };
                        }
                    }
                }
                var rowIDName= this.handsontable.rowIDName;
                if (rowIDName!==undefined && !rowDataWithChanges.hasOwnProperty(rowIDName)){
                    var idValue = rowData[rowIDName], changedIDValue= rowDataWithChanges[rowIDName];
                    if (changedIDValue!==undefined && typeof changedIDValue==='object') {
                        if (changedIDValue.oldValue===undefined) rowDataWithChanges[rowIDName] = idValue;
                    } else if (changedValue===undefined) {
                        rowDataWithChanges[rowIDName] = { oldValue:idValue };
                    }
                }
                this.onChangeRowData(parseInt(changedRow),rowDataWithChanges);                                                        //console.log("HTable.onChangeCells changedRowData=",changedRowData);
            }
        },
        onChangeRowData: function(changedRow,rowDataWithChanges){                                                       console.log("HTable.onChangeRowData changedRow=",changedRow," changedRowsData=",rowDataWithChanges);
            //TODO actions after change user or set (non load) row data
            var condition= null;
            this.loadRowData(changedRow, this.getStoredDataFromChanges(rowDataWithChanges));
            this.storeRowDataToURL(condition,changedRow,rowDataWithChanges,null/*no postaction*/);
        },
        getChangeItemValue: function(changedRowData, itemName){
            var value = changedRowData[itemName];
            if (value===undefined || value===null) return value;
            if (typeof value==='object')
                if (value.newValue!==undefined) return value.newValue; else return value.oldValue;
            return value;
        },
        isChangeItemHasValue: function(changedRowData, itemName){
            var value = this.getChangeItemValue(changedRowData, itemName);
            if (value===undefined || value===null) return false;
            return true;
        },
        isChangeItemEmptyValue: function(changedRowData, itemName){
            var value = this.getChangeItemValue(changedRowData, itemName);
            if (value===undefined || value===null || value.toString().length===0) return true;
            return false;
        },
        isChangeItemEmptyZeroValue: function(changedRowData, itemName){
            var value = this.getChangeItemValue(changedRowData, itemName);
            if (value===undefined || value===null || value.toString().length===0 || value===0) return true;
            return false;
        },
        isChangeItemNumberValue: function(changedRowData, itemName){
            var value = this.getChangeItemValue(changedRowData, itemName);
            if ((undefined === value) || (null === value) || (value.toString().trim().length===0)) return false;
            if (typeof value == 'number') return !isNaN(value);
            return !isNaN(value - 0);
        },
        isChangeItemNumberNonZeroValue: function(changedRowData, itemName){
            var value = this.getChangeItemValue(changedRowData, itemName);
            if ((undefined === value) || (null === value) || (value.toString().trim().length===0)) return false;
            if (typeof value == 'number') return !isNaN(value) && value!==0;
            var dvalue = value - 0;
            return !isNaN(dvalue) && dvalue!==0;
        },
        setChangeItem: function(changedRowData, itemName, value){
            if (changedRowData[itemName]===undefined) changedRowData[itemName] = { newValue: value };
            else changedRowData[itemName].newValue = value;
        },
        setChangeItemIFNull: function(changedRowData, itemName, value){
            var changedValue = this.getChangeItemValue(changedRowData, itemName);
            if (changedValue===undefined || changedValue===null) changedRowData[itemName] = { newValue: value };
        },
        setChangeItemIFEmpty: function(changedRowData, itemName, value){
            var changedValue = this.getChangeItemValue(changedRowData, itemName);
            if (changedValue===undefined || changedValue===null || changedValue.toString().length===0)
                this.setChangeItem(changedRowData, itemName, value);
        },
        isChangedItemValue: function(changedRowData, itemName){
            var value = changedRowData[itemName];
            if (value!==undefined && value!==null
                && value.newValue!==undefined && value.newValue!==value.oldValue ) return true;
            return false;
        },
        isChangedNoEmptyItemValue: function(changedRowData, itemName){
            var value = changedRowData[itemName];
            if (value!==undefined && value.newValue!==undefined
                && value.newValue!==null && value.newValue.toString().length>0) return true;
            return false;
        },
        getStoredDataFromChanges: function(changedRowData){
            var storingData = [];
            for(var changedDataItemName in changedRowData){
                if (changedDataItemName.indexOf("$error")<0){
                    var storeItemValue=undefined;
                    var changedValue= changedRowData[changedDataItemName];
                    if (changedValue!==undefined && typeof changedValue==='object') {
                        if (changedValue.newValue!==undefined) storeItemValue = changedValue.newValue;
                        else if (changedValue.oldValue!==undefined) storeItemValue = changedValue.oldValue;
                    } else if (changedValue!==undefined) {
                        storeItemValue= changedValue;
                    }
                    storingData[changedDataItemName] = storeItemValue;
                }
            }
            return storingData;
        },
        storeRowDataToURL: function(condition,storeRow,storeRowData, postaction){
            if(!this.postURL || !this.postChanges){
                console.log("Function storeRowDataToURL cannot execute with NULL or EMPTY URL or if not set postChanges!"); return;
            }
            var storingData = {};
            for(var dataItem in storeRowData){
                if (dataItem.indexOf("$error")<0) storingData[dataItem] = storeRowData[dataItem];
            }
            var instance = this;                                                                                        console.log("Htable storeRowDataToURL storingData=",storingData);
            postJSONData({url:this.postURL,condition:condition,data:storingData, consoleLog:true},function(success,result){
                if(success==true){
                    var resultItem = result["resultItem"], updateCount = result["updateCount"], error = result["error"];
                    if(!resultItem) { resultItem={}; resultItem['$error_resultItem']="Не удалось получить результат операции с сервера!"; }
                    if(error) {
                        resultItem["$error_"] = error;
                        console.log("HTable.storeRowDataToURL resultItem ERROR! error=",error);/*!!!TEST LOG!!!*/
                    }
                    if(!updateCount>0) resultItem["$error_updateCount"]= "Данные не были сохранены на сервере!";
                    instance.loadRowData(storeRow, resultItem);                                                       console.log("HTable.storeRowDataToURL resultItem=",resultItem);
                    instance.setErrorsCommentsForRow(storeRow,resultItem);
                } else {
                    resultItem= storeRowData;
                    if(!resultItem) resultItem= {};
                    resultItem["$error_"]= "Нет связи с сервером!";
                    instance.setErrorsCommentsForRow(storeRow,resultItem);
                }
                if(postaction) postaction(success,result);
            })
        },
        loadRowData: function(row, rowData){                                                                            //console.log("HTable.loadRowData rowSourceData=",rowSourceData);
            this.setRowData(row, rowData, 'loadData');
        },
        loadDataAtRowProp: function(row, prop, value){
            this.setDataAtRowProp(row, prop, value, 'loadData');
        },
        setCommentPosition: function(x, y) {
            this.editorStyle.left = x + 'px'; this.editorStyle.top = (y+20) + 'px';
        },
        showComment:function(row, col, commentval){
            var commentsPlugin = this.handsontable.getPlugin('comments');
            if(commentsPlugin) commentsPlugin.editor.setPosition = this.setCommentPosition;
            if(commentval){
                commentsPlugin.editor.setValue(commentval);
                commentsPlugin.saveCommentAtCell(row, col);
                commentsPlugin.editor.getInputElement().readOnly = true;
                commentsPlugin.showAtCell(row, col);
            } else {
                commentsPlugin.removeCommentAtCell(row, col);
            }
        },
        disabledComments: function(){
            return !this.handsontable.getPlugin('comments').enabled;
        },
        setCommentForRow:function(row, commentval){
            if(this.disabledComments()) return;
            this.htComments[row][0]=commentval;
            this.showComment(row,0,commentval);
        },
        setCommentsForRow:function(row, commentsval, itemPrefix){
            if(this.disabledComments()) return;
            var rowComments= this.htComments[row];
            if(!rowComments) { rowComments = []; this.htComments[row] = rowComments; }
            for(var i=0;i<this.htviscolumns.length;i++){
                var colData=this.htviscolumns[i];
                var colProp=colData["data"];
                var commentVal = null;
                if(itemPrefix){ commentVal= commentsval[itemPrefix+colProp]; } else { commentVal= commentsval[colProp]; }
                var curComment = rowComments[i];
                if(commentVal){
                    rowComments[i] = commentVal; this.showComment(row,i,commentVal);//show comment
                } else if (!commentVal&&curComment) {
                    if (curComment!=null) {
                        rowComments[i] = null; this.showComment(row,i,null);//remove comment
                    }
                }
            }
        },
        setErrorsCommentsForRow:function(row, commentsval){ this.setCommentsForRow(row, commentsval, '$error_') }
    });
});
