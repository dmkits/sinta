package model.core;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by dmkits on 25.04.16.
 */
public abstract class DMTable extends DMBase {

    protected ArrayList<ChangeItem> CHANGES = new ArrayList<>();
    protected DMMetadata METADATA = null;
    public DMMetadata getMetadata(){ return METADATA; }

    public static final String type_INTEGER = "INTEGER";
    public static final String type_SMALLINT = "SMALLINT";
    public static final String type_BIGINT = "BIGINT";
    public static final String type_VARCHAR = "VARCHAR";//(max size)
    public static final String type_DECIMAL = "DECIMAL";//(precision [, scale ])
    public static final String type_FLOAT = "FLOAT";
    //public static final String type_BIT = "BIT";
    public static final String type_DATE = "DATE";
    public static final String type_SDATE = "SDATE";//date to string by pattern yyyy-MM-dd
    public static final String type_TIMESTAMP = "TIMESTAMP";
    //public static final String type_SDATETIME = "SDATETIME";//date to string by pattern yyyy-MM-dd hh:mm:sss
    public static final String type_NOTNULL = "NOT NULL";
    public static final String val_DEFAULT = "DEFAULT";
    public static final String P_KEY = "PKEY";
    public static final String F_KEY = "FKEY";

    public static final String CREATE_TABLE="CREATE TABLE ";
    public static final String DROP_TABLE="DROP TABLE ";
    public static final String ALTER_TABLE="ALTER TABLE ";
    public static final String ADD_COLUMN=" ADD COLUMN ";
    public static final String DROP_COLUMN=" DROP COLUMN ";
    public static final String ALTER_COLUMN=" ALTER COLUMN ";
    public static final String ALTER=" ALTER ";
    public static final String SET_DATATYPE=" SET DATA TYPE ";
    public static final String COLUMN_DEFAULT=" DEFAULT ";
    public static final String COLUMN_SET_DATA_TYPE=" SET DATA TYPE ";
    public static final String ADD_PKEY=" ADD PRIMARY KEY ";
    public static final String DROP_PKEY=" DROP PRIMARY KEY ";
    public static final String ADD_CONSTRAINT=" ADD CONSTRAINT ";
    public static final String DROP_CONSTRAINT=" DROP CONSTRAINT ";
    public static final String CONSTRAINT_UNIQUE=" UNIQUE";
    public static final String CONSTRAINT_PKEY=" PRIMARY KEY";
    public static final String CONSTRAINT_FKEY=" FOREIGN KEY ";
    public static final String CONSTRAINT_FKEY_REF=" REFERENCES ";

    public static final String INSERT_INTO="INSERT INTO ";
    public static final String VALUES=" VALUES ";
    public static final String UPDATE="UPDATE ";
    public static final String SET=" SET ";
    public static final String WHERE=" MAIN_CONDITION ";

    public DMTable(){
        //addChange(...)
        //addINSERT(...)
        //METADATA = DMMetadata.newMetadata(tablename,field_ID);
        //    .addField(tablename, field_NAME);
        //    ...
    }

    @Override
    public void validate(DBUserSession dbus) throws DataModelException {
        try {
            METADATA.clone()
                    .addWhereCondition(Condition.ISNULLCondition(METADATA.getKeyFieldPublicName())).selectList(dbus);
        } catch (Exception e){
            throw new DataModelException("Failed to validate data model object "+getID()+"! Reason:"+e.getLocalizedMessage());
        }
    }

    protected void addChange(String changeID, String changeDateTime, String changeValue) throws DataModelException {
        CHANGES.add(new ChangeItem(changeID, changeDateTime, changeValue));
    }
    protected String addINSERT(String sTableName, String sFieldsList, String... insData) throws DataModelException {
        String values = null;
        try {
            for (int i = 0; i < insData.length; i++) {
                values = (values==null) ?  insData[i] : values + "," + insData[i];
            }
        } catch (Exception e){
            throw new DataModelException("Cannot adding insert data into "+getID()+"! Reason:"+e.getLocalizedMessage());
        }
        return INSERT_INTO+sTableName+"("+sFieldsList+") values("+values+")";
    }
    @Override
    public void getChanges(ArrayList<HashMap<String,Object>> result) throws DataModelException {
        if (CHANGES==null) return;
        HashMap hmExistsChanges= dataArrayToHashmap(result, DMBase.CHANGE_ID);
        for(int i=0;i<CHANGES.size();i++) {
            ChangeItem changesItem = CHANGES.get(i);
            if(!hmExistsChanges.containsKey(changesItem.getID())){
                result.add(changesItem.getChangeData(this.getClass().getName()));
            } else {
                throw new DataModelException("Failed to get changes from data model object "+getID()+"!"
                        +" Reason: in changes already exists change with id="+changesItem.getID()+"!");
            }
        }
    }

