package controllers.sysadmin;

import controllers.AccessController;
import controllers.PageController;
import model.DataModel;
import model.core.DBUserSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by dmkits on 06.04.16.
 */
public class StartupParametersController extends PageController {

    public StartupParametersController() { sURL="/sysadmin/startup_parameters"; sPAGE_URL="/pages/sysadmin/startup_parameters.html"; }

    @Override
    protected boolean doGetAction(String sAction, HttpServletRequest req, HttpSession session, HashMap outData){
        if(sAction.equals("get_app_local_config")){
            Properties appLocalConfigProps = AccessController.appLocalConfig.getProperties();
            if (appLocalConfigProps!=null) {
                HashMap appLocalParams = new HashMap();
                putAppLocalParameters(appLocalParams, appLocalConfigProps);
                outData.put(DATA_ITEM, appLocalParams);
            }
            if (AccessController.ErrorMsg!=null) outData.put("error", AccessController.ErrorMsg);
        } else return false;
        return true;
    }
    private void putAppLocalParameters(HashMap outData, Properties appLocalConfigProps) {
        String sDBEngine = appLocalConfigProps.getProperty(DBUserSession.DBENGINE);
        outData.put("db.engine", sDBEngine);
        outData.put("db.driver", appLocalConfigProps.getProperty(DBUserSession.DRIVERNAME));
        outData.put("db.driverlib", appLocalConfigProps.getProperty(DBUserSession.DRIVERLIB));
        outData.put("db.Host", appLocalConfigProps.getProperty(DBUserSession.DBHOST));
        outData.put("db.Name", appLocalConfigProps.getProperty(DBUserSession.DBNAME));
        outData.put("db.Created", appLocalConfigProps.getProperty(DBUserSession.CREATE_DB_IF_NOT_EXISTS));
        String sDBURL = appLocalConfigProps.getProperty(DBUserSession.DBURL);
        if (sDBURL==null) sDBURL = DBUserSession.getDBConnURL(appLocalConfigProps, sDBEngine);
        outData.put("db.DBURL", sDBURL);
        outData.put("db.user", appLocalConfigProps.getProperty(DBUserSession.USER));
        outData.put("db.password", appLocalConfigProps.getProperty(DBUserSession.PASSWORD));
    }

    @Override
    protected boolean doPostAction(String sAction, HttpServletRequest req, HttpSession session, HashMap data){
        if ("load_app_local_config".equals(sAction)) {
            try {
                AccessController.appLocalConfig.getProperties().clear();
                AccessController.appLocalConfig.load();
                putAppLocalParameters(data, AccessController.appLocalConfig.getProperties());
                AccessController.ErrorMsg = null;
            } catch (Exception e) {
                AccessController.ErrorMsg = "Cannot load application local configuration! Reason:" + e.getMessage();
                data.put("error", AccessController.ErrorMsg);
            }
        } else if ("save_app_local_config_and_reconnect".equals(sAction)) {
            try {
                Properties appLocalConfigProps = AccessController.appLocalConfig.getProperties();
                appLocalConfigProps.clear();
                for (Enumeration e = req.getParameterNames(); e.hasMoreElements();) {
                    String paramName = (String) e.nextElement();
                    String paramValue= req.getParameter(paramName);
                    if ("db.engine".equals(paramName)) { appLocalConfigProps.setProperty(DBUserSession.DBENGINE,paramValue); }
                    if ("db.driver".equals(paramName)) { appLocalConfigProps.setProperty(DBUserSession.DRIVERNAME,paramValue); }
                    if ("db.driverlib".equals(paramName)) { appLocalConfigProps.setProperty(DBUserSession.DRIVERLIB,paramValue); }
                    if ("db.Host".equals(paramName)) { appLocalConfigProps.setProperty(DBUserSession.DBHOST,paramValue); }
                    if ("db.Name".equals(paramName)) { appLocalConfigProps.setProperty(DBUserSession.DBNAME,paramValue); }
                    if ("db.Created".equals(paramName)) { appLocalConfigProps.setProperty(DBUserSession.CREATE_DB_IF_NOT_EXISTS,paramValue); }
                    if ("db.user".equals(paramName)) { appLocalConfigProps.setProperty(DBUserSession.USER,paramValue); }
                    if ("db.password".equals(paramName)) { appLocalConfigProps.setProperty(DBUserSession.PASSWORD,paramValue); }
                }
                DBUserSession.getDBConnURL(appLocalConfigProps, null);
                putAppLocalParameters(data, appLocalConfigProps);
                AccessController.appLocalConfig.save();
                data.put(DATA_UPDATE_COUNT, 1);
                HashMap appLocalParams = new HashMap();
                putAppLocalParameters(appLocalParams, appLocalConfigProps);
                data.put(DATA_RESULT_ITEM,appLocalParams);
            } catch (Exception e) {
                data.put("error", "Cannot save application local configuration! Reason:" + e.getMessage());
                return true;
            }
            try {
                try{
                    DBUserSession dbus = getSessionDBUS(session);
                    dbus.closeForced();
                } catch (Exception e){}
                DBUserSession dbus = new DBUserSession(AccessController.appLocalConfig.getProperties());
                session.setAttribute(AccessController.DB_SESSION, dbus);
                data.put("reconnect_result", "Reconnected");
                data.put("reconnect_URL", dbus.getURL());
            } catch (Exception e) {
                data.put("reconnect_error", "Cannot reconnect to database! Reason:" + e.getMessage());
                data.put("validate_error", "Cannot validate database, because cannot reconnect to database! Reason:" + e.getMessage());
                return true;
            }
            try {
                DBUserSession dbus = getSessionDBUS(session);
                DataModel.instance().validate(dbus);
                AccessController.ValidateErrorMsg = null;
            } catch (Exception e) {
                data.put("validate_error", "Cannot validate database! Reason:" + e.getMessage());
                AccessController.ValidateErrorMsg = AccessController.DB_VALIDATE_ERROR_MSG+e.getMessage();
            }
        } else return false;
        return true;
    }
}
