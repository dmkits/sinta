package model.core;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import static model.core.DMField.*;
/**
 * Created by dmkits on 07.06.16.
 */
public class DMSQLQueryMetadata {

    protected HashMap<String,DMField>  FIELDS = new HashMap<>();
    protected ArrayList<DMField> FIELDS_LIST = new ArrayList<>();
    protected String MAIN_SOURCE_NAME = null;
    protected String MAIN_SOURCE = null;
    protected HashMap<String,Condition> MAIN_CONDITION = null;
    protected ArrayList<Condition> MAIN_CONDITION_LIST = null;
    protected ArrayList<DMField> GROUP_LIST = null;
    protected ArrayList<Condition> HAVING_LIST = null;
    protected String ORDER = null;
    protected ArrayList<String[]> JOINS = null;
    protected HashMap<String,Object> UPDATEBLE_VALUES = null;
    protected ArrayList<HashMap<String,Object>> COLUMNS = null;

    public DMSQLQueryMetadata(String sMainSourceName, String sField, int iFieldType) {
        MAIN_SOURCE_NAME = sMainSourceName;
        if(sField==null) return;
        addField(sField, sMainSourceName, sField, iFieldType, false);
    }
//    public DMSQLQueryMetadata(String mainSource, ArrayList<DMField> fieldsList) {
//        addFieldsList(fieldsList,null);
//        this.MAIN_SOURCE_NAME = mainSource;
//    }
//    public DMSQLQueryMetadata(String mainSource, ArrayList<DMField> fieldsList, String order) {
//        addFieldsList(fieldsList,null);
//        this.MAIN_SOURCE_NAME = mainSource;
//        ORDER = order;
//    }
//    private DMSQLQueryMetadata(String mainSource, ArrayList<DMField> fieldsList, Condition whereCondition) {
//        addFieldsList(fieldsList,null);
//        this.MAIN_SOURCE_NAME = mainSource;
//        if(whereCondition!=null){
//            addWhereCondition(whereCondition);
//        }
//    }
//    public DMSQLQueryMetadata(String sAddingWhereCondition) { addWhereCondition(sAddingWhereCondition); }
//    public DMSQLQueryMetadata(HashMap<String,Condition> condition) { addWhereConditions(condition); }

    public DMSQLQueryMetadata(DMSQLQueryMetadata queryMetadata, ArrayList<DMField> metadataFieldsList){
        MAIN_SOURCE_NAME = queryMetadata.MAIN_SOURCE_NAME;
        MAIN_SOURCE = queryMetadata.MAIN_SOURCE;
        if (metadataFieldsList==null) {
            FIELDS_LIST.addAll(queryMetadata.getFieldsList());
            FIELDS.putAll(queryMetadata.getFields());
        } else if (metadataFieldsList.size()>0){
            for (int i = 0; i < metadataFieldsList.size(); i++) {
                DMField addingField = new DMField(metadataFieldsList.get(i));
                FIELDS_LIST.add(addingField);
                FIELDS.put(addingField.getPublicName(),addingField);
            }
        }
        if (queryMetadata.MAIN_CONDITION_LIST!=null){
            MAIN_CONDITION_LIST = new ArrayList<>();
            MAIN_CONDITION = new HashMap<>();
            for (int i = 0; i < queryMetadata.MAIN_CONDITION_LIST.size(); i++) {
                addWhereCondition(new Condition(queryMetadata.MAIN_CONDITION_LIST.get(i)));
            }
        }
        if (queryMetadata.GROUP_LIST!=null){
            GROUP_LIST = new ArrayList<>();
            for (int i = 0; i < queryMetadata.GROUP_LIST.size(); i++) {
                DMField groupedField = queryMetadata.GROUP_LIST.get(i);
                String sGroupedFieldNamePublicName =  groupedField.getPublicName();
                if (FIELDS.get(sGroupedFieldNamePublicName)!=null)
                    GROUP_LIST.add( new DMField(queryMetadata.GROUP_LIST.get(i)));
            }
        }
        if (queryMetadata.HAVING_LIST!=null){
            HAVING_LIST = new ArrayList<>();
            for (int i = 0; i < queryMetadata.HAVING_LIST.size(); i++) {
                HAVING_LIST.add(new Condition(queryMetadata.HAVING_LIST.get(i)));
            }
        }
        ORDER = queryMetadata.ORDER;
        if (queryMetadata.JOINS!=null){
            JOINS = new ArrayList<>();
            for (int i = 0; i < queryMetadata.JOINS.size(); i++) {
                JOINS.add(queryMetadata.JOINS.get(i).clone());
            }
        }
    }

    public DMSQLQueryMetadata clone(){
        return new DMSQLQueryMetadata(this,null);
    }
    public DMSQLQueryMetadata cloneWithFields(String... sFieldsPublicNames){
        ArrayList<DMField> metadataFieldsList = new ArrayList<>();
        for (int i = 0; i < sFieldsPublicNames.length; i++) {
            String sFieldsPublicName = sFieldsPublicNames[i];
            DMField field = FIELDS.get(sFieldsPublicName);
            if (field!=null) metadataFieldsList.add(field);
        }
        return new DMSQLQueryMetadata(this,metadataFieldsList);
    }
    public DMSQLQueryMetadata cloneWithoutFields(){
        return new DMSQLQueryMetadata(this,new ArrayList<DMField>());
    }

//    public DMSQLQueryMetadata getMetadataWithOrder(String addingOrder){
//        return clone().addOrder(addingOrder);
//    }
//    public DMSQLQueryMetadata getMetadataWithOrders(String... addingOrders){
//        String addingOrder = null;
//        for (int i = 0; i < addingOrders.length; i++) {
//            addingOrder = (addingOrder==null) ? addingOrders[i] : addingOrder+","+addingOrders[i];
//        }
//        return clone().addOrder(addingOrder);
//    }
//    public DMSQLQueryMetadata getMetadataWithCondition(Condition addingCondition){
//        return clone().addWhereCondition(addingCondition);
//    }
//    public DMSQLQueryMetadata getMetadataWithCondition(String addingConditionFieldName, String addingCondition, Object addingConditionValue){
//        return clone().addWhereCondition(addingConditionFieldName, addingCondition, addingConditionValue);
//    }

