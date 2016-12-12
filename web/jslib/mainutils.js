/**
 * Created by dmkits on 19.02.16.
 */

/**
 * Inited DOJO element for HTML element
 * @param registry
 * @param ID
 * @param htmlElemName
 * @param Class
 * @param Params
 * @returns {*}
 */
function initElem(registry, ID, htmlElemName, Class, Params) {
    var Object = null;
    if (registry!=null) Object = registry.byId(ID);
    if (Object==null) {
        Params.id = ID;
        Object = new Class(Params, htmlElemName);
    }
    return Object;
}
/**
 * Inited DOJO child element for DOJO parent element
 * @param registry
 * @param ID
 * @param Parent
 * @param Class
 * @param Params
 * @returns {*}
 */
function initChild(registry, ID, Parent, Class, Params) {
    var Object = null;
    if (registry!=null) Object = registry.byId(ID);
    if (Object==null) {
        Params.id = ID;
        Object = new Class(Params);
        if (Parent!=null) Parent.addChild(Object);
    }
    return Object;
}

var jsonHeader = {"X-Requested-With":"application/json; charset=utf-8"};
/**
 * doing JSON request and call onSuccess or onError
 * call preAction before send request.
 */
function doJSONRequest(jsonDataURI,requestMethod,reqJSONData, preAction,onSuccess,onError) {
    require(["dojo/request/xhr", "dijit/registry", "dojo/domReady!"],
        function (xhr, registry) {
            if (preAction!=null) reqJSONData = preAction(reqJSONData,registry);
            if (requestMethod!="post") {//get
                xhr.get(jsonDataURI, {headers:jsonHeader,handleAs:"json"}).then(
                    function(data){
                        onSuccess(data, registry);
                    }, function(error){
                        onError(error, registry);
                    })
            } else {//post
                xhr.post(jsonDataURI, {headers:jsonHeader,handleAs:"json",data:reqJSONData}).then(
                    function(data){
                        onSuccess(data, registry);
                    }, function(error){
                        onError(error, registry);
                    })
            }
        })
}

var showRequestErrorDialog= false;//global
function doRequestErrorDialog(){
    doDialogMsg({title:"Внимание",content:"Невозможно завершить операцию! <br>Нет всязи с сервером!",
        style:"width:300px;", btnOkLabel:"OK", btnCancelLabel:"Закрыть"});
}

/** getJSONData
 * data.url, data.condition, data.consoleLog
 * if success : callback(true,data), if not success callback(false,error)
 * @param data
 * @param callback
 */
function getJSONData(data,callback){
    if (!data) return;
    require(["dojo/request/xhr", "dojo/domReady!"],
        function (xhr) {
            var url= data["url"],condition=data["condition"],consoleLog=data["consoleLog"];
            if(condition) url=url+"?"+condition;
            xhr.get(url, {headers:jsonHeader,handleAs:"json"}).then(
                function(data){
                    if(callback)callback(true, data);
                }, function(error){
                    if (showRequestErrorDialog) doRequestErrorDialog();
                    if(consoleLog) console.log("getJSONData ERROR! url=",url," error=",error);
                    if(callback)callback(false, error);
                })
        });
}
/** postJSONData
 * data.url, data.condition, data.data, data.consoleLog
 * if success : callback(true,data), if not success callback(false,error)
 * @param data
 * @param callback
 */
function postJSONData(data,callback){
    if (!data) return;
    require(["dojo/request/xhr", "dojo/domReady!"],
        function (xhr) {
            var url= data["url"],condition=data["condition"],consoleLog=data["consoleLog"];
            if(condition) url=url+"?"+condition;
            xhr.post(url, {headers:jsonHeader,handleAs:"json",data:data["data"]}).then(
                function(data){
                    if(callback)callback(true, data);
                }, function(error){
                    if (showRequestErrorDialog)  doRequestErrorDialog();
                    if(consoleLog) console.log("postJSONData ERROR! url=",url," error=",error);
                    if(callback)callback(false, error);
                })
        });
}

function registredAction(action){
    require(["dijit/registry", "dojo/dom-style", "dojo/domReady!"], function (registry, domStyle) {
        action(registry,domStyle);
    })
}