    private HashMap<String,HashMap> dataArrayToHashmap(ArrayList<HashMap<String,Object>> data, String sIdentifierName){
        HashMap result= new HashMap();
        for(int j=0;j<data.size();j++){
            HashMap dataItem=data.get(j);
            result.put(dataItem.get(sIdentifierName),dataItem);
        }
        return result;
    }

    private class ChangeItem{
        String ID = null;
        Date DATE = null;
        String VALUE = null;

        public ChangeItem(String ID, String DATE, String VALUE) throws DataModelException {
            this.ID = ID;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                this.DATE = dateFormat.parse(DATE);
            } catch (ParseException e) {
                throw new DataModelException("Cannot create change item! Reason:"+e.getLocalizedMessage());
            }
            this.VALUE = VALUE;
        }
        public String getID(){ return ID; }
        public Date getDate(){ return DATE; }
        public String getValue(){ return VALUE; }
        public HashMap getChangeData(String objectName){
            HashMap result= new HashMap();
            result.put(DMBase.CHANGE_ID,ID);
            result.put(DMBase.CHANGE_DATETIME,DATE);
            result.put(DMBase.CHANGE_VALUE,VALUE);
            result.put(DMBase.CHANGE_OBJECT,objectName);
            return result;
        }
    }

    public static HashMap<String,Object> getMetadataSelectItemByFieldValue(DBUserSession dbus, DMMetadata dmMetadata,
                                                                           String sConditionFieldName, Object oConditionValue) throws DataModelException {
        return dmMetadata.clone().addWhereCondition(sConditionFieldName,"=",oConditionValue).selectList(dbus).getSelectResultItemValues();
    }

    public ArrayList<HashMap<String,Object>> getDataItemsByFieldValue(DBUserSession dbus, String sConditionFieldName, Object oConditionValue) throws DataModelException {
        return METADATA.clone().addWhereCondition(sConditionFieldName,"=",oConditionValue).selectList(dbus).getSelectResultItems();
    }
    public HashMap<String,Object> getDataItemByFieldValue(DBUserSession dbus, String sConditionFieldName, Object oConditionValue) throws DataModelException {
        return METADATA.clone().addWhereCondition(sConditionFieldName,"=",oConditionValue).selectList(dbus).getSelectResultItemValues();
    }

    public Object getDataItemValueByFieldValue(DBUserSession dbus,
                                               String sValueFieldName, String sConditionFieldName, Object oConditionValue) throws DataModelException {
        return METADATA.cloneWithFields(sValueFieldName).addWhereCondition(sConditionFieldName,"=",oConditionValue).selectList(dbus).getSelectResultItemValue();
    }
    public void getDataItemValueByFieldValueAndPutTo(HashMap<String,Object> receipent, String itemName, DBUserSession dbus,
                                                     String sValueFieldName, String sConditionFieldName, Object oConditionValue) throws DataModelException {
        METADATA.cloneWithFields(sValueFieldName).addWhereCondition(sConditionFieldName, "=", oConditionValue).selectList(dbus).putResultItemValueTo(receipent, itemName);
    }
    public void getStrDataItemValueByFieldValueAndPutTo(HashMap<String,String> receipent, String itemName, DBUserSession dbus,
                                                        String sValueFieldName, String sConditionFieldName, Object oConditionValue) throws DataModelException {
        METADATA.cloneWithFields(sValueFieldName).addWhereCondition(sConditionFieldName,"=",oConditionValue).selectList(dbus).putResultItemValueTo(receipent, itemName);
    }

    public static Integer getNewValueFor(DBUserSession dbus, DMMetadata dmMetadata, String sNumberFieldName) throws DataModelException {
        Integer result=
                (Integer)dmMetadata.cloneWithoutFields()
                        .addFieldFunction("MAXVALUE" , "MAX", dmMetadata.MAIN_SOURCE_NAME, sNumberFieldName).selectList(dbus).getSelectResultItemValue();
        if (result==null) return Integer.valueOf(1);
        return Integer.valueOf(result.intValue()+1);
    }
    public Integer getNewValueFor(DBUserSession dbus, String sNumberFieldName) throws DataModelException {
        Integer result=
                (Integer)METADATA.cloneWithoutFields()
                        .addFieldFunction("MAXVALUE" , "MAX", METADATA.MAIN_SOURCE_NAME, sNumberFieldName).selectList(dbus).getSelectResultItemValue();
        if (result==null) return Integer.valueOf(1);
        return Integer.valueOf(result.intValue()+1);
    }