    public DMSQLQueryMetadata setMainSource(String sMainSource){
        MAIN_SOURCE = sMainSource;
        return this;
    }
    public String getMainSourceName(){
        return MAIN_SOURCE_NAME;
    }

    protected void addField(DMField newField, boolean grouping){
        FIELDS_LIST.add(newField); FIELDS.put(newField.getPublicName(), newField);
        if(grouping){
            if(GROUP_LIST==null) GROUP_LIST = new ArrayList<>();
            GROUP_LIST.add(newField);
        }
    }
    protected DMSQLQueryMetadata addField(String FIELD_PUBLIC_NAME, String FIELD_SOURCE, String FIELD_NAME, int FIELD_TYPE, boolean grouping){
        DMField newField = new DMField(FIELD_PUBLIC_NAME,FIELD_SOURCE, FIELD_NAME, FIELD_TYPE);
        addField(newField, grouping);
        return this;
    }
    public DMSQLQueryMetadata addField(String FIELD_PUBLIC_NAME, String FIELD_SOURCE, String FIELD_NAME, int FIELD_TYPE){
        return addField(FIELD_PUBLIC_NAME, FIELD_SOURCE, FIELD_NAME, FIELD_TYPE, false);
    }
    public DMSQLQueryMetadata addField(String FIELD_PUBLIC_NAME, String FIELD_SOURCE, String FIELD_NAME){
        return addField(FIELD_PUBLIC_NAME, FIELD_SOURCE, FIELD_NAME, 0, false);
    }
    public DMSQLQueryMetadata addField(String FIELD_SOURCE, String FIELD_NAME, int FIELD_TYPE){
        return addField(FIELD_NAME, FIELD_SOURCE, FIELD_NAME, FIELD_TYPE);
    }
    public DMSQLQueryMetadata addGroupedField(String FIELD_PUBLIC_NAME, String FIELD_SOURCE, String FIELD_NAME, int FIELD_TYPE){
        return addField(FIELD_PUBLIC_NAME, FIELD_SOURCE, FIELD_NAME, FIELD_TYPE, true);
    }
    public DMSQLQueryMetadata addGroupedField(String FIELD_PUBLIC_NAME, String FIELD_SOURCE, String FIELD_NAME){
        return addField(FIELD_PUBLIC_NAME, FIELD_SOURCE, FIELD_NAME, ftype_STRING, true);
    }
//    public DMSQLQueryMetadata addGroupedField(String FIELD_PUBLIC_NAME, String FIELD_SOURCE, String FIELD_NAME,
//                                       String columnLabel, String columnType, int columnDefaultWidth, boolean columnReadOnly){
//        DMField newField = new DMField(FIELD_PUBLIC_NAME,FIELD_SOURCE, FIELD_NAME);
//        this.FIELDS_LIST.add(newField);
//        this.FIELDS.put(newField.getPublicName(), newField);
//        newField.addColumnData(columnLabel, columnType, columnDefaultWidth, columnReadOnly);
//        if(GROUP_LIST==null) GROUP_LIST = new ArrayList<>();
//        GROUP_LIST.add(newField);
//        return this;
//    }
//    public DMSQLQueryMetadata addField(DMField addingField, String addingFieldsPrefix){
//        if (this.FIELDS_LIST == null) { this.FIELDS_LIST=new ArrayList<>(); this.FIELDS=new HashMap<>(); };
//        DMField addedField = new DMField(addingField,addingFieldsPrefix,true);
//        this.FIELDS_LIST.add(addedField); this.FIELDS.put(addedField.getPublicName(), addedField);
//        return this;
//    }
//    public DMSQLQueryMetadata addField(DMField addingField, String addingFieldsPrefix, int position){
//        if (this.FIELDS_LIST == null) { this.FIELDS_LIST=new ArrayList<>(); this.FIELDS=new HashMap<>(); };
//        DMField addedField = new DMField(addingField,addingFieldsPrefix,true);
//        this.FIELDS_LIST.add(position, addedField); this.FIELDS.put(addedField.getPublicName(),addedField);
//        return this;
//    }
    public String getKeyFieldName(){
        return FIELDS_LIST.get(0).getInternalName();
    }
    public String getKeyFieldPublicName(){
        return FIELDS_LIST.get(0).getPublicName();
    }
    public String getFieldInternalFullName(String publicFieldName){
        for (int i = 0; i < FIELDS_LIST.size(); i++) {
            DMField field = FIELDS_LIST.get(i);
            if(publicFieldName.equals(field.getPublicName())) return field.getInternalFullName();
        }
        return null;
    }
//    public DMSQLQueryMetadata addFieldsList(ArrayList<DMField> addingFieldsList, String addingFieldsPrefix){
//        if(addingFieldsList!=null) {
//            if (this.FIELDS_LIST == null) { this.FIELDS_LIST=new ArrayList<>(); this.FIELDS=new HashMap<>(); };
//            for (int i = 0; i < addingFieldsList.size(); i++) {
//                DMField addedField = new DMField(addingFieldsList.get(i), addingFieldsPrefix, true);
//                this.FIELDS_LIST.add(addedField); this.FIELDS.put(addedField.getPublicName(),addedField);
//            }
//        }
//        return this;
//    }
    protected void addGroupField(DMField groupField) {
        if(GROUP_LIST==null) GROUP_LIST = new ArrayList<>();
        GROUP_LIST.add(groupField);
    }
    public DMSQLQueryMetadata addGroupField(String groupFieldPublicName) {
        DMField groupField = FIELDS.get(groupFieldPublicName);
        if(groupField!=null) { addGroupField(groupField); }
        return this;
    }
    public DMSQLQueryMetadata addFieldFunction(String FIELD_PUBLIC_NAME, String function, String sourceName, String functionBodyFields){
        DMField newField = new DMField(FIELD_PUBLIC_NAME,sourceName, functionBodyFields, ftype_DEFAULT);
        newField.FUNCTION = function;
        this.FIELDS_LIST.add(newField);
        this.FIELDS.put(newField.getPublicName(), newField);
        return this;
    }
//    protected DMField addFieldFunction(String fieldPublicName, String function, String[] functionBodyFieldsInternalNames) throws DataModelException {
//        if (KEY_FIELD==null) throw new DataModelException("Cannot field in object "+getID()+"! Reason: key field dont add!");
//        DMField newFunctionField = new DMField(fieldPublicName, function, functionBodyFieldsInternalNames);
//        SOURCE_FIELDS.add(newFunctionField);
//        return newFunctionField;
//    }

//    public DMSQLQueryMetadata replaceFieldPublicName(String fieldPublicName, String newFieldPublicName){
//        if (FIELDS==null) return this;
//        DMField filed = FIELDS.get(fieldPublicName);
//        filed.setPublicName(newFieldPublicName);
//        return this;
//    }
    public ArrayList<DMField> getFieldsList(){
        return FIELDS_LIST;
    }
    public HashMap<String,DMField> getFields(){
        return FIELDS;
    }

