package model.core;

import org.apache.derby.impl.sql.compile.IsNullNode;

/**
 * Created by dmkits on 12.06.16.
 */
public class Condition {
    private String fieldName = null;
    private String condition = null;
    private Object value = null;
    private static final String ISNULL = "IS NULL";

    public static Condition ISNULLCondition(String sFieldName){
        return new Condition(sFieldName, null, ISNULL);
    }
//    public static Condition LINKCondition(String sFieldName, String sLinkSourceName, String sLinkFieldName){
//        return new Condition(sFieldName, null, ISNULL);
//    }
    public static Condition FieldValueCondition(String sFieldName, String sCondition, Object conditionValue){
    return new Condition(sFieldName, sCondition, conditionValue);
}

    public Condition(String fieldName, String condition, Object value) {
        this.fieldName = fieldName;
        this.condition = condition;
        this.value = value;
    }
    private Condition(String fieldName, String condition) {
        this.fieldName = fieldName;
        this.condition = condition;
    }
    public Condition(Condition condition) {
        fieldName = condition.fieldName;
        this.condition = condition.condition;
        value = condition.value;
    }
    public String getFieldWithCondition(){
        if (condition!=null) return fieldName+condition+"?";
        return fieldName+" "+value.toString();
    }
    public String getFieldName(){ return fieldName; }
    public boolean hasCondition(){
        return condition!=null;
    }
    public String getCondition(){
        if (condition!=null) return condition+"?";
        return " "+value.toString();
    }
    public Object getConditionValue(){ return value; }
}
