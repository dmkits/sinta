package controllers.mobile;

import controllers.PageController;
import model.core.DMSimpleQuery;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dmkits on 30.11.2016.
 */
public class MobiCashReportController extends PageController {

    public MobiCashReportController() {
        sURL = "/mobile";
        sPAGE_URL = "/mobile/main.html";
    }

    @Override
    protected boolean doGetAction(String sAction, HttpServletRequest req, HttpSession session, HashMap outData) throws Exception {
        if (sAction.equals("get_units")) {
            outData.put("head", "Магазины");
            setModeTo(outData);
            //outData.put("head", addStrParameter("title","Магазины") );
            try {
                DMSimpleQuery.instance(
                        "select StockID,StockName, REPLACE(StockName,'Магазин IN UA ','') as SHORT_NAME, "
                                + " StockID , StockName "
                                + "from r_Stocks where StockID>0 and StockID<10")
                        .select(getSessionDBUS(session))
                        .replaceResultItemName("SHORT_NAME", "short_name")
                        .replaceResultItemName("StockID", "id")
                        .replaceResultItemName("StockName", "name")
                        .addResultToList(outData, "units");
            } catch (Exception e) {
                outData.put("error", e.getLocalizedMessage());
            }
        } else if (sAction.equals("get_main_data")) {
            HashMap<String, String> params = getReqParams(req);
            String sBDate = params.get("bdate");
            String sEDate = params.get("edate");
            String sUnitsCondition = getUnitsCondition(params);
            if (params.containsKey("detail")) {
                DMSimpleQuery.instance().load("scripts/mobile_main_view_d.sql")
                        .setParameter(sBDate).setParameter(sEDate)
                        .setParameter(sUnitsCondition)
                        .select(getSessionDBUS(session))
                        .replaceResultItemName("VALUE_SUM", "value")
                        .replaceResultItemName("LABEL", "label")
                        .replaceResultItemName("UNIT_ID", "unit_id")
                        .replaceResultItemName("ID", "id")
                        .replaceResultItemName("DETAIL_ID", "detail_id")
                        .addToAllResultItems("url", "/mobile")
                        .addResultToList(outData, "items");
            } else {
                DMSimpleQuery.instance().load("scripts/mobile_main_view.sql")
                        .setParameter(sBDate).setParameter(sEDate)
                        .setParameter(sUnitsCondition)
                        .select(getSessionDBUS(session))
                        .replaceResultItemName("VALUE", "value")
                        .replaceResultItemName("LABEL","label")
                        .replaceResultItemName("DETAIL_ID","detail_id")
                        .replaceResultItemName("ID", "id")
                        .addToAllResultItems("url", "/mobile")
                        .addResultToList(outData, "items");
            }
        } else if (sAction.equals("get_detail_view_data")) {
            String sDetailID = getReqParams(req).get("detail_id");
            String sDetailSQLFileName = "scripts/mobile_detail_view_"+sDetailID+".sql";
            HashMap<String, String> params = getReqParams(req);
            String sBDate = params.get("bdate");
            String sEDate = params.get("edate");
            String sUnitsCondition = getUnitsCondition(params);
            DMSimpleQuery.instance().load(sDetailSQLFileName)
                    .setParameter(sBDate).setParameter(sEDate)
                    .setParameter(sUnitsCondition)
                    .select(getSessionDBUS(session))
                    .replaceResultItemName("VALUE", "value")
                    .replaceResultItemName("LABEL", "label")
                    .addResultToList(outData, "items");
        } else return false;
        return true;
    }
    private String getUnitsCondition(HashMap<String, String> params) {
        String sUnitsCondition, sUnitsList = null;
        Iterator<String> iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            String sParamName = iterator.next();
            if (sParamName.indexOf("unit_") >= 0)
                sUnitsList = (sUnitsList == null) ? " "+params.get(sParamName).toString()+" " : " "+sUnitsList  +
                              " "+params.get(sParamName).toString()+ " ";
        }
        return (sUnitsList == null) ? "is Null" :   sUnitsList;
    }
}