    public DMSQLQueryMetadata addWhereCondition(Condition condition){
        if(condition==null) return this;
        if (MAIN_CONDITION==null) MAIN_CONDITION = new HashMap<>();
        if (MAIN_CONDITION_LIST==null) MAIN_CONDITION_LIST = new ArrayList<>();
        String sCondition = null;
        if (condition.getFieldName()!=null) sCondition= condition.getFieldName();
        if (condition.getCondition()!=null)
            sCondition= (sCondition==null) ? condition.getCondition() : sCondition+condition.getCondition();
        MAIN_CONDITION.put(sCondition, condition); MAIN_CONDITION_LIST.add(condition);
        return this;
    }
//    public DMSQLQueryMetadata addWhereCondition(String addingConditionFieldName, String addingCondition){
//        addWhereCondition(new Condition(addingConditionFieldName, addingCondition));
//        return this;
//    }
//    public DMSQLQueryMetadata addWhereCondition(String addingWhereCondition){
//        addWhereCondition(new Condition(null, addingWhereCondition));
//        return this;
//    }
    public DMSQLQueryMetadata addWhereCondition(String addingConditionFieldName, String addingCondition, Object addingConditionValue){
        addWhereCondition(new Condition(addingConditionFieldName, addingCondition, addingConditionValue));
        return this;
    }
    public DMSQLQueryMetadata addWhereConditions(HashMap<String,Condition> conditions){
        if(conditions==null) return this;
        Iterator<String> conIterator=conditions.keySet().iterator();
        while (conIterator.hasNext()){
            String sFieldNameWithCondition = conIterator.next();
            addWhereCondition(conditions.get(sFieldNameWithCondition));
        }
        return this;
    }

    public DMSQLQueryMetadata addHavingCondition(String addingCondition){
        if (HAVING_LIST==null) HAVING_LIST = new ArrayList<>();
        Condition addingHavCondition = new Condition(null,addingCondition, null);
        HAVING_LIST.add(addingHavCondition);
        return this;
    }
    public DMSQLQueryMetadata addOrder(String... joiningOrders){
        for (int i = 0; i < joiningOrders.length; i++) {
            String joiningOrderFieldName = joiningOrders[i];
            if(joiningOrderFieldName!=null) {
                String orderFieldName = getFieldInternalFullName(joiningOrderFieldName);
                if(orderFieldName==null) orderFieldName= joiningOrderFieldName;
                ORDER = (ORDER==null)?orderFieldName:ORDER+","+orderFieldName;
            }
        }
        return this;
    }

    private DMSQLQueryMetadata joinSource(String joinName, String joinedSourceName, String joinedSourceLinkFieldName,
                                          String mainSourceName, String mainSourceLinkFieldName, String additionalCondition, boolean byLeft) {
        if(JOINS==null) JOINS = new ArrayList<>();
        String joinType = (byLeft)?"left":"inner";
        JOINS.add(new String[]{ joinType, joinName, joinedSourceName,
                joinedSourceLinkFieldName, "=",mainSourceName, mainSourceLinkFieldName,
                additionalCondition});
        return this;
    }
//    private DMSQLQueryMetadata joinSource(String joinedSourceName, String joinedSourceLinkFieldName,
//                                          String mainSourceName, String mainSourceLinkFieldName, boolean byLeft) {
//        return joinSource(joinedSourceName, joinedSourceName, joinedSourceLinkFieldName, mainSourceName, mainSourceLinkFieldName, null, byLeft);
//    }
    public DMSQLQueryMetadata joinSource(String joinedSourceName, String joinedSourceLinkFieldName,
                                         String mainSourceName, String mainSourceLinkFieldName) {
        return joinSource(joinedSourceName, joinedSourceName, joinedSourceLinkFieldName, mainSourceName, mainSourceLinkFieldName, null, false);
    }
    public DMSQLQueryMetadata joinSourceByLeft(String joinedSourceName, String joinedSourceLinkFieldName,
                                               String mainSourceName, String mainSourceLinkFieldName) {
        return joinSource(joinedSourceName, joinedSourceName, joinedSourceLinkFieldName, mainSourceName, mainSourceLinkFieldName, null, true);
    }
    public DMSQLQueryMetadata joinSourceByLeft(String joinName, String joinedSourceName, String joinedSourceLinkFieldName,
                                               String mainSourceName, String mainSourceLinkFieldName, String additionalCondition) {
        return joinSource(joinName, joinedSourceName, joinedSourceLinkFieldName, mainSourceName, mainSourceLinkFieldName, additionalCondition, true);
    }


