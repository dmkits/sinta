package model;

import controllers.AccessController;
import model.core.*;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static model.core.DMSQLQueryMetadata.*;

/**
 * Created by dmkits on 14.04.16.
 */
public class DataModel extends DMBase {
    public static final String DMSchema="APP";
    public static DMT_CHANGE_LOG DM_CHANGE_LOG = null;
    public static final String[][] CHANGE_LOG = new String[][]{

    };

    public static void init() throws DataModelException {
        initDMInstance(new DataModel());

        DMT_CHANGE_LOG.init();
        DM_CHANGE_LOG = DMT_CHANGE_LOG.instance();
        DMObjectsList.clear();

//        DMT_sys_sync_errors_log.init();
//        DMT_sys_sync_databases.init();
//        DMT_sys_sync_incoming_data.init();
//        DMT_sys_sync_incoming_data_details.init();
//        DMT_sys_sync_output_data.init();
//        DMT_sys_sync_output_data_details.init();
//
//        DMT_sys_docstates.init();
//        DMT_sys_operstates.init();
//        DMT_sys_currency.init();
//
//        DMT_dir_units.init();
//        DMT_dir_contractors.init();
//
//        DMT_dir_products_genders.init();
//        DMT_dir_products_categories.init();
//        DMT_dir_products_subcategories.init();
//        DMT_dir_products_categories_subcategories.init();
//        DMT_dir_products_articles.init();
//        DMT_dir_products_kinds.init();
//        DMT_dir_products_collections.init();
//        DMT_dir_products_compositions.init();
//        DMT_dir_products_sizes.init();
//        DMT_dir_products.init();
//        DMT_dir_products_barcodes.init();
//
//        DMT_wrh_orders_bata.init();
//        DMT_wrh_order_bata_details.init();
//        DMT_wrh_pinvs.init();
//        DMT_wrh_pinv_products.init();
//
//        DMT_fin_retail_receipts.init();
//        DMT_fin_retail_payments.init();
//        DMT_wrh_retail_tickets.init();
//        DMT_wrh_retail_ticket_products.init();
//        DMT_wrh_retail_prevals.init();
//        DMT_wrh_retail_preval_products.init();
//
//        dir_products_categories.init();
//        dir_products_subcategories.init();
//        dir_products.init();
//
//        wrh_orders_bata.init();
//        wrh_order_bata_details.init();
//        wrh_pinvs.init();
//        wrh_pinv_products.init();
//
//        DM_wrh_price_retail_prevals.init();
//        DM_wrh_price_retail_preval_products.init();
//
//        DM_fin_retail_payments.init();
//        DM_wrh_retail_ticket_products.init();
//        retail_cashreport.init();
//
//        wrh_products_moves.init();
//        wrh_products_balance.init();
    }
    public static DataModel instance() throws DataModelException {
        return getDMInstance(DataModel.class);
    }

    public static DMT_CHANGE_LOG getDMChangeLog() throws DataModelException { return DM_CHANGE_LOG; }

    /**
     * Validating database objects.
     * @param dbus
     * @throws SQLException
     */
    public void validate(DBUserSession dbus) throws DataModelException {
        if(DM_CHANGE_LOG==null)
            throw new DataModelException("In data model not exists CHANGE_LOG object! Set CHANGE_LOG in data model!");
        else DM_CHANGE_LOG.validate(dbus);
        for(int i=0;i< DMObjectsList.size();i++){
            DMObjectsList.get(i).validate(dbus);
        }
    }

    @Override
    public void getChanges(ArrayList<HashMap<String,Object>> result) throws DataModelException {
    }

    public static DMData getModelObjectsChangesTableID() throws DataModelException {
        return DMData.newDMData(DMBase.CHANGE_ID);
    }
    public static DMData getModelObjectsChangesForTable(HashMap parameters, DBUserSession dbus) throws DataModelException {
        ArrayList<HashMap<String,Object>> tableData = new ArrayList<>();
        generateChanges(dbus, tableData);
        return DMData.newDMData(tableData);
    }

    public static DMData doInitUpdateAction(HashMap parameters, DBUserSession dbus) throws Exception {
        HashMap result= new HashMap();
        ArrayList<HashMap<String,Object>> resultChanges = new ArrayList<>();
        DMData resultData = DMData.newDMData();
        try {
            initUpdate(dbus, resultChanges, result);
        } catch (Exception e){
            resultData.setDataItem("error", "Cannot init/update database! Reason:" + e.getMessage());
        } finally {
            resultData.setDataItem("changeCount", result.get("changeCount"));
            resultData.setDataList(resultChanges);
        }
        try {
            //DataModel.validate(dbus);
            AccessController.ValidateErrorMsg = null;
        } catch (Exception e) {
            resultData.setDataItem("validate_error", "Cannot validate database! Reason:" + e.getMessage());
        }
        return resultData;
    }

