package model.core;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dmkits on 04.11.16.
 */
public class DMMetadata extends DMSQLQueryMetadata {

    ArrayList<HashMap<String,Object>> selectResult= null;

    public DMMetadata(String sMainSource, String sField, int iFieldType) {
        super(sMainSource, sField, iFieldType);
    }
    public DMMetadata(DMSQLQueryMetadata dmSQLQueryMetadata, ArrayList<DMField> metadataFieldsList) {
        super(dmSQLQueryMetadata, metadataFieldsList);
    }

    public static DMMetadata newMetadata(String tableName, String fieldName, int iFieldType){
        return new DMMetadata(tableName, fieldName, iFieldType);
    }
    public static DMMetadata getMetadataFrom(DMSQLQueryMetadata dmSQLQueryMetadata){
        return new DMMetadata(dmSQLQueryMetadata,null);
    }
    public DMMetadata clone(){
        return new DMMetadata(super.clone(),null);
    }
    public DMMetadata cloneWithFields(String... sFieldsNames){
        return getMetadataFrom(super.cloneWithFields(sFieldsNames));
    }
    public DMMetadata cloneWithoutFields(){
        return new DMMetadata(this,new ArrayList<DMField>());
    }

    @Override
    public DMMetadata setMainSource(String sMainSource){
        super.setMainSource(sMainSource);
        return this;
    }
    protected DMMetadata addField(String FIELD_PUBLIC_NAME, String FIELD_SOURCE, String FIELD_NAME, int FIELD_TYPE, boolean grouping){
        super.addField(FIELD_PUBLIC_NAME, FIELD_SOURCE, FIELD_NAME, FIELD_TYPE, grouping);
        return this;
    }
    public DMMetadata addField(String FIELD_PUBLIC_NAME, String FIELD_SOURCE, String FIELD_NAME, int FIELD_TYPE){
        return addField(FIELD_PUBLIC_NAME, FIELD_SOURCE, FIELD_NAME, FIELD_TYPE, false);
    }
    public DMMetadata addField(String FIELD_PUBLIC_NAME, String FIELD_SOURCE, String FIELD_NAME){
        return addField(FIELD_PUBLIC_NAME, FIELD_SOURCE, FIELD_NAME, 0);
    }
    public DMMetadata addField(String FIELD_SOURCE, String FIELD_NAME, int FIELD_TYPE){
        return addField(FIELD_NAME, FIELD_SOURCE, FIELD_NAME, FIELD_TYPE);
    }
    public DMMetadata addGroupedField(String FIELD_PUBLIC_NAME, String FIELD_SOURCE, String FIELD_NAME, int FIELD_TYPE){
        super.addGroupedField(FIELD_PUBLIC_NAME, FIELD_SOURCE, FIELD_NAME, FIELD_TYPE);
        return this;
    }
    public DMMetadata addGroupedField(String FIELD_PUBLIC_NAME, String FIELD_SOURCE, String FIELD_NAME){
        super.addGroupedField(FIELD_PUBLIC_NAME, FIELD_SOURCE, FIELD_NAME);
        return this;
    }
    public DMMetadata addGroupField(String groupFieldPublicName) {
        super.addGroupField(groupFieldPublicName);
        return this;
    }
    public DMMetadata addFieldFunction(String FIELD_PUBLIC_NAME, String function, String sourceName, String functionBodyFields){
        super.addFieldFunction(FIELD_PUBLIC_NAME, function, sourceName, functionBodyFields);
        return this;
    }
    public DMMetadata addWhereCondition(String addingConditionFieldName, String addingCondition, Object addingConditionValue){
        super.addWhereCondition(addingConditionFieldName, addingCondition, addingConditionValue);
        return this;
    }
    public DMMetadata addWhereCondition(Condition condition){
        super.addWhereCondition(condition);
        return this;
    }
    public DMMetadata addWhereConditions(HashMap<String,Condition> conditions){
        super.addWhereConditions(conditions);
        return this;
    }
    public DMMetadata addOrder(String... joiningOrders){
        super.addOrder(joiningOrders);
        return this;
    }
    public DMMetadata addColumnData(String fieldPublicName, String columnLabel, String columnType, int columnDefaultWidth, boolean columnReadOnly){
        super.addColumnData(fieldPublicName, columnLabel, columnType, columnDefaultWidth, columnReadOnly);
        return this;
    }
    public DMMetadata addHideColumnData(String fieldPublicName, String columnLabel, String columnType, int columnDefaultWidth, boolean columnReadOnly){
        super.addHideColumnData(fieldPublicName, columnLabel, columnType, columnDefaultWidth, columnReadOnly);
        return this;
    }

