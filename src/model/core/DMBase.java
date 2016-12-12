package model.core;

import model.DataModel;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by dmkits on 03.06.16.
 */
public abstract class DMBase {

    public static HashMap<String,DMBase> DMObjects= new HashMap<>();
    public static ArrayList<DMBase> DMObjectsList = new ArrayList<>();

    protected String getID(){ return this.getClass().getName(); }

    protected static <DMClass extends DMBase> void initDMInstance(DMClass dmInstance) throws DataModelException {
        DMObjects.put(dmInstance.getClass().getName(), dmInstance);
        DMObjectsList.add(dmInstance);
    }
    protected static <DMClass extends DMBase>DMClass getDMInstance(Class<DMClass> dmClass) throws DataModelException {
        String sDMObjectID = dmClass.getName();
        DMClass dmObject = (DMClass) DMObjects.get(sDMObjectID);
        if (dmObject == null) {
            throw new DataModelException("Failed to get data model object " + sDMObjectID + "! Reason: object not inited!");
        } else
            return (DMClass) DMObjects.get(sDMObjectID);
    }

    public abstract void validate(DBUserSession dbus) throws DataModelException;

    public static String CHANGE_ID= "id";
    public static String CHANGE_VALUE= "value";
    public static String CHANGE_DATETIME= "datetime";
    public static String CHANGE_OBJECT= "object";
    public abstract void getChanges(ArrayList<HashMap<String,Object>> result) throws DataModelException;

    public static final String DATA_UPDATE_COUNT = "updateCount";
    public static final String DATA_RESULT_ITEM = "resultItem";

    public static final HashMap<String,Object> addParameter(String sParameterKey, Object parameterValue, HashMap<String,Object> existsParameters){
        if(existsParameters==null)existsParameters= new HashMap<>();
        existsParameters.put(sParameterKey,parameterValue);
        return existsParameters;
    }
    public static final HashMap<String,Object> addParameter(String sParameterKey, Object parameterValue){
        return addParameter(sParameterKey, parameterValue, null);
    }

    public static final HashMap<String,String> addStrParameter(String sParameterKey, String parameterValue, HashMap<String, String> existsParameters){
        if(existsParameters==null)existsParameters= new HashMap<>();
        existsParameters.put(sParameterKey,parameterValue);
        return existsParameters;
    }
    public static final HashMap<String,String> addStrParameter(String sParameterKey, String parameterValue){
        return addStrParameter(sParameterKey, parameterValue, null);
    }

    protected static final HashMap<String,Condition> addCondition(String sConditionField, String sCondition, Object sConditionValue, HashMap<String,Condition> pExistsCondition){
        HashMap<String,Condition> result = new HashMap<>();
        if(pExistsCondition!=null) result.putAll(pExistsCondition);
        result.put(sConditionField+sCondition, new Condition(sConditionField, sCondition, sConditionValue));
        return result;
    }
    protected static final HashMap<String,Condition> addCondition(String sConditionField, String sCondition, Object sConditionValue){
        return addCondition(sConditionField, sCondition, sConditionValue, null);
    }
    protected static final HashMap<String,Condition> addCondition(String sCondition, HashMap<String,Condition> pExistsCondition){
        HashMap<String,Condition> result = new HashMap<>();
        if(pExistsCondition!=null) result.putAll(pExistsCondition);
        result.put(sCondition, new Condition(null, sCondition, null));
        return result;
    }

    /**
     * Generate short UUID (13 characters)
     *
     * @return short UUID
     */
    protected static final long getShortUUID() {
        UUID uuid = UUID.randomUUID();
        return  ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
    }
    protected static final String getShortUUIDString() {
        return Long.toString(getShortUUID());
    }
    protected static final String getStringFromShortUUID(long SUUID) {
        return Long.toString(SUUID, Character.MAX_RADIX);
    }

    protected static final HashMap<String,HashMap> getDataMapFromDataList(ArrayList<HashMap> dataList, String sIdentifierName) throws DataModelException {
        HashMap<String,HashMap> result = new HashMap<>();
        for (int i = 0; i < dataList.size(); i++) {
            HashMap dataItem = dataList.get(i);
            String sIdentifierValue= (String)dataItem.get(sIdentifierName);
            if (result.containsKey(sIdentifierValue)) throw new DataModelException("Cannot convert dataList to dataMap! Reason: key data value dublicated!");
            result.put(sIdentifierValue, dataItem);
        }
        return result;
    }
}