//    public HashMap<String,Object> validateFields(DBUserSession dbus, HashMap<String, String> validateParameters, boolean insert) throws DataModelException {
//        return (HashMap)validateParameters;
//    }
//    public static Object validateField(String fieldName, HashMap<String,String> parameters, String fieldType) throws DataModelException {
//        return validateField(fieldName, parameters.get(fieldName), fieldType);
//    }
//    public static boolean isValidatedField(String fieldName, Object value, String fieldType) throws DataModelException {
//        if (value==null) return false;
//        if (!String.class.getName().equals(value.getClass().getName())) return true;
//        try {
//            validateField(fieldName, (String)value, fieldType);
//        } catch (Exception e){ return false; }
//        return true;
//    }
//    public static boolean isValidatedField(String fieldName, HashMap<String,String> parameters, String fieldType) throws DataModelException {
//        return isValidatedField(fieldName, parameters.get(fieldName), fieldType);
//    }

//    public static void setResultItemErrorForField(String fieldName,String message, HashMap validateParameters){
//        validateParameters.put(DATA_RESULT_ITEM_ERROR_PREFIX+fieldName, message);
//    }
//    public static boolean validateField(String fieldName, String fieldType, String message, HashMap validateParameters) {
//        Object value = validateParameters.get(fieldName);
//        if (value!=null&&!String.class.getName().equals(value.getClass().getName())) return true;
//        try {
//            validateParameters.put(fieldName, validateField(fieldName, (String)value, fieldType));
//        } catch (Exception e){
//            setResultItemErrorForField(fieldName, message, validateParameters);
//            return false;
//        }
//        return true;
//    }

    public boolean isParameterItemNULL(HashMap<String, String> parameters, String sParamItemName){
        return DMMetadata.valIsNull(parameters.get(sParamItemName));
    }
    public void setParameterItem(HashMap<String,Object> parameters, String sParamItemName, Object sParamItemValue){
        parameters.put(sParamItemName, sParamItemValue);
    }
    public void setStrParameterItem(HashMap<String,String> parameters, String sParamItemName, String sParamItemValue){
        parameters.put(sParamItemName, sParamItemValue);
    }
    public void setParameterItemFrom(HashMap<String, String> parameters, String sParamItemName, String sParamValueItemName){
        parameters.put(sParamItemName, parameters.get(sParamValueItemName));
    }

    synchronized public int insert(DBUserSession dbus, Object keyValue, HashMap<String,Object> insFieldsParameters) throws DataModelException {
        try {
            DMMetadata insertMetadata = METADATA.clone();
            return insertMetadata.setUpdatableValues(insFieldsParameters)
                    .setUpdatableFieldValue(insertMetadata.getKeyFieldPublicName(), keyValue)
                    .insert(dbus);
        } catch (Exception e) {
            throw new DataModelException("Cannot do insert in data model object:"+getID()+"! Reason:"+e.getLocalizedMessage());
        }
    }
    synchronized public int add(DBUserSession dbus, HashMap<String, Object> addFieldsParameters) throws DataModelException {
        try {
            DMMetadata storeMetadata = METADATA.clone();
            String keyValue= getShortUUIDString();
            int result= storeMetadata.setUpdatableValues(addFieldsParameters)
                    .setUpdatableFieldValue(storeMetadata.getKeyFieldPublicName(), keyValue)
                    .insert(dbus);
            if (result>0) addFieldsParameters.put(storeMetadata.getKeyFieldPublicName(), keyValue);
            return result;
        } catch (Exception e) {
            throw new DataModelException("Cannot do add in data model object:"+getID()+"! Reason:"+e.getLocalizedMessage());
        }
    }
    synchronized public int update(DBUserSession dbus, Object keyValue, HashMap<String,Object> updFieldsParameters) throws DataModelException {
        try {
            DMMetadata updateMetadata = METADATA.clone();
            String sKeyFieldPublicName = updateMetadata.getKeyFieldPublicName();
            HashMap<String,Object> updatableParameters = new HashMap<>(updFieldsParameters);
            if (updatableParameters.containsKey(sKeyFieldPublicName)) updatableParameters.remove(sKeyFieldPublicName);
            return updateMetadata.setUpdatableValues(updatableParameters)
                    .addWhereCondition(sKeyFieldPublicName,"=",keyValue)
                    .update(dbus);
        } catch (Exception e) {
            throw new DataModelException("Cannot do update in data model object:"+getID()+"! Reason:"+e.getLocalizedMessage());
        }
    }

    synchronized public static int storeMetadata(DMMetadata dmMetadata,
                                    DBUserSession dbus, HashMap<String,Object> storeFieldsParameters) throws DataModelException {
        int updateCount = 0;
        String sIDFieldName= dmMetadata.getKeyFieldPublicName();
        Object idFieldValue= storeFieldsParameters.get(sIDFieldName);
        DMMetadata storeMetadata = dmMetadata.clone();
        HashMap<String,Object> resultParameters = new HashMap<>();
        boolean isInsert = storeFieldsParameters.get(sIDFieldName)==null;
        if (isInsert) {
            try {
                String keyValue = getShortUUIDString();
                updateCount = storeMetadata
                        .setUpdatableValues(storeFieldsParameters)
                        .setUpdatableFieldValue(sIDFieldName, keyValue)
                        .insert(dbus);
            } catch (Exception e){
                resultParameters.remove(sIDFieldName);
                throw new DataModelException(e.getLocalizedMessage());
            }
            storeMetadata.putResultValuesTo(storeFieldsParameters);
            return updateCount;
        }
        try {//update
            HashMap<String,Object> updateFieldsParameters = new HashMap<>(storeFieldsParameters);
            if (updateFieldsParameters.containsKey(sIDFieldName)) updateFieldsParameters.remove(sIDFieldName);
            resultParameters.put(sIDFieldName, idFieldValue);
            updateCount = storeMetadata
                    .setUpdatableValues(updateFieldsParameters)
                    .addWhereCondition(sIDFieldName, "=", idFieldValue)
                    .update(dbus);
        } catch (Exception e){
            throw new DataModelException(e.getLocalizedMessage());
        }
        storeMetadata.putResultValuesTo(storeFieldsParameters);
        return updateCount;
    }

    public static HashMap<String,Object> validateMetadataForStoreAndPutResultTo(HashMap outData, DMMetadata dmMetadata,
                                                              HashMap<String,String> storeFieldsParameters) throws DataModelException {
        String sIDFieldName= dmMetadata.getKeyFieldPublicName();
        DMMetadata validateMetadata = dmMetadata.clone();
        HashMap<String,Object> resultParameters = new HashMap<>();
        boolean isInsert = storeFieldsParameters.get(sIDFieldName)==null;
        if (isInsert) {
            try {
                String keyValue = getShortUUIDString();
                validateMetadata
                        .setUpdatableValues(storeFieldsParameters)
                        .setUpdatableFieldValue(sIDFieldName, keyValue)
                        .validateUpdatableValues(resultParameters);
            } catch (Exception e){
                resultParameters.remove(sIDFieldName);
                throw new DataModelException(e.getLocalizedMessage());
            } finally {
                outData.put(DATA_RESULT_ITEM, resultParameters);
            }
            return validateMetadata.getResultValues();
        }
        try {//update
            validateMetadata
                    .setUpdatableValues(storeFieldsParameters)
                    .validateUpdatableValues(resultParameters);
        } catch (Exception e){
            throw new DataModelException(e.getLocalizedMessage());
        } finally {
            outData.put(DATA_RESULT_ITEM, resultParameters);
        }
        return validateMetadata.getResultValues();
    }
    synchronized public static int storeMetadataAndPutResultTo(HashMap outData, DMMetadata dmMetadata,
                                                   HashMap<String,Object> storeFieldsParameters, boolean insert, String sIDFieldName,
                                                   DBUserSession dbus) throws DataModelException {
        int updateCount = 0;
        HashMap<String,Object> resultParameters = new HashMap<>();
        DMMetadata storeMetadata = dmMetadata.clone();
        if (insert) {
            try {
                updateCount = storeMetadata.setUpdatableValues(storeFieldsParameters).insert(dbus);
                storeMetadata.putResultValuesTo(resultParameters);
            } catch (Exception e){
                resultParameters.put(sIDFieldName, null);
                throw new DataModelException(e.getLocalizedMessage());
            } finally {
                HashMap<String,Object> outDataResultParameters = (HashMap)outData.get(DATA_RESULT_ITEM);
                if (outDataResultParameters==null)
                    outData.put(DATA_RESULT_ITEM, resultParameters);
                else
                    outDataResultParameters.putAll(resultParameters);
                outData.put(DATA_UPDATE_COUNT, updateCount);
            }
            return updateCount;
        }
        try {
            String sIDFieldValue = (String)storeFieldsParameters.get(sIDFieldName);
            HashMap<String,Object> updateFieldsParameters = new HashMap<>(storeFieldsParameters);
            if (updateFieldsParameters.containsKey(sIDFieldName)) updateFieldsParameters.remove(sIDFieldName);
            updateCount = storeMetadata.setUpdatableValues(updateFieldsParameters)
                    .addWhereCondition(sIDFieldName,"=",sIDFieldValue)
                    .update(dbus);
            storeMetadata.putResultValuesTo(resultParameters);
            resultParameters.put(sIDFieldName, sIDFieldValue);
        } catch (Exception e){
            throw new DataModelException(e.getLocalizedMessage());
        } finally {
            HashMap<String,Object> outDataResultParameters = (HashMap)outData.get(DATA_RESULT_ITEM);
            if (outDataResultParameters==null)
                outData.put(DATA_RESULT_ITEM, resultParameters);
            else
                outDataResultParameters.putAll(resultParameters);
            outData.put(DATA_UPDATE_COUNT, updateCount);
        }
        return updateCount;
    }
    synchronized public void storeAndPutResultTo(HashMap outData,
                                    HashMap<String,Object> storeFieldsParameters, boolean insert, String sIDFieldName,
                                    DBUserSession dbus) throws DataModelException {
        storeMetadataAndPutResultTo(outData, METADATA, storeFieldsParameters, insert, sIDFieldName, dbus);
    }
    synchronized public void validateStoreMetadataAndPutResultTo(HashMap outData, DMMetadata dmMetadata,
                                                                 HashMap<String, String> storeFieldsParameters, DBUserSession dbus) throws DataModelException {
        String sIDFieldName = METADATA.getKeyFieldName();
        boolean isInsert = isParameterItemNULL(storeFieldsParameters, sIDFieldName);
        DMMetadata storeMetadata = dmMetadata.clone();
        HashMap<String,Object> storingParameters =
                validateMetadataForStoreAndPutResultTo(outData, storeMetadata, storeFieldsParameters);
        try {
            storeMetadataAndPutResultTo(outData, storeMetadata, storingParameters, isInsert, sIDFieldName, dbus);
            METADATA.clone()
                    .addWhereCondition(sIDFieldName, "=", storingParameters.get(sIDFieldName))
                    .selectList(dbus).putResultItemTo(outData, DATA_RESULT_ITEM);
        } catch (Exception e){
            outData.put(DATA_UPDATE_COUNT, 0);
            throw new DataModelException("Failed to validate and store metadata in data model object"+getID()+"! Reason:"+e.getLocalizedMessage());
        }
    }
    public void validateStoreAndPutResultTo(HashMap outData,
                                            HashMap<String, String> storeFieldsParameters, DBUserSession dbus) throws DataModelException {
        validateStoreMetadataAndPutResultTo(outData, METADATA, storeFieldsParameters, dbus);
    }
    synchronized public int deleteMetadataAndPutResultTo(HashMap outData, DMMetadata dmMetadata,
                                                  HashMap<String,String> deleteFieldsParameters, String sIDFieldName, DBUserSession dbus) throws DataModelException {
        int updateCount = 0;
        HashMap<String,Object> resultParameters = new HashMap<>();
        DMMetadata deleteMetadata = dmMetadata.clone();
        try {
            String sIDFieldValue = deleteFieldsParameters.get(sIDFieldName);
            updateCount = deleteMetadata
                    .addWhereCondition(sIDFieldName,"=",sIDFieldValue)
                    .delete(dbus);
            resultParameters.put(sIDFieldName, sIDFieldValue);
        } catch (Exception e){
            throw new DataModelException(e.getLocalizedMessage());
        } finally {
            HashMap<String,Object> outDataResultParameters = (HashMap)outData.get(DATA_RESULT_ITEM);
            if (outDataResultParameters==null)
                outData.put(DATA_RESULT_ITEM, resultParameters);
            else
                outDataResultParameters.putAll(resultParameters);
            outData.put(DATA_UPDATE_COUNT, updateCount);
        }
        return updateCount;
    }
    synchronized public void deleteAndPutResultTo(HashMap outData,
                                                  HashMap<String,String> deleteFieldsParameters, DBUserSession dbus) throws DataModelException {
        deleteMetadataAndPutResultTo(outData, METADATA, deleteFieldsParameters, METADATA.getKeyFieldName(), dbus);
    }