    public DMMetadata joinSource(String joinedSourceName, String joinedSourceLinkFieldName,
                                         String mainSourceName, String mainSourceLinkFieldName) {
        super.joinSource(joinedSourceName, joinedSourceLinkFieldName, mainSourceName, mainSourceLinkFieldName);
        return this;
    }
    public DMMetadata joinSourceByLeft(String joinedSourceName, String joinedSourceLinkFieldName,
                                               String mainSourceName, String mainSourceLinkFieldName) {
        super.joinSourceByLeft(joinedSourceName, joinedSourceLinkFieldName, mainSourceName, mainSourceLinkFieldName);
        return this;
    }
    public DMMetadata joinSourceByLeft(String joinName, String joinedSourceName, String joinedSourceLinkFieldName,
                                               String mainSourceName, String mainSourceLinkFieldName, String additionalCondition) {
        super.joinSourceByLeft(joinName, joinedSourceName, joinedSourceLinkFieldName, mainSourceName, mainSourceLinkFieldName, additionalCondition);
        return this;
    }
    public DMMetadata joinMetadataSources(DMSQLQueryMetadata joiningQueryMetadata, String joinedSourceLinkFieldName,
                                                  String mainSourceName, String mainSourceLinkFieldName) {
        super.joinMetadataSources(joiningQueryMetadata, joinedSourceLinkFieldName, mainSourceName, mainSourceLinkFieldName);
        return this;
    }
    public DMMetadata addFieldFromMetadata(String sFieldPublicName, DMSQLQueryMetadata joiningQueryMetadata, String sMetadataFieldPublicName){
        super.addFieldFromMetadata(sFieldPublicName, joiningQueryMetadata, sMetadataFieldPublicName);
        return this;
    }
    public DMMetadata addGroupedFieldFromMetadata(String sFieldPublicName, DMSQLQueryMetadata joiningQueryMetadata, String sMetadataFieldPublicName){
        super.addGroupedFieldFromMetadata(sFieldPublicName, joiningQueryMetadata, sMetadataFieldPublicName);
        return this;
    }
    public <T extends Object> DMMetadata setUpdatableValues(HashMap<String,T> parameters){
        super.setUpdatableValues(parameters);
        return this;
    }
    public DMMetadata setUpdatableFieldValue(String fieldName, Object value){
        super.setUpdatableFieldValue(fieldName, value);
        return this;
    }
    public DMMetadata validateUpdatableValues(HashMap<String,Object> resultParameters) throws DataModelException {
        super.validateUpdatableValues(resultParameters);
        return this;
    }
    public HashMap<String,Object> getResultValues() {
        return UPDATEBLE_VALUES;
    }
    public DMMetadata putResultValuesTo(HashMap<String,Object> parameters) {
        super.putResultValuesTo(parameters);
        return this;
    }

    public DMMetadata selectList(DBUserSession dbus) throws DataModelException {
        try {
            selectResult= dbus.getDataListFromSQLQuery(getSelectSQLQuery(dbus), getConditionsValues());
            return this;
        } catch (SQLException e) {
            throw new DataModelException("Failed do selectList! Reason: "+e.getLocalizedMessage());
        }
    }
    public DMMetadata selectListWithReplace(DBUserSession dbus, HashMap<String,String> replacingDataItemLabels) throws DataModelException {
        try {
            selectResult= dbus.getDataListFromSQLQuery(getSelectSQLQuery(dbus), getConditionsValues(), replacingDataItemLabels);
            return this;
        } catch (SQLException e) {
            throw new DataModelException("Failed do selectList! Reason: "+e.getLocalizedMessage());
        }
    }
    public ArrayList<HashMap<String,Object>> getSelectResultItems() throws DataModelException {
        return selectResult;
    }
    public static Object findValueByFieldValue(ArrayList<HashMap<String,Object>> selectResult,
                                               String sFindValueFieldName, String sConditionFieldName, Object sConditionValue) throws DataModelException {
        if (selectResult==null) return null;
        for (int i = 0; i < selectResult.size(); i++) {
            HashMap<String,Object> selectResultItem = selectResult.get(i);
            if (selectResultItem!=null
                    && selectResultItem.containsKey(sConditionFieldName)
                    && selectResultItem.get(sConditionFieldName).equals(sConditionValue)) return selectResultItem.get(sFindValueFieldName);
        }
        return null;
    }
    public HashMap<String,Object> getSelectResultItemValues() throws DataModelException {
        if (selectResult==null || selectResult.size()==0) return null;
        if (selectResult.size()>1)
            throw new DataModelException("Failed do getSelectResultItemValue! Reason: select result contains more that one items!");
        HashMap<String,Object> selectResultItem = selectResult.get(0);
        return selectResultItem;
    }
    public Object getSelectResultItemValue() throws DataModelException {
        HashMap<String,Object> selectResultItem = getSelectResultItemValues();
        if (selectResultItem==null) return null;
        if (selectResultItem.size()>1)
            throw new DataModelException("Failed do getSelectResultItemValue! Reason: select result item contains more that one values!");
        return selectResultItem.values().toArray()[0];
    }