    protected DMSQLQueryMetadata joinMetadataSources(DMSQLQueryMetadata joiningQueryMetadata, String joinedSourceLinkFieldName,
                                           String mainSourceName, String mainSourceLinkFieldName, boolean joinByLeft) {
        if(joiningQueryMetadata==null) return this;
        if(joiningQueryMetadata.MAIN_SOURCE_NAME !=null) {
            joinSource(joiningQueryMetadata.MAIN_SOURCE_NAME, joiningQueryMetadata.MAIN_SOURCE_NAME,
                    joinedSourceLinkFieldName, mainSourceName, mainSourceLinkFieldName, null, joinByLeft);
        }
        if(joiningQueryMetadata.JOINS!=null){
            if(JOINS==null) JOINS = new ArrayList<>();
            for (int i = 0; i < joiningQueryMetadata.JOINS.size(); i++) { JOINS.add(joiningQueryMetadata.JOINS.get(i)); }
        }
        return this;
    }
    public DMSQLQueryMetadata joinMetadataSources(DMSQLQueryMetadata joiningQueryMetadata, String joinedSourceLinkFieldName,
                                                     String mainSourceName, String mainSourceLinkFieldName) {
        return joinMetadataSources(joiningQueryMetadata, joinedSourceLinkFieldName, mainSourceName, mainSourceLinkFieldName, false);
    }
    private DMSQLQueryMetadata addFieldFromMetadata(String sFieldPublicName, DMSQLQueryMetadata joiningQueryMetadata, String sMetadataFieldPublicName, boolean grouped){
        if (joiningQueryMetadata==null || joiningQueryMetadata.FIELDS==null) return this;
        DMField field = DMField.clone(joiningQueryMetadata.FIELDS.get(sMetadataFieldPublicName));
        if (field!=null) {
            field.FIELD_PUBLIC_NAME= sFieldPublicName;
            addField(field, false);
            if (grouped) addGroupField(sFieldPublicName);
        }
        return this;
    }
    public DMSQLQueryMetadata addFieldFromMetadata(String sFieldPublicName, DMSQLQueryMetadata joiningQueryMetadata, String sMetadataFieldPublicName){
        return addFieldFromMetadata(sFieldPublicName, joiningQueryMetadata, sMetadataFieldPublicName, false);
    }
    public DMSQLQueryMetadata addGroupedFieldFromMetadata(String sFieldPublicName, DMSQLQueryMetadata joiningQueryMetadata, String sMetadataFieldPublicName){
        return addFieldFromMetadata(sFieldPublicName, joiningQueryMetadata, sMetadataFieldPublicName, true);
    }

//    public HashMap<String,String> getJoinedSourceParameters(String joinedSourceName, HashMap<String, String> parameters) {
//        HashMap<String,String> result = new HashMap<>();
//        for (int i = 0; i < FIELDS_LIST.size(); i++) {
//            DMField field = FIELDS_LIST.get(i);
//            if (joinedSourceName.equals(field.getSourceName()) && parameters.containsKey(field.getPublicName()))
//                result.put(field.getInternalName(), parameters.get(field.getPublicName()));
//        }
//        return result;
//    }

//    public DMField getJoinedSourceFieldByIntFieldName(String joinedSourceName, String joinedSourceFieldIntName) {
//        for (int i = 0; i < FIELDS_LIST.size(); i++) {
//            DMField field = FIELDS_LIST.get(i);
//            if (joinedSourceName.equals(field.getSourceName()) && joinedSourceFieldIntName.equals(field.getInternalName()))
//                return field;
//        }
//        return null;
//    }
//    public String getJoinedSourceFieldIntName(String joinedSourceName, String fieldPublicName) {
//        for (int i = 0; i < FIELDS_LIST.size(); i++) {
//            DMField field = FIELDS_LIST.get(i);
//            if (joinedSourceName.equals(field.getSourceName()) && fieldPublicName.equals(field.getPublicName()))
//                return field.getInternalName();
//        }
//        return null;
//    }


//    public DMSQLQueryMetadata joinMetadata(DMSQLQueryMetadata joiningQueryMetadata, String joiningFieldsPrefix, boolean joinByLeft) {
//        if(joiningQueryMetadata==null) return this;
//        if(FIELDS_LIST !=null&&joiningQueryMetadata.FIELDS_LIST !=null) {
//            for (int i = 0; i < FIELDS_LIST.size(); i++) {
//                DMField field = FIELDS_LIST.get(i);
//                for (int j = 0; j < joiningQueryMetadata.FIELDS_LIST.size(); j++) {
//                    DMField joiningField = joiningQueryMetadata.FIELDS_LIST.get(j);
//                    if (field.getInternalName()!=null&&field.getSourceName()==null && field.getInternalName().equals(joiningField.getPublicName())) {
//                        field.FIELD_SOURCE= joiningField.getSourceName();
//                        field.FIELD_NAME= joiningField.getInternalName();
//                    } else if(field.getInternalName()!=null&&field.getSourceName()!=null
//                            && field.getInternalName().equals(joiningField.getPublicName())&&field.getSourceName().equals(joiningField.getSourceName())){
//                        //nothing
//                    } else if("*".equals(field.getInternalName())&&!"*".equals(joiningField.getInternalName()) && field.getSourceName()!=null&&joiningField.getSourceName()!=null&&
//                            ( field.getSourceName().equals(joiningField.getSourceName())
//                                    || (field.getSourceName().endsWith("*")&&joiningField.getSourceName().startsWith(field.getSourceName().replace("*","")) ) )){
//                        addField(joiningField, joiningFieldsPrefix, i+1);
//                        i++;
//                    }
//                }
//            }
//        }
//        if(joiningQueryMetadata.MAIN_SOURCE_NAME !=null) {
//            if(!joinByLeft) joinSource(joiningQueryMetadata.MAIN_SOURCE_NAME, joiningQueryMetadata.MAIN_SOURCE_NAME, joiningQueryMetadata.MAIN_CONDITION);
//            else  joinSourceByLeft(joiningQueryMetadata.MAIN_SOURCE_NAME, joiningQueryMetadata.MAIN_SOURCE_NAME, joiningQueryMetadata.MAIN_CONDITION);
//        } else {
//            if(joiningQueryMetadata.MAIN_CONDITION !=null) addWhereConditions(joiningQueryMetadata.MAIN_CONDITION);
//        }
//        if(joiningQueryMetadata.ORDER!=null) ORDER = (ORDER==null)?joiningQueryMetadata.ORDER:ORDER+","+joiningQueryMetadata.ORDER;
//        if(joiningQueryMetadata.JOINS!=null){
//            if(JOINS==null) JOINS = new ArrayList<>();
//            for (int i = 0; i < joiningQueryMetadata.JOINS.size(); i++) { JOINS.add(joiningQueryMetadata.JOINS.get(i)); }
//        }
//        return this;
//    }