/**
 * sending AJAX JSON-request to jsonDataURI with parameter "context"= dojoContextName
 * and getting JSON-response from server with data;
 * setting getted data to dojo objects by id.
*/
function setContextData(jsonDataURI,dojoContextName) {
    console.log("setContextData: contentName=",dojoContextName);//!!!IT'S FOR TESTING!!!
    if (dojoContextName!=null) { jsonDataURI = jsonDataURI + "?context="+dojoContextName}
    doJSONRequest(jsonDataURI,"get",{}, null,
        function(data,registry) {//onSuccess request
            registry.byId(dojoContextName).set("jsonData",data);
            for (var id in data) {
                var dojoObj = registry.byId(id);
                if (dojoObj!=null) {
                    dojoObj.set(data[id].attribute,data[id].value);
                } else { console.warn("setContextData WARNING: dojo id=",id," not founded!") }
            }
        })
}
/**
 * sending AJAX JSON-POST-request to jsonDataURI with parameter "context"= dojoContextName
 * and getting JSON-response from server with data- result of posted;
 * sending only changed values of dojo objects.
 * @param jsonDataURI
 * @param dojoContextName
 */
function sendContextData(jsonDataURI, dojoContextName) {
    console.log("sendContextData: contentName=",dojoContextName);//!!!IT'S FOR TESTING!!!
    if (dojoContextName!=null) { jsonDataURI = jsonDataURI + "?context="+dojoContextName}
    var jsonData = {};
    doJSONRequest(jsonDataURI,"post",jsonData,
        function(jsonData,registry) {//preAction
            try {
                var dojoContext = registry.byId(dojoContextName);
                var items = dojoContext.getChildren();
                for(var i in items) {
                    var item = items[i];                                                                                console.log("sendContextData: context item=",item," value=",item.value);//!!!IT'S FOR TESTING!!!
                    if (dojoContext.jsonData[item.id]!=null) { //if context's object in jsonData
                        var objValue = item.get(dojoContext.jsonData[item.id].attribute);
                        if (objValue!=dojoContext.jsonData[item.id].value) {
                            //set obj value to data to sending
                            jsonData[item.id] = item.get(dojoContext.jsonData[item.id].attribute);
                        }
                    }
                }
            } catch(e) { console.error("sendContextData preAction ERROR:",e); }                                         console.log("sendContextData: posting data=",jsonData);//!!!IT'S FOR TESTING!!!
            return jsonData;
        }, function(data,registry){//onSuccess
                                                                                                                        console.log("sendContextData: post response=",data);//!!!IT'S FOR TESTING!!!
        })
}
/**
 * ContentController for loadData and store changed data this ContentController elements.
 * @param parameters: getURL, postURL
 * @returns new object of ContentController.
 * use addElement for added new element to controller
 * use setDataFromUrl for set controller elements values from url
 * set onResetAction for do actions after reset content all elements
 * set onContentChanged for do action after changed value of controller element
 * use postDataToUrl for post controller elements values to url
 */
