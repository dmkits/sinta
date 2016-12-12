package model.core;

/**
 * Created by dmkits on 04.06.2016.
 */
public class DMField {
    String FIELD_PUBLIC_NAME = null;
    String FIELD_SOURCE = null;
    String FIELD_NAME = null;
    public static final int ftype_DEFAULT = 0;
    public static final int ftype_STRING = 1;
    public static final int ftype_STRING_NULL = 2;
    public static final int ftype_INTEGER = 10;
    public static final int ftype_INTEGER_NULL = 11;
    public static final int ftype_SMALLINT = 15;
    public static final int ftype_BIGINT = 20;
    public static final int ftype_BIGINT_NULL = 21;
    public static final int ftype_FLOAT = 30;
    public static final int ftype_DECIMAL_12_2 = 41;
    public static final int ftype_DECIMAL_12_4 = 42;
    public static final int ftype_DATE = 50;
    public static final int ftype_DATETIME = 51;
    public static final int ftype_DATETIME_NULL = 52;
    public static final int ftype_SDATE = 101;
    int FIELD_TYPE = 0;
    String FUNCTION = null;
    public static final String FUNC_CONCAT = "concat";
    public static final String FUNC_SUMNOTNULL = "SumNotNull";

    public DMField(String FIELD_PUBLIC_NAME, String FIELD_SOURCE, String FIELD_NAME, int FIELD_TYPE) {
        this.FIELD_PUBLIC_NAME = FIELD_PUBLIC_NAME;
        this.FIELD_SOURCE = FIELD_SOURCE;
        this.FIELD_NAME = FIELD_NAME;
        this.FIELD_TYPE = FIELD_TYPE;
    }
    public DMField(DMField field){
        FIELD_PUBLIC_NAME = field.FIELD_PUBLIC_NAME;
        FIELD_SOURCE = field.FIELD_SOURCE;
        FIELD_NAME = field.FIELD_NAME;
        FIELD_TYPE = field.FIELD_TYPE;
        FUNCTION = field.FUNCTION;
    }
//    public DMField(DMField field, String prefix, boolean addColumnData) {
//        this.FIELD_PUBLIC_NAME = (prefix==null)?field.FIELD_PUBLIC_NAME:prefix+field.FIELD_PUBLIC_NAME;
//        this.FIELD_SOURCE = field.FIELD_SOURCE;
//        this.FIELD_NAME = field.FIELD_NAME;
//        if(addColumnData&&field.COLUMN_DATA!=null){
//            this.COLUMN_DATA = new HashMap();
//            Iterator iterator = field.COLUMN_DATA.keySet().iterator();
//            while (iterator.hasNext()){
//                String key = (String)iterator.next();
//                this.COLUMN_DATA.put(key, field.COLUMN_DATA.get(key));
//            }
//            if(this.COLUMN_DATA.containsKey("data")) this.COLUMN_DATA.put("data",this.FIELD_PUBLIC_NAME);
//        }
//    }
    public DMField(String FIELD_PUBLIC_NAME, String function, String[] functionBodyFields) {
        String functionName = function;if(FUNC_CONCAT.equals(function)) functionName=null;
        String functionBodyFieldsList = null;
        for (int i = 0; i < functionBodyFields.length; i++) {
            String functionBodyFieldName = functionBodyFields[i];
            String functionOperation = function; if(FUNC_CONCAT.equals(function)) functionOperation="+";
            functionBodyFieldsList = (functionBodyFieldsList==null)?functionBodyFieldName:functionBodyFieldsList+functionOperation+functionBodyFieldName;
        }
        this.FIELD_PUBLIC_NAME = FIELD_PUBLIC_NAME;
        this.FIELD_SOURCE = null;
        this.FIELD_NAME = (functionName==null)?functionBodyFieldsList:functionName+"("+functionBodyFieldsList+")";
    }
    public static DMField clone(DMField field){
        return new DMField(field);
    }

    public String getPublicName(){ return FIELD_PUBLIC_NAME; }
    public void setPublicName(String publicName){ FIELD_PUBLIC_NAME=publicName; }
    public String getSourceName(){ return FIELD_SOURCE; }
    public String getInternalName(){ return FIELD_NAME; }
    public String getInternalFullName(){
        String result = null;
        if(FIELD_SOURCE!=null) result = FIELD_SOURCE+"."+FIELD_NAME;
        else result = FIELD_NAME;
        return result;
    }
    public String getPublicFullName(DBUserSession dbus){
        //String result = null;
        if(FIELD_NAME==null)
            return FIELD_PUBLIC_NAME;
        if(FIELD_SOURCE!=null&&FUNCTION==null) {
            if(FIELD_PUBLIC_NAME!=null && !FIELD_NAME.equals(FIELD_PUBLIC_NAME))
                return FIELD_SOURCE+"."+FIELD_NAME+" as "+FIELD_PUBLIC_NAME;
            else
                return FIELD_SOURCE+"."+FIELD_NAME;
        }
        if(FIELD_SOURCE==null&&FIELD_PUBLIC_NAME!=null&&FUNCTION==null) {
            return FIELD_NAME+" as "+FIELD_PUBLIC_NAME;
        }
        if(FIELD_NAME!=null&&FUNCTION!=null) {
            String sTablePrefix = "";
            if (FIELD_SOURCE!=null) sTablePrefix = FIELD_SOURCE+".";
            if(FUNCTION.equals(FUNC_CONCAT)) {
                if(dbus.DBENGINE_DERBY.equals(dbus.getDBEngine())||dbus.DBENGINE_DERBY_EMBEDDED.equals(dbus.getDBEngine())){
                    return FIELD_NAME.replace("+","||")+" as "+FIELD_PUBLIC_NAME;
                }
            } if(FUNCTION.equals(FUNC_SUMNOTNULL)) {
                return "COALESCE(SUM("+sTablePrefix+FIELD_NAME+"),0) as "+FIELD_PUBLIC_NAME;
            } else return FUNCTION+"("+sTablePrefix+FIELD_NAME+") as "+FIELD_PUBLIC_NAME;
        }
        return null;
    }
}
