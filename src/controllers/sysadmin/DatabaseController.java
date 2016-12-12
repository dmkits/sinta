package controllers.sysadmin;

import controllers.AccessController;
import controllers.PageController;
import model.DataModel;
import model.core.DBUserSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

/**
 * Created by dmkits on 06.04.16.
 */
public class DatabaseController extends PageController {

    public DatabaseController() { sURL="/sysadmin/database"; sPAGE_URL="/pages/sysadmin/database.html"; }

    @Override
    protected boolean doGetAction(String sAction, HttpServletRequest req, HttpSession session, HashMap outData) throws Exception {
        if(sAction.equals("get_connect_to_db_state")){
            DBUserSession dbus = getSessionDBUS(session);
            String sDBURL = dbus.getURL();
            outData.put("result", sDBURL);
            if (AccessController.ValidateErrorMsg!=null) { outData.put("validate_error", AccessController.ValidateErrorMsg); }
        } else if(sAction.equals("get_changelog")){
            DataModel.getDMChangeLog()
                    .getDBChangeLogForTable()
                    .putIDNameTo(outData, DATA_IDENTIFIER)
                    .putColumnsTo(outData, TABLE_DATA_COLUMNS)
                    .selectList(getSessionDBUS(session)).putResultListTo(outData, DATA_ITEMS);
        } else if(sAction.equals("get_changes")){
            DataModel.getChangesDataTableColumns().putDataListTo(outData, TABLE_DATA_COLUMNS);
            DataModel.getModelObjectsChangesTableID().putDataValueTo(outData, DATA_IDENTIFIER);
            DataModel.getModelObjectsChangesForTable(null, getSessionDBUS(session)).putDataListTo(outData, DATA_ITEMS);
        } else return false;
        return true;
    }

    @Override
    protected boolean doPostAction(String sAction, HttpServletRequest req, HttpSession session, HashMap outData) throws Exception {
        if ("reconnect_to_db".equals(sAction)) {
            try {
                try{
                    DBUserSession dbus = getSessionDBUS(session);
                    dbus.closeForced();
                } catch (Exception e){}
                DBUserSession dbus = new DBUserSession(AccessController.appLocalConfig.getProperties());
                session.setAttribute(AccessController.DB_SESSION,dbus);
                outData.put("result", dbus.getURL());
            } catch (Exception e) {
                outData.put(ACTION_ERROR, "Cannot reconnect to database! Reason:" + e.getMessage());
                outData.put("validate_error", "Cannot validate database, because cannot reconnect to database!");
                return true;
            }
            try {
                DBUserSession dbus = getSessionDBUS(session);
                DataModel.instance().validate(dbus);
                AccessController.ValidateErrorMsg = null;
            } catch (Exception e) {
                outData.put("validate_error", "Cannot validate database! Reason:" + e.getMessage());
                AccessController.ValidateErrorMsg = AccessController.DB_VALIDATE_ERROR_MSG+e.getMessage();
            }
        } else if ("clean_db".equals(sAction)) {
            outData.put(ACTION_ERROR, "Cannot clean database: action cannot work!");
//            try {
//                DataModel.clean(AccessController.appLocalConfig);
//                data.put("clean_result", "Cleaned database: " + AccessController.appLocalConfig.getProperty(DBUserSession.DBURL));
//            } catch (Exception e) {
//                data.put("error_msg", "Cannot clean database: " + e.getMessage());
//            }
        } else if ("update_database".equals(sAction)) {
            DBUserSession dbus = getSessionDBUS(session);
            DataModel.getChangesDataTableColumns().putDataListTo(outData, TABLE_DATA_COLUMNS);
            DataModel.getModelObjectsChangesTableID().putDataValueTo(outData, DATA_IDENTIFIER);
            DataModel.doInitUpdateAction(null, dbus).putDataItemTo(outData).putDataListTo(outData, DATA_ITEMS);
            try {
                DataModel.instance().validate(dbus);
                AccessController.ValidateErrorMsg = null;
            } catch (Exception e) {
                outData.put("validate_error", "Cannot validate database! Reason:" + e.getMessage());
                AccessController.ValidateErrorMsg = AccessController.DB_VALIDATE_ERROR_MSG+e.getMessage();
            }
        } else return false;
        return true;
    }
}