//    @Override
//    public void actionDelete(HashMap outData, DBUserSession dbus, Object keyFieldIDValue) {
//        int deleteCount = 0;
//        try {
//            deleteCount = delete(dbus, addCondition(getKeyField().getInternalFullName(), "=", keyFieldIDValue));
//        } catch (DataModelException e) {
//            outData.put(DATA_ERROR, "Cannot do actionDelete in data model object "+getID()+"! Reason:"+e.getLocalizedMessage()+".");
//        } finally {
//            //outData.put(DATA_RESULT_ITEM, storeParameters);
//            outData.put(DATA_UPDATE_COUNT,deleteCount);
//        }
        //throw new DataModelException("Function not support in data model object:"+getID()+"!");
//    }
    synchronized public int delete(DBUserSession dbus, Object keyValue) throws DataModelException {
        try {
            DMSQLQueryMetadata deleteMetadata = METADATA.clone();
            String sKeyFieldPublicName = deleteMetadata.getKeyFieldPublicName();
            deleteMetadata.addWhereCondition(sKeyFieldPublicName, "=", keyValue);
            return dbus.execSQLQuery(deleteMetadata.getDeleteSQLQuery());
        } catch (Exception e) {
            throw new DataModelException("Cannot do delete in data model object:"+getID()+"! Reason:"+e.getLocalizedMessage());
        }
    }