    protected String getSelectSQLQuery(DBUserSession dbus){
        String fieldsList = null;
        if(FIELDS_LIST!=null){
            for (int i = 0; i < FIELDS_LIST.size(); i++) {
                DMField field = FIELDS_LIST.get(i);
                if(!"*".equals(field.getInternalName())) {
                    fieldsList = (fieldsList == null) ? field.getPublicFullName(dbus) : fieldsList + "," + field.getPublicFullName(dbus);
                }
            }
        }
        String resultSelectQuery = "SELECT " + ((fieldsList==null)?"":fieldsList);
        if (MAIN_SOURCE==null)
            resultSelectQuery = resultSelectQuery + ((MAIN_SOURCE_NAME ==null)?"":" FROM "+ MAIN_SOURCE_NAME);
        else
            resultSelectQuery = resultSelectQuery + " FROM (" + MAIN_SOURCE + ") " + ((MAIN_SOURCE_NAME ==null)?"":" AS "+ MAIN_SOURCE_NAME);
        if(JOINS!=null)
            for (int i = 0; i < JOINS.size(); i++) {
                String[] joinData = JOINS.get(i);
                String sJOIN = ("left".equals(joinData[0])) ? " LEFT JOIN " : " INNER JOIN ";
                String sAdditionalCondition = "";
                if (joinData.length>7 && joinData[7]!=null) sAdditionalCondition = " and "+joinData[7];
                resultSelectQuery = resultSelectQuery + sJOIN + joinData[2]+" as "+joinData[1]+" ON "+joinData[1]+"."+joinData[3]+joinData[4]+joinData[5]+"."+joinData[6]+sAdditionalCondition;
            }
        String sMainCondition = null;
        if(MAIN_CONDITION_LIST!=null)
            for (int i = 0; i < MAIN_CONDITION_LIST.size(); i++) {
                Condition condition = MAIN_CONDITION_LIST.get(i);
                if (condition.getFieldName() != null) {
                    DMField field = FIELDS.get(condition.getFieldName());
                    if (field != null)
                        sMainCondition = (sMainCondition == null) ? field.getInternalFullName() + condition.getCondition() : sMainCondition+" and " + field.getInternalFullName() + condition.getCondition();
                    else
                        sMainCondition = (sMainCondition == null) ? condition.getFieldWithCondition() : sMainCondition+" and " + condition.getFieldWithCondition();
                } else {
                    sMainCondition = (sMainCondition == null) ? condition.getCondition() : sMainCondition+" and " + condition.getCondition();
                }
            }
        String groupList = null;
        if(GROUP_LIST!=null){
            for (int i = 0; i < GROUP_LIST.size(); i++) {
                DMField groupField = GROUP_LIST.get(i);
                groupList = (groupList==null)?groupField.getInternalFullName():groupList+","+groupField.getInternalFullName();
            }
        }
        String havingList = null;
        if(HAVING_LIST!=null){
            for (int i = 0; i < HAVING_LIST.size(); i++) {
                Condition havCondition = HAVING_LIST.get(i);
                havingList = (havingList == null) ? havCondition.getCondition() : havingList+" and " + havCondition.getCondition();
//                if (havCondition.fieldName != null) {
//                } else {
//                }
            }
        }
        return resultSelectQuery + ((sMainCondition==null)?"":" WHERE "+ sMainCondition) + ((groupList==null)?"":" GROUP BY "+groupList)
                + ((havingList==null)?"":" HAVING "+havingList)
                + ((ORDER==null)?"":" ORDER BY "+ORDER);
    }
    protected Object[] getConditionsValues(){
        if(MAIN_CONDITION_LIST==null) return null;
        Object[] result = new Object[0];
        for (int i = 0; i < MAIN_CONDITION_LIST.size(); i++) {
            Condition condition = MAIN_CONDITION_LIST.get(i);
            if (condition.hasCondition()) {
                result = Arrays.copyOf(result, result.length+1);
                result[result.length-1] = condition.getConditionValue();
            }
        }
        return result;
    }

