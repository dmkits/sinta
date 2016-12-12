package controllers;

import core.AppCoreLogger;
import model.core.DBUserSession;
import model.core.DataModelException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by dmkits on 19.02.16.
 * Base class for all page's controlles
 */
public class PageController extends HttpServlet {

    protected String sURL="/";
    protected String sPAGE_URL="/";
    protected String sERRORPAGE_URL="/pages/syserror_noaction.html";

    protected final String MODE = "mode";

    protected final String ACTION_ERROR = "error";
    protected static String DATA_IDENTIFIER = "identifier";
    protected static String DATA_ITEMS = "items";
    protected static String TABLE_DATA_COLUMNS= "columns";
    protected static String DATA_ITEM = "item";

    protected static String DATA_LABEL = "label";
    protected static String DATA_VALUE = "value";

    public static final String DATA_UPDATE_COUNT = "updateCount";
    public static final String DATA_RESULT_ITEM = "resultItem";
    public static final String DATA_RESULT_ITEMS = "resultItems";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AppCoreLogger.logReqInfo(this, "goGet:", req);
        String s=req.getRequestURI();
        if(!sURL.equals(s)) {
            forvardTo(sERRORPAGE_URL, req, resp); return;
        }
        // if (JSONRequest.isJSON(req)||JSONRequest.isAJAX(req)) {
        String sAction = req.getParameter("action");
        if(sAction!=null){
            HttpSession session = req.getSession();
            HashMap data = new HashMap();
            try{
                if(!doGetAction(sAction, req, session, data)) data.put(ACTION_ERROR,"Action not supported!");
            }catch (Exception e){
                data.put(ACTION_ERROR,"Cannot execute get action:"+sAction+"! Reason:"+e.getLocalizedMessage());
            }
            JSONResponse.doHttpResponse(resp, data);
            return;
        }
        forvardTo(sPAGE_URL, req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AppCoreLogger.logReqInfo(this, "goPost:", req);
        // if (JSONRequest.isJSON(req)||JSONRequest.isAJAX(req)) {
        String sAction = req.getParameter("action");
        if(sAction!=null){
            HttpSession session = req.getSession();
            HashMap data = new HashMap();
            try{
                if(!doPostAction(sAction, req, session, data)) data.put(ACTION_ERROR,"Action not supported!");
            }catch (Exception e){
                data.put(ACTION_ERROR,"Cannot execute post action:"+sAction+"! Reason:"+e.getLocalizedMessage());
            }
            JSONResponse.doHttpResponse(resp, data);
            return;
        }
        super.doPost(req, resp);
    }

    protected boolean doGetAction(String sAction, HttpServletRequest req, HttpSession session, HashMap outData) throws Exception {
        return false;
    }
    protected boolean doPostAction(String sAction, HttpServletRequest req, HttpSession session, HashMap outData) throws Exception {
        return false;
    }

    protected void forvardTo(String target, ServletRequest sreq, ServletResponse sresp) throws ServletException, IOException {
        AppCoreLogger.logReqInfo(this, "forvardTo: " + target, (HttpServletRequest) sreq);
        RequestDispatcher rd = sreq.getRequestDispatcher(target);
        rd.forward(sreq, sresp);
    }

    protected DBUserSession getSessionDBUS(HttpSession session) throws DataModelException {
        DBUserSession dbus = (DBUserSession) session.getAttribute(AccessController.DB_SESSION);
        if(dbus==null||!dbus.connected()){
            throw new DataModelException("No database connection!");
        }
        return dbus;
    }
    protected HashMap<String,String > getReqParams(HttpServletRequest req){
        HashMap<String,String> params = new HashMap<>();
        for (Enumeration e = req.getParameterNames(); e.hasMoreElements();) {
            String paramName = (String) e.nextElement();
            params.put(paramName,req.getParameter(paramName));
        }
        return params;
    }
    protected HashMap<String,String > getReqParams(HttpServletRequest req,String... paramsNames){
        HashMap<String,String> params = new HashMap<>();
        for (int i=0; i<paramsNames.length;i++) {
            String paramName = paramsNames[i];
            params.put(paramName, req.getParameter(paramName));
        }
        return params;
    }

    protected void setModeTo(HashMap outData){
        if(AccessController.appLocalConfig!=null){
            String sMode = AccessController.appLocalConfig.getProperty(AccessController.appLocalConfigMode);
            if (sMode==null) sMode = "UNKNOWN MODE!!!";
            outData.put(MODE, sMode);
        }
    }
}