//    public boolean setJoinedSourceParameters(String joinedSourceName, DMSelection joinedSourceDMObject, DBUserSession dbus,
//                                             HashMap<String,Object> validateParameters) throws DataModelException {
//        ArrayList<HashMap<String,Object>> joinedSourceData =
//                getJoinedSourceDataByParameters(joinedSourceName, joinedSourceDMObject, dbus, validateParameters, null);
//        if (joinedSourceData==null || joinedSourceData.size()==0) return false;
//        HashMap<String,Object> findedJoinedSourceData= joinedSourceData.get(0);
//        for (int i = 0; i < METADATA.getFieldsList().size(); i++) {
//            DMField field = METADATA.getFieldsList().get(i);
//            if (joinedSourceName.equals(field.getSourceName())){
//                String sJoinedSourceFieldPublicName = field.getPublicName();
//                if (valIsNull(validateParameters.get(sJoinedSourceFieldPublicName))){
//                    String sJoinedSourceField = field.getInternalName();
//                    validateParameters.put(sJoinedSourceFieldPublicName, findedJoinedSourceData.get(sJoinedSourceField));
//                }
//            } else {
//                String sJoinedSourceField = field.getInternalName();
//                DMField joinedSourceField = joinedSourceDMObject.METADATA.getJoinedSourceFieldByIntFieldName(field.getSourceName(), sJoinedSourceField);
//                if (joinedSourceField!=null){
//                    String sJoinedSourceFieldPublicName = field.getPublicName();
//                    if (valIsNull(validateParameters.get(sJoinedSourceFieldPublicName))){
//                        validateParameters.put(sJoinedSourceFieldPublicName, findedJoinedSourceData.get(joinedSourceField.getPublicName()));
//                    }
//                }
//            }
//        }
//        return true;
//    }