    public ArrayList<HashMap<String,Object>> getColumns(){
        return COLUMNS;
    }

    protected Object[] getInsertSQLQueryData(){
        String fieldsList = null;
        String fieldsValuesList = null;
        Object[] fieldsValues = new Object[0];
        if(FIELDS_LIST!=null&&UPDATEBLE_VALUES!=null){
            for (int i = 0; i < FIELDS_LIST.size(); i++) {
                DMField field = FIELDS_LIST.get(i);
                if(MAIN_SOURCE_NAME.equals(field.getSourceName()) && field.FUNCTION==null && UPDATEBLE_VALUES.containsKey(field.getPublicName())){
                    fieldsList = (fieldsList == null) ? field.getInternalName() : fieldsList + "," + field.getInternalName();
                    fieldsValuesList = (fieldsValuesList == null) ? "?" : fieldsValuesList + "," + "?";
                    fieldsValues = Arrays.copyOf(fieldsValues, fieldsValues.length+1);
                    fieldsValues[fieldsValues.length-1] = UPDATEBLE_VALUES.get(field.getPublicName());
                }
            }
        }
        String resultInsertQuery = "INSERT INTO " + ((MAIN_SOURCE_NAME ==null)?"": MAIN_SOURCE_NAME)
                + "(" + ((fieldsList==null)?"":fieldsList) + ") VALUES ("+((fieldsValuesList==null)?"":fieldsValuesList)+")";
        if (fieldsValues.length==0) return new Object[]{resultInsertQuery};
        Object[] result = new Object[fieldsValues.length+1];
        result[0] = resultInsertQuery;
        for (int i = 0; i < fieldsValues.length; i++) {
            result[i+1] = fieldsValues[i];
        }
        return result;
    }
    public Object[] getUpdateSQLQuery(){
        String fieldsList = null;
        Object[] fieldsValues = new Object[0];
        if(FIELDS_LIST!=null&&UPDATEBLE_VALUES!=null){
            for (int i = 0; i < FIELDS_LIST.size(); i++) {
                DMField field = FIELDS_LIST.get(i);
                if(MAIN_SOURCE_NAME.equals(field.getSourceName()) && field.FUNCTION==null && UPDATEBLE_VALUES.containsKey(field.getPublicName())){
                    fieldsList = (fieldsList == null) ? field.getInternalName()+"=?" : fieldsList + "," + field.getInternalName()+"=?";
                    fieldsValues = Arrays.copyOf(fieldsValues, fieldsValues.length+1);
                    fieldsValues[fieldsValues.length-1] = UPDATEBLE_VALUES.get(field.getPublicName());
                }
            }
        }
        String sConditions = null;
        if (MAIN_CONDITION_LIST!=null){
            for (int i = 0; i < MAIN_CONDITION_LIST.size(); i++) {
                Condition condition = MAIN_CONDITION_LIST.get(i);
                if (condition.getFieldName()!=null){
                    String sCondition;
                    if (condition.hasCondition()) {
                        sCondition = condition.getFieldName() + condition.getCondition();
                        fieldsValues = Arrays.copyOf(fieldsValues, fieldsValues.length+1);
                        fieldsValues[fieldsValues.length-1] = condition.getConditionValue();
                    } else
                        sCondition=condition.getFieldName()+" "+condition.getConditionValue().toString();
                    sConditions = (sConditions==null) ? sCondition : sConditions+" and "+sCondition;
                }
            }
        }
        String resultInsertQuery = "UPDATE " + ((MAIN_SOURCE_NAME ==null)?"": MAIN_SOURCE_NAME)
                + " SET " + ((fieldsList==null)?"":fieldsList) + " WHERE "+((sConditions==null)?"":sConditions);
        if (fieldsValues.length==0) return new Object[]{resultInsertQuery};
        Object[] result = new Object[fieldsValues.length+1];
        result[0] = resultInsertQuery;
        for (int i = 0; i < fieldsValues.length; i++) {
            result[i+1] = fieldsValues[i];
        }
        return result;
    }
    public Object[] getDeleteSQLQuery(){
        String sConditions = null;
        Object[] conditionValues = new Object[0];
        if (MAIN_CONDITION_LIST!=null){
            for (int i = 0; i < MAIN_CONDITION_LIST.size(); i++) {
                Condition condition = MAIN_CONDITION_LIST.get(i);
                if (condition.getFieldName()!=null){
                    String sCondition;
                    if (condition.hasCondition()) {
                        sCondition = condition.getFieldName() + condition.getCondition();
                        conditionValues = Arrays.copyOf(conditionValues, conditionValues.length+1);
                        conditionValues[conditionValues.length-1] = condition.getConditionValue();
                    } else
                        sCondition=condition.getFieldName()+" "+condition.getConditionValue().toString();
                    sConditions = (sConditions==null) ? sCondition : sConditions+" and "+sCondition;
                }
            }
        }
        String resultInsertQuery = "DELETE FROM " + ((MAIN_SOURCE_NAME ==null)?"": MAIN_SOURCE_NAME)
                + " WHERE "+((sConditions==null)?"":sConditions);
        if (conditionValues.length==0) return new Object[]{resultInsertQuery};
        Object[] result = new Object[conditionValues.length+1];
        result[0] = resultInsertQuery;
        for (int i = 0; i < conditionValues.length; i++) {
            result[i+1] = conditionValues[i];
        }
        return result;
    }
//    protected Object[] getUpdatableValues(){
//
//    }

