package controllers;

import core.AppCoreLogger;
import model.core.DBUserSession;
import model.core.DataModelException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dmkits on 26.01.16.
 */
public class SysadminController extends PageController {
    public SysadminController() {sURL="/sysadmin"; sPAGE_URL="/pages/sysadmin.html"; }

    @Override
    protected boolean doGetAction(String sAction, HttpServletRequest req, HttpSession session, HashMap outData) {
        if("get_data".equals(sAction)){
            setModeTo(outData);
            getMainData(outData);
        } else if("get_db_connect_state".equals(sAction)) {
            try {
                DBUserSession dbus = getSessionDBUS(session);
                String sDBURL = dbus.getURL();
                outData.put("result", "Connected");
                outData.put("result_URL", sDBURL);
            } catch (Exception e) {
                outData.put(ACTION_ERROR, "Cannot receive database connection state! Reason:" + e.getMessage());
            }
            if (AccessController.ValidateErrorMsg!=null) { outData.put("validate_error", AccessController.ValidateErrorMsg); }
        } else return false;
        return true;
    }
    private void getMainData(HashMap<String,Object> md) {
        AppCoreLogger.log(this, "getMainData:");
        //md.put("title", new String("MODA.DP.UA (SYSADMIN)"));
        md.put("user", new String("sysadmin"));
        ArrayList<HashMap<String,Object>> actions = new ArrayList<>(); md.put("actions", actions);
        HashMap<String,Object> actionItem;
        actions.add(actionItem = new HashMap<String, Object>());
        setItemValues(actionItem, atr("itemname", "actionItemSAMain"), atr("itemtitle", "Main"), atr("action", "open"),
                atr("id", "sa_main"), atr("title", "Sysadmin main"), atr("content", "/sysadmin/main")); actionItem.put("closable", true);
        actions.add(actionItem = new HashMap<String, Object>());
        setItemValues(actionItem, atr("itemname", "actionItemSAStartupParameters"), atr("itemtitle", "Startup parameters"), atr("action", "open"),
                atr("id", "sa_prartup_parameters"), atr("title", "Startup parameters"), atr("content", "/sysadmin/startup_parameters")); actionItem.put("closable", true);
        actions.add(actionItem = new HashMap<String, Object>());
        setItemValues(actionItem, atr("itemname", "actionItemSADatabase"), atr("itemtitle", "Database"), atr("action", "open"),
                atr("id", "sa_database"), atr("title", "Database"), atr("content", "/sysadmin/database")); actionItem.put("closable", true);
        actions.add(actionItem = new HashMap<String, Object>());
        setItemValues(actionItem, atr("itemname", "actionItemSASyncronization"), atr("itemtitle", "Syncronization"), atr("action", "open"),
                atr("id", "sa_syncronization"), atr("title", "Syncronization"), atr("content", "/sysadmin/syncronization")); actionItem.put("closable", true);
        actions.add(actionItem = new HashMap<String, Object>());
        setItemValues(actionItem, atr("itemname", "actionItemClose"), atr("itemtitle", "Exit"),atr("action", "close"));
        actions.add(actionItem = new HashMap<String, Object>());
        setItemValues(actionItem, atr("itemname", "actionItemHelpAbout"), atr("itemtitle", "About"), atr("action", "help_about"));
    }
    private void setItemValues(HashMap<String,Object> pItem, String[]... pValues) {
        for (String[] item : pValues) { pItem.put(item[0],item[1]); }
    }
    private String[] atr(String key,String val) { return new String[]{key,val}; }

    @Override
    protected boolean doPostAction(String sAction, HttpServletRequest req, HttpSession session, HashMap data) throws Exception {
        return super.doPostAction(sAction, req, session, data);
    }
}