//    public boolean setJoinedSourceParameters(DMSelection joinedSourceDMObject, DBUserSession dbus,
//                                             HashMap<String,Object> validateParameters) throws DataModelException {
//        ArrayList<HashMap<String,Object>> joinedSourceData =
//                getJoinedSourceDataByParameters(joinedSourceName, joinedSourceDMObject, dbus, validateParameters, null);
//        if (joinedSourceData==null || joinedSourceData.size()==0) return false;
//        HashMap<String,Object> findedJoinedSourceData= joinedSourceData.get(0);
//        for (int i = 0; i < METADATA.getFieldsList().size(); i++) {
//            DMField field = METADATA.getFieldsList().get(i);
//            if (joinedSourceName.equals(field.getSourceName())){
//                String sJoinedSourceFieldPublicName = field.getPublicName();
//                if (valIsNull(validateParameters.get(sJoinedSourceFieldPublicName))){
//                    String sJoinedSourceField = field.getInternalName();
//                    validateParameters.put(sJoinedSourceFieldPublicName, findedJoinedSourceData.get(sJoinedSourceField));
//                }
//            } else {
//                String sJoinedSourceField = field.getInternalName();
//                DMField joinedSourceField = joinedSourceDMObject.METADATA.getJoinedSourceFieldByIntFieldName(field.getSourceName(), sJoinedSourceField);
//                if (joinedSourceField!=null){
//                    String sJoinedSourceFieldPublicName = field.getPublicName();
//                    if (valIsNull(validateParameters.get(sJoinedSourceFieldPublicName))){
//                        validateParameters.put(sJoinedSourceFieldPublicName, findedJoinedSourceData.get(joinedSourceField.getPublicName()));
//                    }
//                }
//            }
//        }
//        return true;
//    }