    /**
     * Return all new and not ident changes.
     * @param dbus
     * @throws Exception
     */
    private static void generateChanges(DBUserSession dbus, ArrayList<HashMap<String,Object>> changes) throws DataModelException {
        DM_CHANGE_LOG.getChanges(changes);
        try {
            for (int i = 0; i < DMObjectsList.size(); i++) {
                DMObjectsList.get(i).getChanges(changes);
            }
        } catch (Exception dme){
            throw new DataModelException(dme.getLocalizedMessage()+"<br>Please contact your system administrator.");
        }
        Collections.sort(changes, new Comparator<HashMap>() {
            @Override
            public int compare(HashMap i1, HashMap i2) {
                Date sDT1 = (Date) i1.get("datetime");
                Date sDT2 = (Date) i2.get("datetime");
                if ((sDT1).compareTo(sDT2) < 0) return -1;
                else if ((sDT1).equals(sDT2)) {
                    String sID1 = (String) i1.get("id");
                    String sID2 = (String) i2.get("id");
                    if ((sID1).compareTo(sID2) < 0) return -1;
                    else if ((sID1).equals(sID2)) return 0;
                    else return 1;
                }
                return 1;
            }
        });
        HashMap<String,HashMap<String,Object>> hmCurrentCHL;
        String sCurrentCHLError = null;
        try {
            hmCurrentCHL = DM_CHANGE_LOG.getChangeLogDIsMap(dbus);
        } catch (Exception e) {
            sCurrentCHLError = "Cannot read CHANGE_LOG for compare changes with CHANGE_LOG! Reason:" + e.getLocalizedMessage() + "<br>Please update new clean database for create CHANGE_LOG.";
            hmCurrentCHL = new HashMap<>();
        }
        int i=0;
        while(i<changes.size()){
            HashMap changeItem= changes.get(i);
            String sChangeID= (String)changeItem.get(CHANGE_ID);
            String sMsgType = null; String sMsg= null;
            if(!hmCurrentCHL.containsKey(sChangeID)){
                sMsgType="info"; sMsg= "New change id="+sChangeID+".";
                i++;
            } else {
                HashMap hmCurrentCHLItem= hmCurrentCHL.get(sChangeID);
                String sChangeObj= (String)changeItem.get(CHANGE_OBJECT);
                String sChangeValue= (String)changeItem.get(CHANGE_VALUE);
                Date dCurCHLItemDate =(Date)hmCurrentCHLItem.get(DMT_CHANGE_LOG.field_CHANGE_DATETIME);
                Date dChangeDT = (Date)changeItem.get(CHANGE_DATETIME);
                if(!dChangeDT.equals(dCurCHLItemDate)
                        ||!sChangeObj.equals(hmCurrentCHLItem.get(DMT_CHANGE_LOG.field_CHANGE_OBJ))
                        ||!sChangeValue.equals(hmCurrentCHLItem.get(DMT_CHANGE_LOG.field_CHANGE_VAL))){
                    sMsgType="warning"; sMsg="Values of change id="+sChangeID+" not ident values in CHANGE LOG!";
                    i++;
                } else {//already applied
                    changes.remove(i);
                }
            }
            if(sMsgType!=null){ changeItem.put("msgtype",sMsgType); changeItem.put("msg",sMsgType+"<br>"+sMsg); }
        }
        if(sCurrentCHLError!=null) throw new DataModelException(sCurrentCHLError);
    }
    /**
     * Update (or Initing) database from changes data in DataModel classes.
     * @param dbus
     * @throws Exception
     */
    private static void initUpdate(DBUserSession dbus, ArrayList<HashMap<String,Object>> resultChanges, HashMap result) throws DataModelException {

        int changeCount=0;ArrayList<HashMap<String,Object>> changes = new ArrayList<>();
        boolean bChangeLogError= false;
        try{
            DM_CHANGE_LOG.validate(dbus);
        } catch (Exception e){ bChangeLogError=true; }
        if(bChangeLogError){
            DM_CHANGE_LOG.getChanges(changes);
            HashMap changeItem = null; String resultMsg = null;
            try {
                dbus.begin();
            } catch (Exception e){
                resultMsg = "Cannot start transaction! Reason:"+e.getLocalizedMessage()+".";
                addToResultChanges(resultChanges, changeItem, resultMsg);
                throw new DataModelException("Cannot create/update "+ DMT_CHANGE_LOG.class.getName()+"! Reason:"+e.getLocalizedMessage());
            }
            try {
                for (int i = 0; i < changes.size(); i++) {
                    changeItem = changes.get(i);
                    String sChangeVal = (String) changeItem.get(CHANGE_VALUE);
                    dbus.execSQLQuery(sChangeVal);
                    changeCount++;
                    result.put("changeCount", changeCount);
                }
                for (int i = 0; i < changes.size(); i++) {
                    changeItem = changes.get(i);
                    HashMap<String, Object> params = new HashMap();
                    //params.put(DMT_CHANGE_LOG.field_ID, changeItem.get(CHANGE_ID));
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    params.put(DMT_CHANGE_LOG.field_CHANGE_DATETIME, dateFormat.format(changeItem.get(CHANGE_DATETIME)));
                    params.put(DMT_CHANGE_LOG.field_CHANGE_OBJ, changeItem.get(CHANGE_OBJECT));
                    params.put(DMT_CHANGE_LOG.field_CHANGE_VAL, changeItem.get(CHANGE_VALUE));
                    params.put(DMT_CHANGE_LOG.field_CHANGE_APPLIED_DATETIME, dateFormat.format(new Date()));
                    int resultStoreChangelog = DM_CHANGE_LOG.insert(dbus, changeItem.get(CHANGE_ID), params);
                    if (resultStoreChangelog==0) throw new DataModelException("Cannot add changelog item!");
                    addToResultChanges(resultChanges, changeItem, "Applied.");
                }
                dbus.commit();
            } catch (Exception e){
                resultMsg = "Cannot finish transaction! Reason:"+e.getLocalizedMessage()+".";
                addToResultChanges(resultChanges, changeItem, resultMsg);
                try { dbus.rollback(); } catch (Exception e1){}
                throw new DataModelException("Cannot create/update "+ DMT_CHANGE_LOG.class.getName()+"! Reason:"+e.getLocalizedMessage());
            }
        }
        changes.clear();
        generateChanges(dbus, changes);
        for(int i=0;i<changes.size();i++){
            HashMap changeItem= changes.get(i);
            String sChangeMsgType= (String)changeItem.get("msgtype");
            if("info".equals(sChangeMsgType)) {
                String sChangeID = (String) changeItem.get(CHANGE_ID);
                String sChangeValue = (String) changeItem.get(CHANGE_VALUE);
                String sResultMsg = null;
                try {
                    dbus.begin();
                } catch (Exception e){
                    sResultMsg = "Cannot start transaction! Reason:"+e.getLocalizedMessage()+".";
                    addToResultChanges(resultChanges, changeItem, sResultMsg);
                    throw new DataModelException("Cannot create/update "+ DMT_CHANGE_LOG.class.getName()+"! Reason:"+e.getLocalizedMessage());
                }
                try {
                    dbus.execSQLQuery(sChangeValue);
                    changeCount++;
                    result.put("changeCount", changeCount);
                    HashMap params = new HashMap();
                    //params.put(DMT_CHANGE_LOG.field_ID, sChangeID);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    params.put(DMT_CHANGE_LOG.field_CHANGE_DATETIME, dateFormat.format(changeItem.get(CHANGE_DATETIME)));
                    params.put(DMT_CHANGE_LOG.field_CHANGE_OBJ, changeItem.get(CHANGE_OBJECT));
                    params.put(DMT_CHANGE_LOG.field_CHANGE_VAL, sChangeValue);
                    params.put(DMT_CHANGE_LOG.field_CHANGE_APPLIED_DATETIME, dateFormat.format(new Date()));
                    DM_CHANGE_LOG.insert(dbus, sChangeID, params);
                    sResultMsg = "Applied.";
                    addToResultChanges(resultChanges, changeItem, sResultMsg);
                    dbus.commit();
                } catch (SQLException e) {
                    sResultMsg = "Not Applied! Reason:" + e.getLocalizedMessage() + ".";
                    addToResultChanges(resultChanges, changeItem, sResultMsg);
                    try { dbus.rollback(); } catch (Exception e1){}
                    throw new DataModelException("Cannot applied change id=" + sChangeID+"! Reason:"+e.getLocalizedMessage()+".");
                }
            } else if("warning".equals(sChangeMsgType)) {
                addToResultChanges(resultChanges, changeItem, null);
            }
        }
    }

    private static void addToResultChanges(ArrayList<HashMap<String,Object>> resultChanges, HashMap resultItem, String addingResultMsg){
        resultChanges.add(resultItem);
        String msg = (String)resultItem.get("msg");
        if (addingResultMsg!=null) resultItem.put("msg", (msg==null)?addingResultMsg:msg+" "+addingResultMsg);
    }

    public static DMData getChangesDataTableColumns(){
        ArrayList<HashMap<String,Object>> tableDataColumns = new ArrayList<>();
        tableDataColumns.add(getColumnData("id", "Change ID", TEXT_COLUMN, 250, true));
        tableDataColumns.add(getColumnData("datetime", "Change datetime", TEXT_DATETIME_COLUMN, 90, true));
        tableDataColumns.add(getColumnData("object", "Change object", TEXT_COLUMN, 300, true));
        tableDataColumns.add(getColumnData("value", "Change value", TEXT_COLUMN, 450, true));
        tableDataColumns.add(getColumnData("msg", "Change info", HTML_TEXT_COLUMN, 200, true));
        return DMData.newDMData(tableDataColumns);
    }
}