    public <T extends Object> DMSQLQueryMetadata setUpdatableValues(HashMap<String,T> parameters){
        if(FIELDS_LIST==null) return this;
        if (UPDATEBLE_VALUES==null) UPDATEBLE_VALUES= new HashMap();
        for (int i = 0; i < FIELDS_LIST.size(); i++) {
            DMField field = FIELDS_LIST.get(i);
            String sFieldPublicName = field.getPublicName();
            if(field.FUNCTION==null && parameters.containsKey(sFieldPublicName)){
                UPDATEBLE_VALUES.put(sFieldPublicName, parameters.get(sFieldPublicName));
            }
        }
        return this;
    }
    public DMSQLQueryMetadata setUpdatableFieldValue(String fieldName, Object value){
        if (UPDATEBLE_VALUES==null) UPDATEBLE_VALUES= new HashMap();
        UPDATEBLE_VALUES.put(fieldName,value);
        return this;
    }

    protected static Object validateField(String fieldName, Object value, int iFieldType) throws DataModelException {
        if(ftype_BIGINT==iFieldType){
            if (valIsNull(value)) {
                throw new DataModelException("Cannot validateField! Field:"+fieldName+" cannot be NULL or empty!");
            }
            return value;
        } else if(ftype_BIGINT_NULL==iFieldType){
            if (value==null) return null;
            if (valIsNull(value)) {
                throw new DataModelException("Cannot validateField! Field:"+fieldName+" cannot be NULL or empty!");
            }
            return value;
        } else if(ftype_STRING==iFieldType){
            if (valIsNull(value)) {
                throw new DataModelException("Cannot validateField! Field:"+fieldName+" cannot be NULL or empty!");
            }
            return value;
        } else if(ftype_STRING_NULL==iFieldType){
            try {
                if (value!=null)
                    value.toString();
            } catch (Exception e){
                throw new DataModelException("Cannot validateField! Field:"+fieldName+" cannot convert to string!");
            }
            return value;
        } else if(ftype_INTEGER==iFieldType){
            try{
                return Integer.parseInt(value.toString());
            } catch (Exception e){
                throw new DataModelException("Cannot validateField! Field:"+fieldName+" no Integer value!");
            }
        } else if(ftype_INTEGER_NULL==iFieldType){
            if (value==null) return null;
            try{
                return Integer.parseInt(value.toString());
            } catch (Exception e){
                throw new DataModelException("Cannot validateField! Field:"+fieldName+" no Integer value!");
            }
        } else if(ftype_FLOAT==iFieldType){
            try{
                return Float.parseFloat(value.toString());
            } catch (Exception e){
                throw new DataModelException("Cannot validateField! Field:"+fieldName+" no Float value!");
            }
        } else if(ftype_DECIMAL_12_2==iFieldType){
            try{
                Double dValue= Double.parseDouble(value.toString());
                return BigDecimal.valueOf(Double.valueOf(Math.round(dValue.doubleValue() * 100)) / 100);
            } catch (Exception e){
                throw new DataModelException("Cannot validateField! Field:"+fieldName+" no Float value!");
            }
        } else if(ftype_DECIMAL_12_4==iFieldType){
            try{
                Double dValue= Double.parseDouble(value.toString());
                return BigDecimal.valueOf(Double.valueOf(Math.round(dValue.doubleValue() * 10000)) / 10000);
            } catch (Exception e){
                throw new DataModelException("Cannot validateField! Field:"+fieldName+" no Float value!");
            }
        } else if(ftype_DATE==iFieldType){
            try{
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                return dateFormat.parse(value.toString());
            } catch (Exception e){
                throw new DataModelException("Cannot validateField! Field:"+fieldName+" no Date value in format yyyy-MM-dd!");
            }
        } else if(ftype_SDATE==iFieldType){
            try{
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                return dateFormat.parse(value.toString());
            } catch (Exception e){
                throw new DataModelException("Cannot validateField! Field:"+fieldName+" no Date value in format yyyy-MM-dd!");
            }
        } else if(ftype_DATETIME==iFieldType){
            try{
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return dateFormat.parse(value.toString());
            } catch (Exception e){
                throw new DataModelException("Cannot validateField! Field:"+fieldName+" no DateTime value in format yyyy-MM-dd HH:mm:ss!");
            }
        } else if(ftype_DATETIME_NULL==iFieldType){
            if (value==null) return null;
            try{
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return dateFormat.parse(value.toString());
            } catch (Exception e){
                throw new DataModelException("Cannot validateField! Field:"+fieldName+" no DateTime value in format yyyy-MM-dd HH:mm:ss!");
            }
        }
        //throw new DataModelException("Cannot validateField field "+fieldName+"! Missing validate for type "+Integer.valueOf(iFieldType).toString()+"!");
        return value;
    }

    protected static final String DATA_RESULT_ITEM_ERROR_PREFIX = "$error_";

    public DMSQLQueryMetadata validateUpdatableValues(HashMap<String,Object> resultParameters) throws DataModelException {
        if(FIELDS_LIST==null || UPDATEBLE_VALUES==null) return this;
        String errors = null;
        for (int i = 0; i < FIELDS_LIST.size(); i++) {
            DMField field = FIELDS_LIST.get(i);
            String sFieldPublicName = field.getPublicName();
            if(field.FUNCTION==null){
                Object value = UPDATEBLE_VALUES.get(sFieldPublicName);
                try {
                    UPDATEBLE_VALUES.put(sFieldPublicName, validateField(sFieldPublicName, value, field.FIELD_TYPE));
                } catch (DataModelException e) {
                    errors= (errors==null) ? e.getLocalizedMessage() : errors+" "+e.getLocalizedMessage();
                }
            }
        }
        if (resultParameters!=null) resultParameters.putAll(UPDATEBLE_VALUES);
        if (errors!=null)
            throw new DataModelException(errors);
        return this;
    }
    public DMSQLQueryMetadata putResultValuesTo(HashMap<String,Object> parameters){
        parameters.putAll(UPDATEBLE_VALUES);
        return this;
    }