//    private ArrayList<HashMap<String,Object>> getJoinedSourceDataByParameters(DMSelection joinedSourceDMObject, String[] parametersNames, DBUserSession dbus,
//                                                                              HashMap<String,Object> parameters, String sOrder) throws DataModelException {
//        DMSQLQueryMetadata metadata = getSQLQueryMetadata();
//        HashMap<String,Condition> condition = new HashMap<>();
//        for (int i = 0; i < parametersNames.length; i++) {
//            String sPublicFieldName= parametersNames[i];
//            DMField field = metadata.getFields().get(sPublicFieldName);
//            if (field!=null){
//                if ( joinedSourceDMObject.METADATA.getMainSource().equals(field.getSourceName())
//                        && joinedSourceDMObject.METADATA.getFields().get(field.getInternalName())!=null ){
//
//                }
//            }
//
//        }
//
//
//
//        Iterator<String> iterator = parameters.keySet().iterator();
//        while (iterator.hasNext()){
//            String sPublicFieldName = iterator.next();
//            String sJoinedSourceFieldIntName = metadata.getJoinedSourceFieldIntName(joinedSourceName, sPublicFieldName);
//            Object fieldValue = parameters.get(sPublicFieldName);
//            if(sJoinedSourceFieldIntName!=null && !valIsNull(fieldValue))
//                condition.put(sJoinedSourceFieldIntName+"=",
//                        new Condition(sJoinedSourceFieldIntName,"=", fieldValue));
//        }
//        return joinedSourceDMObject.getDataList(dbus, condition, sOrder);
//    }

