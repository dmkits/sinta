package model.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by dmkits on 02.12.16.
 */
public class DMData {

    Object dataValue = null;
    HashMap<String,Object> dataItem= null;
    ArrayList<HashMap<String,Object>> dataList= null;

    private DMData(){}
    private DMData(Object dataValue){
        this.dataValue=dataValue;
    }
    private DMData(ArrayList<HashMap<String,Object>> dataList){
        this.dataList = dataList;
    }

    public static DMData newDMData(){
        return new DMData();
    }
    public static DMData newDMData(Object dataValue){
        return new DMData(dataValue);
    }
    public static DMData newDMData(ArrayList<HashMap<String,Object>> dataList){
        return new DMData(dataList);
    }

    public DMData setDataItem(String itemName, Object itemValue){
        if (dataItem==null) dataItem = new HashMap<>();
        dataItem.put(itemName,itemValue);
        return this;
    }
    public DMData setDataList(ArrayList<HashMap<String,Object>> dataList){
        this.dataList = dataList;
        return this;
    }

    public DMData putDataValueTo(HashMap outData, String itemName){
        outData.put(itemName, dataValue);
        return this;
    }
    public DMData putDataItemTo(HashMap outData){
        outData.putAll(dataItem);
        return this;
    }
    public DMData putDataItemTo(HashMap outData, String itemName){
        outData.put(itemName, dataItem);
        return this;
    }
    public DMData putDataListTo(HashMap outData, String itemName){
        outData.put(itemName, dataList);
        return this;
    }

    public static void putValueTo(HashMap outData, String itemName, Object value){
        outData.put(itemName,value);
    }
}