    public static final String TEXT_COLUMN= "text";
    public static final String HTML_TEXT_COLUMN= "html_text";
    public static final String NUMERIC_COLUMN= "numeric";
    public static final String NUMERIC2_COLUMN= "numeric2";
    public static final String CURRENCY_COLUMN= "numeric_currency";
    public static final String PERCENT_COLUMN= "numeric_percent";
    public static final String DATE_COLUMN= "date";
    public static final String TEXT_DATE_COLUMN= "text_date";
    public static final String TEXT_DATETIME_COLUMN= "text_datetime";
    public static final String CHECKBOX_COLUMN= "checkbox";
    public static HashMap<String,Object> getColumnData(String columnName, String columnLabel, String columnType, int columnDefaultWidth, boolean columnReadOnly){
        HashMap<String,Object> columnData = new HashMap<>();
        columnData.put("data", columnName);
        columnData.put("name", columnLabel);
        if(TEXT_COLUMN.equals(columnType)){
            columnData.put("type", "text");
        } else if(HTML_TEXT_COLUMN.equals(columnType)){
            columnData.put("type", "text");columnData.put("html", true);
        } else if(NUMERIC_COLUMN.equals(columnType)){
            columnData.put("type", "numeric");columnData.put("format", "0.[000000]");columnData.put("language", "ru");
        } else if(NUMERIC2_COLUMN.equals(columnType)){
            columnData.put("type", "numeric");columnData.put("format", "0.00");columnData.put("language", "ru");
        } else if(CURRENCY_COLUMN.equals(columnType)){
            columnData.put("type", "numeric");columnData.put("format", "0,000.00[0000]");columnData.put("language", "ru");
        } else if(PERCENT_COLUMN.equals(columnType)){
            columnData.put("type", "numeric");columnData.put("format", "0%");
        } else if(DATE_COLUMN.equals(columnType)){
            columnData.put("type", "date");
        } else if(TEXT_DATE_COLUMN.equals(columnType)){
            columnData.put("type", "text"); columnData.put("dateFormat","DD.MM.YY");
        } else if(TEXT_DATETIME_COLUMN.equals(columnType)){
            columnData.put("type", "text"); columnData.put("dateFormat","DD.MM.YY HH:mm:ss");
        } else if(CHECKBOX_COLUMN.equals(columnType)){
            columnData.put("type","checkbox");columnData.put("checkedTemplate","1");columnData.put("uncheckedTemplate","0");
        } else columnData.put("type", columnType);
        columnData.put("width", columnDefaultWidth);
        if(columnReadOnly) columnData.put("readOnly",columnReadOnly);
        return columnData;
    }
    public DMSQLQueryMetadata addColumnData(String fieldPublicName, String columnLabel, String columnType, int columnDefaultWidth, boolean columnReadOnly){
        if (COLUMNS==null) COLUMNS= new ArrayList<>();
        COLUMNS.add(getColumnData(fieldPublicName, columnLabel, columnType, columnDefaultWidth, columnReadOnly));
        return this;
    }
    public DMSQLQueryMetadata addHideColumnData(String fieldPublicName, String columnLabel, String columnType, int columnDefaultWidth, boolean columnReadOnly){
        if (COLUMNS==null) COLUMNS= new ArrayList<>();
        HashMap<String,Object> columnData = getColumnData(fieldPublicName, columnLabel, columnType, columnDefaultWidth, columnReadOnly);
        columnData.put("visible",false);
        COLUMNS.add(columnData);
        return this;
    }

//    public DMSQLQueryMetadata addColumnLabelText(String fieldName, String addingColumnLabelText){
//        if (FIELDS==null) return this;
//        DMField filed = FIELDS.get(fieldName);
//        filed.replaceColumnData(filed.getColumnDataLabel() + addingColumnLabelText);
//        return this;
//    }


//    public DMSQLQueryMetadata setColumnLabel(String fieldName, String sColumnLabelText, String sColumnType, int iColumnWidth, boolean bColumnReadOnly){
//        if (FIELDS==null) return this;
//        DMField filed = FIELDS.get(fieldName);
//        filed.addColumnData(sColumnLabelText, sColumnType, iColumnWidth, bColumnReadOnly);
//        return this;
//    }

//    public String getColumnDataLabel(){ return (String)COLUMN_DATA.get("name"); };
//    public DMField replaceColumnData(String columnLabel){ COLUMN_DATA.put("name", columnLabel); return this; }
//    public DMField replaceColumnData(String columnLabel, int width){ COLUMN_DATA.put("name", columnLabel);COLUMN_DATA.put("width", width); return this; }
//    public DMField hideColumn(){ if(COLUMN_DATA!=null)COLUMN_DATA.put("visible",false); return this; }

    public static boolean valIsNull(Integer iVal){
        if(iVal==null) return true;
        return false;
    }
    public static boolean valIsNull(String sVal){
        if(sVal==null||sVal.replaceAll("\\s","").length()==0){ return true; }
        return false;
    }
    public static boolean valIsNull(Object value){
        if(value==null) return true;
        if(String.class.getName().equals(value.getClass().getName())) return valIsNull((String)value);
        return false;
    }

}
