package model.core;

import java.util.ArrayList;
import java.util.HashMap;

import static model.core.DMField.*;
import static model.core.DMSQLQueryMetadata.*;

/**
 * Created by dmkits on 13.05.16.
 */
public class DMT_CHANGE_LOG extends DMTable {

    public static final String tablename = "CHANGE_LOG";
    public static final String field_ID = "ID";
    public static final String field_CHANGE_DATETIME = "CHANGE_DATETIME";
    public static final String field_CHANGE_OBJ = "CHANGE_OBJ";
    public static final String field_CHANGE_VAL = "CHANGE_VAL";
    public static final String field_CHANGE_APPLIED_DATETIME = "APPLIED_DATETIME";

    private DMT_CHANGE_LOG() throws DataModelException {
        addChange("chl__1", "2016-04-14 09:00:00", CREATE_TABLE + "CHANGE_LOG" + "(ID " + type_VARCHAR + "(64)" + ")");
        addChange("chl__2","2016-04-14 09:01:00", ALTER_TABLE+"CHANGE_LOG"+ALTER_COLUMN+"ID"+" "+type_NOTNULL);
        addChange("chl__3","2016-04-14 09:02:00", ALTER_TABLE+"CHANGE_LOG"+ADD_PKEY+"(ID)");
        addChange("chl__4","2016-04-14 09:03:00", ALTER_TABLE+"CHANGE_LOG"+ADD_COLUMN+"CHANGE_DATETIME "+ type_TIMESTAMP);
        addChange("chl__5","2016-04-14 09:04:00", ALTER_TABLE+"CHANGE_LOG"+ALTER_COLUMN+"CHANGE_DATETIME "+type_NOTNULL);
        addChange("chl__6","2016-04-14 09:05:00", ALTER_TABLE+"CHANGE_LOG"+ADD_COLUMN+"CHANGE_OBJ "+type_VARCHAR+"(255)");
        addChange("chl__7","2016-04-14 09:06:00", ALTER_TABLE+"CHANGE_LOG"+ALTER_COLUMN+"CHANGE_OBJ "+type_NOTNULL);
        addChange("chl__8","2016-04-14 09:07:00", ALTER_TABLE+"CHANGE_LOG"+ADD_COLUMN+"CHANGE_VAL "+type_VARCHAR+"(511)");
        addChange("chl__9","2016-04-14 09:08:00", ALTER_TABLE+"CHANGE_LOG"+ALTER_COLUMN+"CHANGE_VAL "+type_NOTNULL);
        addChange("chl_10","2016-04-14 09:09:00", ALTER_TABLE+"CHANGE_LOG"+ADD_COLUMN+"APPLIED_DATETIME "+ type_TIMESTAMP);
        addChange("chl_11","2016-04-14 09:10:00", ALTER_TABLE+"CHANGE_LOG"+ALTER_COLUMN+"APPLIED_DATETIME "+type_NOTNULL);

        METADATA= DMMetadata.newMetadata(tablename, field_ID, ftype_BIGINT)
                .addField(tablename, field_CHANGE_DATETIME, ftype_DATETIME)
                .addField(tablename, field_CHANGE_OBJ, ftype_STRING)
                .addField(tablename, field_CHANGE_VAL, ftype_STRING)
                .addField(tablename, field_CHANGE_APPLIED_DATETIME, ftype_DATETIME);
    }
    public static void init() throws DataModelException {
        initDMInstance(new DMT_CHANGE_LOG());
    }
    public static DMT_CHANGE_LOG instance() throws DataModelException {
        return getDMInstance(DMT_CHANGE_LOG.class);
    }

    public DMMetadata getDBChangeLogForTable() throws DataModelException {
        return METADATA.clone().addOrder(field_CHANGE_DATETIME, field_ID)
                .addColumnData(field_ID, "Change ID", TEXT_COLUMN, 250, true)
                .addColumnData(field_CHANGE_DATETIME, "Change datetime", TEXT_DATETIME_COLUMN, 90, true)
                .addColumnData(field_CHANGE_OBJ, "Change object", TEXT_COLUMN, 300, true)
                .addColumnData(field_CHANGE_VAL, "Change value", TEXT_COLUMN, 450, true)
                .addColumnData(field_CHANGE_APPLIED_DATETIME, "Applied datetime", TEXT_DATETIME_COLUMN, 90, true);
    }

    public HashMap<String,HashMap<String,Object>> getChangeLogDIsMap(DBUserSession dbus) throws DataModelException {
        ArrayList<HashMap<String,Object>> resultItems =
                METADATA.clone().addOrder(field_ID).selectList(dbus).getSelectResultItems();
        if (resultItems==null) return null;
        HashMap<String,HashMap<String,Object>> result = new HashMap<>();
        for (int i = 0; i < resultItems.size(); i++) {
            HashMap<String,Object> resultItem = resultItems.get(i);
            if (resultItem!=null) {
                result.put(resultItem.get(field_ID).toString(), resultItem);
            }
        }
        return result;
    }
}