function ContentController(parameters) {
    var contentController = {
        data: null,
        dataIDName: null,
        elements: {},
        addElement: function (itemName, elementObj) {
            this.elements[itemName] = elementObj;
            var declaredClass = elementObj.declaredClass, instance = this;
            elementObj.onChange = function (newValue) {
                instance.onElementChanged(instance, itemName, elementObj);
            };
            if (declaredClass.indexOf("TextBox") >= 0) {
                elementObj.on("keyup", function (event) {
                    instance.onElementChanged(instance, itemName, elementObj);
                });
            } else if (declaredClass.indexOf("DateTextBox") >= 0) {
            } else if (declaredClass.indexOf("Select") >= 0) {
                this.setSelectDropDown(elementObj);
            } //else
            elementObj.set("disabled", true);
        },
        setSelectDropDown: function (selectObj) {
            selectObj.selectloadDropDown = selectObj.loadDropDown;
            var loadDropDownURL = selectObj.loadDropDownURL;
            selectObj.loadDropDown = function (loadCallback) {                                                          //console.log("ContentController.setSelectDropDown loadDropDown ",this);
                getJSONData({url: loadDropDownURL, consoleLog: true},
                    function (success,data) {
                        if (success) {                                                                                  //console.log("ContentController.setSelectDropDown loadDropDown getJSONData data=",data);
                            var value = selectObj.get("value");
                            selectObj.set("options", data.items);
                            selectObj.set("value", value);
                        }
                        selectObj.selectloadDropDown(loadCallback);
                    });
            };
        },
        getElement: function (itemName) {
            return this.elements[itemName];
        },
        resetAllElements: function (contentData, markElemens) {                                                         //console.log("ContentController.resetAllElements contentData=",contentData," this.data=",this.data);
            for (var item in this.elements) {
                var elementObj = this.elements[item], elementValue = undefined;
                if (contentData !== undefined && contentData !== null) {
                    elementValue = contentData[item];
                    if(elementValue===undefined)elementValue=null;
                }
                if (elementValue === undefined) {
                    elementObj.set("value", null, false);
                    this.markElementAsChanged(elementObj, false);
                    elementObj.set("disabled", true);

                } else {
                    if(elementObj.disabled)elementObj.set("disabled",false);
                    if (elementObj.declaredClass.indexOf("Select") >= 0) {
                        if (contentData !== undefined && contentData != null && elementValue) {
                            var newOption = {
                                label: contentData[elementObj.labelDataItem],
                                value: elementValue,
                                selected: true
                            };
                            if (!elementObj.options || elementObj.options.length == 0) elementObj.set("options", [newOption]);
                            else if (elementObj.options && elementObj.options.length > 0) {
                                var founded = false;
                                for (var i in elementObj.options)
                                    if (elementObj.options[i]["value"] == elementValue) {
                                        founded = true;
                                        break;
                                    }
                                if (founded == false)  elementObj.options[elementObj.options.length] = newOption;
                            }
                        }
                    }
                    elementObj.value = elementValue;
                    elementObj.set("value", elementValue, false);                                               //console.log("ContentController.resetAllElements item=",item," value=",elementValue,"data value=",this.data[item]," markElemens=",markElemens);
                    if (markElemens == true) this.markElementAsChanged(elementObj, true);
                    else {
                        var dataIDValue = null;
                        if (this.dataIDName && this.data[this.dataIDName] !== undefined) dataIDValue = this.data[this.dataIDName];
                        var isMarked = (dataIDValue == null);
                        if (isMarked == false) {
                            var dataValue = this.data[item];
                            var isEquals = ( ((dataValue === undefined || dataValue == null || dataValue == '') && (elementValue === undefined || elementValue == null || elementValue == ''))
                                || (dataValue.toString() == elementValue.toString())  );
                            isMarked = !isEquals;
                        }
                        this.markElementAsChanged(elementObj, !isEquals);
                    }
                }
            }
        },
        getData: function(){
            return this.data;
        },
        getDataItem: function (itemName) {
            if (!this.data) return null;
            return this.data[itemName];
        },
        getDataIDValue: function () {
            if (!this.data||!this.dataIDName) return undefined;
            return this.data[this.dataIDName];
        },
        setData: function (newData) {
            if(newData===null) {
                this.data= null;
                return;
            }
            this.data = [];
            if(this.dataIDName) {
                var dataIDValue= null;
                if(newData&&newData[this.dataIDName]!==undefined) dataIDValue= newData[this.dataIDName];
                this.data[this.dataIDName]= dataIDValue;
            }
            if(!this.elements) return;
            for(var itemName in this.elements){
                if(!newData||newData[itemName]===undefined) this.data[itemName]=null; else this.data[itemName]=newData[itemName];
            }                                                                                                           //console.log("ContentController.setData data=",this.data);
        },
        clearDataItems: function () {
            for (var item in this.data) {
                this.data[item] = null;
            }
        },
        setContent: function (newData, updateResult) {                                                                  //console.log("ContentController.setContent newData=",newData);
            if (updateResult===false){
                this.displayStateMessageonContentUpdated(false);
                return;
            }
            //if updateResult===undefined || updateResult===true
            this.setData(newData);
            this.resetAllElements(newData);
            this.displayStateMessageonContentUpdated(updateResult);
            this.onContentUpdated(this.getData(), false);
        },
        setContentValues: function (newData) {                                                                          //console.log("ContentController.setContentValues newData=",newData);
            this.setData(newData);
            this.clearDataItems();
            this.resetAllElements(newData,true);
            this.displayStateMessageonContentUpdated();
            if(newData===null) {
                this.onContentChanged(this.getData(), false);
            } else {
                this.onContentUpdated(this.getData(), true);
            }
        },
        onContentUpdated: function (contentData,isContentChanged) {
            // TODO actions on content data has been updated by call setContent() or setContentValues()
        },
        isElementDataEquals: function (instance, itemName, elementObj) {                                                //console.log("ContentController.isElementDataEquals ",itemName,elementObj.value,elementObj.get("value"),elementObj.displayedValue);
            var dataValue = undefined;
            if (instance.data!==null) {
                dataValue = instance.data[itemName];
                if (dataValue === undefined) dataValue = null;
            }
            if (dataValue===undefined) return true;
            var elementObjValue = elementObj.get("value");
            if ((elementObjValue instanceof Date) && !(dataValue instanceof Date)) {
                dataValue = new Date(dataValue);
                dataValue.setHours(0, 0, 0, 0);
                elementObjValue.setHours(0, 0, 0, 0);
            }
            if (elementObj.declaredClass.indexOf("CheckBox") >= 0) {
                if(elementObj.checked==true) elementObjValue=1; else elementObjValue=0;
            }
            if (elementObjValue != null) elementObjValue = elementObjValue.toString();
            if (dataValue != null) dataValue = dataValue.toString();
            var result = (elementObjValue == dataValue)
                || (elementObjValue == "" && dataValue == null);                      //console.log("ContentController.isElementDataEquals ",itemName,elementObjValue,elementObj.get("value"),dataValue,result /*,elementObjValue instanceof Date,dataValue instanceof Date*/);
            return result;
        },
        markElementAsChanged: function (elementObj, markAsChanged) {                                                    //console.log("ContentController.markElementAsChanged markAsChanged",markAsChanged,elementObj);
            var markNode = null;
            var declaredClass = elementObj.declaredClass;
            if (declaredClass.indexOf("TextBox") >= 0) {
                markNode = elementObj.textbox;
            } else if (declaredClass.indexOf("DateTextBox") >= 0) {
                markNode = elementObj.textbox;
            } else if (declaredClass.indexOf("Select") >= 0) {
                markNode = elementObj.textDirNode;
            } //else //markNode=
            if (markAsChanged) {
                elementObj.domNode.classList.add("contentControllerMarkedElement");
                //elementObj.domNode.style.color= 'red';
                if(markNode) markNode.style.color='red';
            } else {
                elementObj.domNode.classList.remove("contentControllerMarkedElement");
                //elementObj.domNode.style.color= '';
                if(markNode) markNode.style.color='';
            }
        },
        onElementChanged: function (instance, itemName, elementObj) {
            var isMarkElement = false;
            if(this.dataIDName&&this.data[this.dataIDName]===null) isMarkElement=true;
            if(isMarkElement==false)
                isMarkElement = !instance.isElementDataEquals(instance, itemName, elementObj);
            instance.markElementAsChanged(elementObj, isMarkElement);                                                   //console.log("onElementChanged isMarkElement=",isMarkElement);
            if(isMarkElement==true) {
                instance.displayStateMessageonContentChanged(true);
                instance.onContentChanged(true);
            } else {
                var isContentChanged= instance.isContentChanged();
                instance.displayStateMessageonContentChanged(isContentChanged);
                instance.onContentChanged(isContentChanged);
            }
        },
        isContentChanged: function () {                                                                                 //console.log("ContentController.isContentChanged data=",this.data," dataIDName=",this.dataIDName);
            if(this.data===null) return false;
            if(this.dataIDName&&this.data[this.dataIDName]===null) return true;
            var isChanged = false;
            for (var item in this.elements) {
                var elementObj = this.elements[item];
                if (this.isElementDataEquals(this, item, elementObj) == false) {
                    isChanged = true;
                    break;
                }
            }                                                                                                           //console.log("ContentController.isContentChanged return=",isChanged);
            return isChanged;
        },
        onContentChanged: function (isContentChanged) {
            // TODO actions on content has been changed by user or element value has been changed
        },
        stateElementID: null,
        setStateElement: function(stateElementID){
            this.stateElementID = stateElementID;
            this.setDefaultStateMessages();
        },
        stateMessages: [],
        setDefaultStateMessages: function(){
            this.stateMessages["loadError"]= "<div><b style='color:red'>Не удалось загрузить данные с сервера!<br>Нет связи с сервером.</br></div>";
            this.stateMessages["readData"]= "<div>Просмотр данных.</div>";
            this.stateMessages["noData"]= "<div>Нет данных.</div>";
            this.stateMessages["newData"]= "<div><b style='color:red'>Новая запись.</b></div>"
                +"<div><b>Для сохранения новой записи выберите действие \"Сохранить данные\".</b></div>"
                +"<div><b>Для отмены создания новой записи выберите действие \"Отменить изменение\".</b></div>";
            this.stateMessages["updateData"]= "<div><b style='color:red'>Изменение данных.</b></div>"
                +"<div><b>Для сохранения изменений выберите действие \"Сохранить данные\".</b></div>"
                +"<div><b>Для отмены изменений выберите действие \"Отменить изменение\".</b></div>";
            this.stateMessages["updateOK"]= "<div><b>Данные сохранены на сервере.</b></div>";
            this.stateMessages["updateError"]= "<div><b style='color:red'>Не удалось сохранить данные на сервере!<br> </br></div>";
        },
        getStateMessage: function(stateMessagesItemName){/*stateMessagesItemName = loadError/readData/noData/newData/updateData/postOK/postError or other*/
            if (!this.stateMessages) return "";
            var stateMessage = this.stateMessages[stateMessagesItemName];
            if (!stateMessage) return "";
            return stateMessage;
        },
        displayStateMessage: function(stateMessagesItemName){                              //console.log("ContentController.displayStateMessage stateElementID=",this.stateElementID," ",stateMessagesItemName," ",this.stateMessages[stateMessagesItemName]);
            if(this.stateElementID){
                var messageElement= document.getElementById(this.stateElementID);
                if(messageElement) messageElement.innerHTML = this.getStateMessage(stateMessagesItemName);
            }
        },
        displayStateMessageonContentUpdated: function(updateResult){
            if (this.data===null||this.data===undefined)  this.displayStateMessage("noData");
            else if (updateResult===undefined) {//data loaded
                if (this.getDataIDValue()===null) this.displayStateMessage("newData");
                else this.displayStateMessage("readData");
            } else {//updateResult!==undefined - data updated
                if (updateResult===false) this.displayStateMessage("updateError");
                else this.displayStateMessage("updateOK");
            }
        },
        displayStateMessageonContentChanged: function(isContentChanged){
            if (this.data===null) this.displayStateMessage("noData");
            else {
                if (isContentChanged!==true) this.displayStateMessage("readData");
                else {
                    if (this.getDataIDValue()===null) this.displayStateMessage("newData");
                    else this.displayStateMessage("updateData");
                }
            }
        },
        getURL: "/",
        setDataFromUrl: function (condition, postaction) {                                                              //console.log("ContentController.setDataFromUrl getJSONData url=",this.url," condition=",condition);
            var thisInstance = this;
            getJSONData({url: this.getURL, condition: condition, consoleLog: true},
                function (success,data) {
                    var itemData = null;
                    if (success) {                                                                                      //console.log("ContentController.setDataFromUrl getJSONData data=",data," data.item=",data["item"]);
                        if (data && data["item"]!==undefined) itemData = data["item"];
                        thisInstance.setContent(itemData);
                    } else {
                        console.log("ContentController.setDataFromUrl getJSONData error!");
                        thisInstance.setContent(null);
                    }
                    if (postaction) postaction(success, data, itemData);
                });
        },
        postURL: "/",
        postDataToUrl: function (condition, postaction) {
            var dataToPost = [];
            for (var item in this.data) {
                var value = this.data[item];
                var elementObj = this.elements[item];
                if (elementObj !== undefined) {
                    var newValue = this.elements[item].value;
                    if (elementObj.declaredClass.indexOf("CheckBox") >= 0) {
                        if(elementObj.checked==true) newValue=1; else newValue=0;
                    }
                    if (newValue !== undefined) {
                        if (newValue instanceof Date) newValue = moment(newValue).format("YYYY-MM-DD");
                        value = newValue;
                    }
                }
                dataToPost[item] = value;
            }
            var thisInstance = this;
            postJSONData({url: this.postURL, condition: condition, data: dataToPost, consoleLog: true},
                function (success,data) {                                                                               //console.log("ContentController.postDataToUrl postJSONData data=",data);
                    var resultItem = null, updateCount = null;
                    if (success) {
                        if (data) {
                            resultItem = data["resultItem"];
                            var error = data["error"];
                            if(error) console.log("ContentController.postDataToUrl postJSONData DATA ERROR! error=",error);
                            updateCount = data["updateCount"];
                            if (updateCount > 0) thisInstance.setContent(resultItem, true);
                            else thisInstance.setContent(resultItem, false);
                        }
                    }
                    if (postaction) postaction(success, data, updateCount, resultItem);
                });
        }
    };
    if (parameters) {
        if (parameters.dataIDName) contentController.dataIDName = parameters.dataIDName;
        if (parameters.getURL) contentController.getURL = parameters.getURL;
        if (parameters.postURL) contentController.postURL = parameters.postURL;
        if (parameters.stateElementID) contentController.setStateElement(parameters.stateElementID);
    }
    return contentController;
}

function today(){
    return moment().toDate();
}
function curMonthBDate(){
    return moment().startOf('month').toDate();
}
function curMonthEDate(){
    return moment().endOf('month').toDate();
}