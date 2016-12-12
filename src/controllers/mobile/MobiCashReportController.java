package controllers.mobile;

import controllers.PageController;
import model.core.DMMetadata;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;

import static model.core.DMBase.*;
import static model.core.DMData.*;
import static model.core.DMField.*;
/**
 * Created by dmkits on 30.11.2016.
 */
public class MobiCashReportController extends PageController {

    public MobiCashReportController() {
        sURL="/mobile"; sPAGE_URL="/mobile/main.html";
    }

    @Override
    protected boolean doGetAction(String sAction, HttpServletRequest req, HttpSession session, HashMap outData) throws Exception {
        if(sAction.equals("get_units")){
            outData.put("head", "Магазины");
            //outData.put("head", addStrParameter("title","Магазины") );
            try {
                String sTableName = "r_Stocks";
                DMMetadata.newMetadata(sTableName, "ChID", ftype_INTEGER)
                        .addField("id", sTableName, "StockID", ftype_INTEGER)
                        .addFieldFunction("short_name", "", null, "REPLACE(StockName,'Магазин IN UA ','')")
                        .addField("name", sTableName, "StockName", ftype_STRING)
                        .addWhereCondition("StockID",">",0)
                        .selectList(getSessionDBUS(session)).putResultListTo(outData, "units");
            } catch (Exception e){
                outData.put("error",e.getLocalizedMessage());
            }
        } else if(sAction.equals("get_cashbalance")){
            try {
                HashMap<String,String> params = getReqParams(req);
                String sBDate = params.get("bdate");
                String sEDate = params.get("edate");
//                String sTableName = "if_SelectCRBalance(1)";
                ArrayList<HashMap<String,Object>> res =
                        getSessionDBUS(session)
                                .getDataListFromSQLQuery(
                                        "select * from if_SelectCRBalance(?,?,?)"
                                        ,new Object[]{1, sBDate, sEDate}
                                        ,addStrParameter("ItemID","id",
                                                addStrParameter("ItemName","label",
                                                        addStrParameter("SumCC","value"))) );
                res.add(0,
                        DMMetadata.newMetadata("t_Sale", "ChID", ftype_INTEGER)
                                .cloneWithoutFields()
                                .addFieldFunction("value", FUNC_SUMNOTNULL, null, "TSumCC_wt")
                                .addWhereCondition("DocDate", ">=", sBDate)
                                .addWhereCondition("DocDate", "<=", sEDate)
                                .selectList(getSessionDBUS(session))
                                .putToSelectResultItemValue("id", 0)
                                .putToSelectResultItemValue("label","Реализация")
                                .getSelectResultItemValues() );
                outData.put("cashbalance",res);
            } catch (Exception e){
                outData.put("error",e.getLocalizedMessage());
            }
        } else return false;
        return true;
    }
}