//    private ArrayList<HashMap<String,Object>> getJoinedSourceDataByParameters(String joinedSourceName, DMSelection joinedSourceDMObject, DBUserSession dbus,
//                                                                   HashMap<String,Object> parameters, String sOrder) throws DataModelException {
//        DMSQLQueryMetadata metadata = getSQLQueryMetadata();
//        HashMap<String,Condition> condition = new HashMap<>();
//        Iterator<String> iterator = parameters.keySet().iterator();
//        while (iterator.hasNext()){
//            String sPublicFieldName = iterator.next();
//            String sJoinedSourceFieldIntName = metadata.getJoinedSourceFieldIntName(joinedSourceName, sPublicFieldName);
//            Object fieldValue = parameters.get(sPublicFieldName);
//            if(sJoinedSourceFieldIntName!=null && !valIsNull(fieldValue))
//                condition.put(sJoinedSourceFieldIntName+"=",
//                        new Condition(sJoinedSourceFieldIntName,"=", fieldValue));
//        }
//        return joinedSourceDMObject.getDataList(dbus, condition, sOrder);
//    }


//    public ArrayList<HashMap<String,Object>> getIDFromJoinedSource(String joinedSourceName, DMSelection joinedSourceDMObject, DBUserSession dbus,
//                                        HashMap<String,String> parameters, String sOrder) throws DataModelException {
//        DMSQLQueryMetadata metadata = getSQLQueryMetadata();
//        HashMap<String,Condition> condition = new HashMap<>();
//        Iterator<String> iterator = parameters.keySet().iterator();
//        while (iterator.hasNext()){
//            String sPublicFieldName = iterator.next();
//            String sJoinedSourceFieldIntName = metadata.getJoinedSourceFieldIntName(joinedSourceName, sPublicFieldName);
//            if(sJoinedSourceFieldIntName!=null)
//                condition.put(sJoinedSourceFieldIntName+"=",
//                        new Condition(sJoinedSourceFieldIntName,"=", parameters.get(sPublicFieldName)));
//        }
//        return joinedSourceDMObject.getDataList(dbus, condition, sOrder);
//    }

//    public Object getIDFromJoinedSourceByFieldValue(String joinedSourceName, DMSelection joinedSourceDMObject, DBUserSession dbus,
//                                                    String fieldName, Object fieldValue) throws DataModelException {
//        DMSQLQueryMetadata metadata = getSQLQueryMetadata();
//        String getJoinedSourceFieldIntName = metadata.getJoinedSourceFieldIntName(joinedSourceName, fieldName);
//        return joinedSourceDMObject.getFieldValueByFieldValue(dbus, joinedSourceDMObject.METADATA.getKeyFieldName(), getJoinedSourceFieldIntName, fieldValue);
//    }

    public static String[] list(String... listItem){
        return listItem;
    }
//    public Object getValueFromJoinedSourceByID(String joinedSourceName, String dataField, DMSelection joinedSourceDMObject, DBUserSession dbus,
//                                               Object idFieldValue) throws DataModelException {
//        DMSQLQueryMetadata metadata = getSQLQueryMetadata();
//        String getJoinedSourceFieldIntName = metadata.getJoinedSourceFieldIntName(joinedSourceName, dataField);
//        return joinedSourceDMObject.getFieldValueByFieldValue(dbus, getJoinedSourceFieldIntName, joinedSourceDMObject.METADATA.getKeyFieldName(), idFieldValue);
//    }
//    public Object getValueFromJoinedSource(String joinedSourceName, String dataField, DMSelection joinedSourceDMObject, DBUserSession dbus,
//                                                          String fieldName, Object fieldValue) throws DataModelException {
//        DMSQLQueryMetadata metadata = getSQLQueryMetadata();
//        String getJoinedSourceFieldIntName = metadata.getJoinedSourceFieldIntName(joinedSourceName, fieldName);
//        return joinedSourceDMObject.getFieldValueByFieldValue(dbus, dataField, getJoinedSourceFieldIntName, fieldValue);
//    }
//    public HashMap<String,Object> getDataFromJoinedSourceByFieldValue(String joinedSourceName, String[] dataFields, DMSelection joinedSourceDMObject, DBUserSession dbus,
//                                                                      String fieldName, Object fieldValue) throws DataModelException {
//        DMSQLQueryMetadata metadata = getSQLQueryMetadata();
//        String getJoinedSourceFieldIntName = metadata.getJoinedSourceFieldIntName(joinedSourceName, fieldName);
//        return joinedSourceDMObject.getFieldsDataByFieldValue(dbus, dataFields, getJoinedSourceFieldIntName, fieldValue);
//    }
}