    public DMMetadata putIDNameTo(HashMap recipient, String itemName) throws DataModelException {
        recipient.put(itemName,getKeyFieldPublicName());
        return this;
    }
    public DMMetadata putResultItemValueTo(HashMap recipient, String itemName) throws DataModelException {
        if (selectResult==null || selectResult.size()==0) {
            recipient.put(itemName,null);
            return this;
        }
        if (selectResult.size()>1)
            throw new DataModelException("Cannot do putResultItemValueTo! Reason: select result contains more that one items!");
        recipient.put(itemName,selectResult.get(0).values().toArray()[0]);
        return this;
    }
    public DMMetadata putResultItemValuesTo(HashMap recipient) throws DataModelException {
        if (selectResult==null) return this;
        if (selectResult.size()>1)
            throw new DataModelException("Cannot do putResultTo! Reason: select result contains more that one items!");
        HashMap<String,Object> selectResultItem = selectResult.get(0);
        for (int i = 0; i < FIELDS_LIST.size(); i++) {
            DMField resultField= FIELDS_LIST.get(i);
            recipient.put(resultField.getPublicName(), selectResultItem.get(resultField.getPublicName()));
        }
        return this;
    }
    public DMMetadata putResultItemValuesIFNULLTo(HashMap recipient) throws DataModelException {
        if (selectResult==null) return this;
        if (selectResult.size()>1)
            throw new DataModelException("Cannot do putResultItemValuesIFNULLTo! Reason: select result contains more that one items!");
        HashMap<String,Object> selectResultItem = selectResult.get(0);
        for (int i = 0; i < FIELDS_LIST.size(); i++) {
            DMField resultField= FIELDS_LIST.get(i);
            if (selectResultItem!=null) {
                Object selectResultFieldValue = selectResultItem.get(resultField.getPublicName());
                if (selectResultFieldValue==null) selectResultFieldValue= 0;
                recipient.put(resultField.getPublicName(), selectResultFieldValue);
            }
        }
        return this;
    }
    public DMMetadata putResultItemTo(HashMap recipient, String itemName) throws DataModelException {
        if (selectResult==null || selectResult.size()==0) {
            recipient.put(itemName,null);
            return this;
        }
        if (selectResult.size()>1)
            throw new DataModelException("Cannot do putResultItemTo! Reason: select result contains more that one items!");
        recipient.put(itemName,selectResult.get(0));
        return this;
    }
    public DMMetadata putResultFirstItemTo(HashMap recipient, String itemName) throws DataModelException {
        if (selectResult==null || selectResult.size()==0) {
            recipient.put(itemName,null);
            return this;
        }
        recipient.put(itemName,selectResult.get(0));
        return this;
    }
    public DMMetadata putResultListTo(HashMap recipient, String itemName) throws DataModelException {
        if (selectResult==null) {
            recipient.put(itemName,new ArrayList<>());
            return this;
        }
        recipient.put(itemName,selectResult);
        return this;
    }
    public DMMetadata putColumnsTo(HashMap recipient, String itemName) throws DataModelException {
        recipient.put(itemName,getColumns());
        return this;
    }

    public int insert(DBUserSession dbus) throws DataModelException {
        int result;
        try {
            result= dbus.execSQLQuery(this.getInsertSQLQueryData());
        } catch (SQLException e) {
            throw new DataModelException("Failed to insert! Reason:"+e.getLocalizedMessage());
        }
        if (result==0) throw new DataModelException("Failed to insert! Reason: insert result no count!");
        return result;
    }
    public int update(DBUserSession dbus) throws DataModelException {
        if (MAIN_CONDITION==null || MAIN_CONDITION.size()==0 || MAIN_CONDITION_LIST==null || MAIN_CONDITION_LIST.size()==0)
            throw new DataModelException("Failed to update! Reason: no conditions for update!");
        int result;
        try {
            result= dbus.execSQLQuery(this.getUpdateSQLQuery());
        } catch (SQLException e) {
            throw new DataModelException("Failed to update! Reason:"+e.getLocalizedMessage());
        }
        if (result==0) throw new DataModelException("Failed to update! Reason: update result no count!");
        return result;
    }
//    public int store(DBUserSession dbus, boolean insert) throws DataModelException {
//        if (insert) return insert(dbus); else return update(dbus);
//    }
    public int delete(DBUserSession dbus) throws DataModelException {
        if (MAIN_CONDITION==null || MAIN_CONDITION.size()==0 || MAIN_CONDITION_LIST==null || MAIN_CONDITION_LIST.size()==0)
            throw new DataModelException("Failed to delete! Reason: no conditions for delete!");
        int result;
        try {
            result= dbus.execSQLQuery(this.getDeleteSQLQuery());
        } catch (SQLException e) {
            throw new DataModelException("Failed to delete! Reason:"+e.getLocalizedMessage());
        }
        if (result==0) throw new DataModelException("Failed to delete! Reason: delete result no count!");
        return result;
    }
}
