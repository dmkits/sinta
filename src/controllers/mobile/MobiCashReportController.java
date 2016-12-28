package controllers.mobile;

import controllers.PageController;
import model.core.DMData;
import model.core.DMMetadata;
import model.core.DMSimpleQuery;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static model.core.DMBase.*;
import static model.core.DMData.*;
import static model.core.DMField.*;

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
                DMSimpleQuery.instance(
                        "select 'sales'+CAST(st.StockID as varchar(200)) as ID, st.StockName, st.StockID  AS STOCK_ID, " +
                                " REPLACE(st.StockName,'Магазин IN UA ','Реализация ') as SHORT_NAME, COALESCE(SUM(TSumCC_wt),0) as SALE_SUM "
                                + "FROM r_Stocks st "
                                + " LEFT JOIN t_Sale s ON s.StockID=st.StockID AND DocDate BETWEEN  ? AND ? "
                                + " WHERE st.StockID " + sUnitsCondition
                                + "group by st.StockID, st.StockName")

                        .setParameter(sBDate).setParameter(sEDate)
                        .select(getSessionDBUS(session))
                        .replaceResultItemName("SALE_SUM", "value")
                        .replaceResultItemName("SHORT_NAME", "label")
                        .replaceResultItemName("STOCK_ID", "unit_id")
                        .replaceResultItemName("ID", "id")
                        .addToAllResultItems("action", "sales_detail_view")
                        .addToAllResultItems("url", "/mobile")
                        .addResultToList(outData, "items");

                DMSimpleQuery.instance(
                        "select 'returns'+CAST(st.StockID as varchar(200)) as ID, st.StockName,st.StockID  AS STOCK_ID," +
                                " REPLACE(st.StockName,'Магазин IN UA ','Возвраты ') as SHORT_NAME, COALESCE(SUM(TSumCC_wt),0) as RET_SUM "
                                + "FROM r_Stocks st "
                                + " LEFT JOIN t_CRRet r ON r.StockID=st.StockID AND DocDate BETWEEN  ? AND ? "
                                + " WHERE st.StockID " + sUnitsCondition
                                + "group by st.StockID, st.StockName")

                        .setParameter(sBDate).setParameter(sEDate)
                        .select(getSessionDBUS(session))
                        .replaceResultItemName("RET_SUM", "value")
                        .replaceResultItemName("SHORT_NAME", "label")
                        .replaceResultItemName("STOCK_ID", "unit_id")
                        .replaceResultItemName("ID", "id")
                        .addToAllResultItems("action", "returns_detail_view")
                        .addToAllResultItems("url", "/mobile")
                        .addResultToList(outData, "items");

                DMSimpleQuery.instance("SELECT 'cash_income'+CAST(st.StockID as varchar(200)) as ID, st.StockName, st.StockID AS STOCK_ID," +
                        "REPLACE(st.StockName,'Магазин IN UA ','Выручка НАЛ ') as SHORT_NAME, SUM(m.SumCC_wt) as SUMM\n" +
                        "FROM r_Stocks st\n" +
                        "  LEFT JOIN (\n" +
                        "   SELECT sales.StockID, COALESCE(SUM(pays.SumCC_wt), 0) as SumCC_wt\n" +
                        "    FROM t_SalePays pays\n" +
                        "    INNER JOIN t_Sales sales ON sales.ChID = pays.ChID\n" +
                        "    WHERE sales.StockID " + sUnitsCondition + " AND pays.PayformCode = 1 AND sales.DocDate BETWEEN  ? AND ?" +
                        "    GROUP BY sales.StockID\n" +
                        "    UNION ALL\n" +
                        "    SELECT crr.StockID, COALESCE(SUM(-crpays.SumCC_wt), 0) as SumCC_wt\n" +
                        "    FROM t_CRRetPays crpays\n" +
                        "    INNER JOIN t_CRRet crr ON crr.ChID = crpays.ChID\n" +
                        "    WHERE crr.StockID " + sUnitsCondition + " AND crpays.PayformCode =1 AND crr.DocDate BETWEEN  ? AND ?" +
                        "    GROUP BY crr.StockID\n" +
                        "    ) m on    m.StockID = st.StockID\n" +
                        "  WHERE st.StockID \n" + sUnitsCondition +
                        "    GROUP BY st.StockID, st.StockName")
                        .setParameter(sBDate).setParameter(sEDate).setParameter(sBDate).setParameter(sEDate)
                        .select(getSessionDBUS(session))
                        .replaceResultItemName("SUMM", "value")
                        .replaceResultItemName("SHORT_NAME", "label")
                        .replaceResultItemName("STOCK_ID", "unit_id")
                        .replaceResultItemName("ID", "id")
                        .addToAllResultItems("action", "cash_detail_view")
                        .addToAllResultItems("url", "/mobile")
                        .addResultToList(outData, "items");

                DMSimpleQuery.instance("SELECT 'card_income'+CAST(st.StockID as varchar(200)) as ID, st.StockName,  st.StockID AS STOCK_ID," +
                        "REPLACE(st.StockName,'Магазин IN UA ','Выручка ПК ') as SHORT_NAME, SUM(m.SumCC_wt) as SUMM\n" +
                        "FROM r_Stocks st\n" +
                        "  LEFT JOIN (\n" +
                        "   SELECT sales.StockID, COALESCE(SUM(pays.SumCC_wt), 0) as SumCC_wt\n" +
                        "    FROM t_SalePays pays\n" +
                        "    INNER JOIN t_Sales sales ON sales.ChID = pays.ChID\n" +
                        "    WHERE sales.StockID " + sUnitsCondition + " AND pays.PayformCode = 2 AND sales.DocDate BETWEEN  ? AND ?" +
                        "    GROUP BY sales.StockID\n" +
                        "    UNION ALL\n" +
                        "    SELECT crr.StockID, COALESCE(SUM(-crpays.SumCC_wt), 0) as SumCC_wt\n" +
                        "    FROM t_CRRetPays crpays\n" +
                        "    INNER JOIN t_CRRet crr ON crr.ChID = crpays.ChID\n" +
                        "    WHERE crr.StockID  " + sUnitsCondition + "  AND crpays.PayformCode =2 AND crr.DocDate BETWEEN  ? AND ?" +
                        "    GROUP BY crr.StockID\n" +
                        "    ) m on    m.StockID = st.StockID\n" +
                        "  WHERE st.StockID  " + sUnitsCondition +
                        "    GROUP BY st.StockID, st.StockName")
                        .setParameter(sBDate).setParameter(sEDate).setParameter(sBDate).setParameter(sEDate)
                        .select(getSessionDBUS(session))
                        .replaceResultItemName("SUMM", "value")
                        .replaceResultItemName("SHORT_NAME", "label")
                        .replaceResultItemName("STOCK_ID", "unit_id")
                        .replaceResultItemName("ID", "id")
                        .addToAllResultItems("action", "cash_detail_view")
                        .addToAllResultItems("url", "/mobile")
                        .addResultToList(outData, "items");

                DMSimpleQuery.instance("SELECT 'else_income'+CAST(st.StockID as varchar(200)) as ID, st.StockName,  st.StockID AS STOCK_ID," +
                        "REPLACE(st.StockName,'Магазин IN UA ','Выручка прочее ') as SHORT_NAME, SUM(m.SumCC_wt) as SUMM\n" +
                        "FROM r_Stocks st\n" +
                        "  LEFT JOIN (\n" +
                        "   SELECT sales.StockID, COALESCE(SUM(pays.SumCC_wt), 0) as SumCC_wt\n" +
                        "    FROM t_SalePays pays\n" +
                        "    INNER JOIN t_Sales sales ON sales.ChID = pays.ChID\n" +
                        "    WHERE sales.StockID " + sUnitsCondition + " AND NOT pays.PayformCode in (1,2) AND sales.DocDate BETWEEN  ? AND ?" +
                        "    GROUP BY sales.StockID\n" +
                        "    UNION ALL\n" +
                        "    SELECT crr.StockID, COALESCE(SUM(-crpays.SumCC_wt), 0) as SumCC_wt\n" +
                        "    FROM t_CRRetPays crpays\n" +
                        "    INNER JOIN t_CRRet crr ON crr.ChID = crpays.ChID\n" +
                        "    WHERE crr.StockID " + sUnitsCondition + " AND NOT crpays.PayformCode in (1,2) AND crr.DocDate BETWEEN  ? AND ?" +
                        "    GROUP BY crr.StockID\n" +
                        "    ) m on    m.StockID = st.StockID\n" +
                        "  WHERE st.StockID  " + sUnitsCondition +
                        "    GROUP BY st.StockID, st.StockName")
                        .setParameter(sBDate).setParameter(sEDate).setParameter(sBDate).setParameter(sEDate)
                        .select(getSessionDBUS(session))
                        .replaceResultItemName("SUMM", "value")
                        .replaceResultItemName("SHORT_NAME", "label")
                        .replaceResultItemName("STOCK_ID", "unit_id")
                        .replaceResultItemName("ID", "id")
                        .addToAllResultItems("action", "other_detail_view")
                        .addToAllResultItems("url", "/mobile")
                        .addResultToList(outData, "items");
            } else {
                DMSimpleQuery.instance("select COALESCE(SUM(TSumCC_wt),0) as SALE_SUM from t_Sale where DocDate BETWEEN ?  AND ? AND StockID " + sUnitsCondition)
                        .setParameter(sBDate).setParameter(sEDate)
                        .select(getSessionDBUS(session))
                        .replaceResultItemName("SALE_SUM", "value")
                        .addToResultItem("label", "Реализация")
                        .addToResultItem("id", "sales")
                        .addToResultItem("url", "/mobile")
                        .addToResultItem("action", "sales_detail_view")
                        .addResultItemToList(outData, "items");

                DMSimpleQuery.instance("select COALESCE(SUM(TSumCC_wt),0) as RET_SUM from t_CRRet where DocDate BETWEEN ?  AND ? AND StockID " + sUnitsCondition)
                        .setParameter(sBDate).setParameter(sEDate)
                        .select(getSessionDBUS(session))
                        .replaceResultItemName("RET_SUM", "value")
                        .addToResultItem("label", "Возвраты")
                        .addToResultItem("id", "returns")
                        .addToResultItem("url", "/mobile")
                        .addToResultItem("action", "returns_detail_view")
                        .addResultItemToList(outData, "items");

                DMSimpleQuery.instance("SELECT (\n" +
                        "SELECT COALESCE(SUM(pays.SumCC_wt),0)\n" +
                        "FROM t_SalePays pays\n" +
                        "INNER JOIN t_Sales sales ON sales.ChID=pays.ChID\n" +
                        "WHERE pays.PayformCode=1 AND sales.DocDate BETWEEN ?  AND ? AND sales.StockID " + sUnitsCondition + ")\n" +
                        "-(SELECT COALESCE(sum(pays.SumCC_wt),0)\n" +
                        "FROM t_CRRetPays pays\n" +
                        "INNER JOIN t_CRRet returns ON returns.ChID=pays.ChID\n" +
                        "WHERE pays.PayformCode=1 AND returns.DocDate BETWEEN ?  AND ? AND returns.StockID " + sUnitsCondition + " ) as CASH_INCOME")
                        .setParameter(sBDate).setParameter(sEDate).setParameter(sBDate).setParameter(sEDate)
                        .select(getSessionDBUS(session))
                        .replaceResultItemName("CASH_INCOME", "value")
                        .addToResultItem("label", "Выручка нал")
                        .addToResultItem("action", "cash_detail_view")
                        .addToResultItem("id", "cash_income")
                        .addToResultItem("url", "/mobile")
                        .addResultItemToList(outData, "items");

                DMSimpleQuery.instance("SELECT (\n" +
                        "SELECT COALESCE(SUM(pays.SumCC_wt),0)\n" +
                        "FROM t_SalePays pays\n" +
                        "INNER JOIN t_Sales sales ON sales.ChID=pays.ChID\n" +
                        "WHERE pays.PayformCode=2 AND sales.DocDate BETWEEN ?  AND ?  AND sales.StockID " + sUnitsCondition + ")\n" +
                        " -(SELECT COALESCE(sum(pays.SumCC_wt),0)\n" +
                        "FROM t_CRRetPays pays\n" +
                        "INNER JOIN t_CRRet returns ON returns.ChID=pays.ChID\n" +
                        "WHERE pays.PayformCode=2 AND returns.DocDate BETWEEN ?  AND ? AND returns.StockID " + sUnitsCondition + "  ) as CARD_INCOME")
                        .setParameter(sBDate).setParameter(sEDate).setParameter(sBDate).setParameter(sEDate)
                        .select(getSessionDBUS(session))
                        .replaceResultItemName("CARD_INCOME", "value")
                        .addToResultItem("label", "Выручка ПК")
                        .addToResultItem("action", "card_detail_view")
                        .addToResultItem("id", "card_income")
                        .addToResultItem("url", "/mobile")
                        .addResultItemToList(outData, "items");

                DMSimpleQuery.instance("SELECT COALESCE( " +
                        "  ( SELECT SUM(pays.SumCC_wt) " +
                        "FROM t_SalePays pays " +
                        "  INNER JOIN t_Sales sales ON sales.ChID=pays.ChID " +
                        " WHERE NOT pays.PayformCode in (1,2) " +
                        "AND sales.DocDate BETWEEN ?  AND ?  AND sales.StockID " + sUnitsCondition + ") " +
                        "  - (SELECT sum(pays.SumCC_wt) " +
                        " FROM t_CRRetPays pays " +
                        "   INNER JOIN t_CRRet returns ON returns.ChID=pays.ChID " +
                        " WHERE pays.PayformCode!=2 AND pays.PayformCode!=1 " +
                        "       AND returns.DocDate BETWEEN ?  AND ?  AND returns.StockID " + sUnitsCondition + "),0) as OTHER_INCOME")
                        .setParameter(sBDate).setParameter(sEDate).setParameter(sBDate).setParameter(sEDate)
                        .select(getSessionDBUS(session))
                        .replaceResultItemName("OTHER_INCOME", "value")
                        .addToResultItem("label", "Выручка прочее")
                        .addToResultItem("action", "other_detail_view")
                        .addToResultItem("id", "other_income")
                        .addToResultItem("url", "/mobile")
                        .addResultItemToList(outData, "items");
            }


        } else if (sAction.equals("sales_detail_view")) {
            HashMap<String, String> params = getReqParams(req);
            String sBDate = params.get("bdate");
            String sEDate = params.get("edate");
            String sUnitsCondition = getUnitsCondition(params);
            DMSimpleQuery.instance("select pc.PCatName as BRAND, sum(sd.SumCC_wt) as SUM\n" +
                    "from t_SaleD sd\n" +
                    "INNER JOIN t_Sale s ON s.ChID=sd.ChID\n" +
                    "INNER JOIN r_Prods p ON p.ProdID=sd.ProdID\n" +
                    "INNER JOIN r_ProdC pc on pc.PCatID=p.PCatID\n" +
                    "WHERE s.DocDate BETWEEN ? AND ?  AND s.StockID " + sUnitsCondition +
                    "GROUP BY pc.PCatName\n" +
                    "ORDER BY sum DESC")
                    .setParameter(sBDate).setParameter(sEDate)
                    .select(getSessionDBUS(session))
                    .replaceResultItemName("SUM", "value")
                    .replaceResultItemName("BRAND", "label")
                    .addResultToList(outData, "items");
        } else if (sAction.equals("returns_detail_view")) {
            HashMap<String, String> params = getReqParams(req);
            String sBDate = params.get("bdate");
            String sEDate = params.get("edate");
            String sUnitsCondition = getUnitsCondition(params);
            DMSimpleQuery.instance("select pc.PCatName as BRAND, sum(rd.SumCC_wt) as SUM\n" +
                    "from t_CRRetD rd\n" +
                    "INNER JOIN t_CRRet r ON r.ChID=rd.ChID\n" +
                    "INNER JOIN r_Prods p ON p.ProdID=rd.ProdID\n" +
                    "INNER JOIN r_ProdC pc on pc.PCatID=p.PCatID\n" +
                    "WHERE r.DocDate BETWEEN ? AND ? AND r.StockID " + sUnitsCondition +
                    "GROUP BY pc.PCatName\n" +
                    "ORDER BY sum DESC")
                    .setParameter(sBDate).setParameter(sEDate)
                    .select(getSessionDBUS(session))
                    .replaceResultItemName("SUM", "value")
                    .replaceResultItemName("BRAND", "label")
                    .addResultToList(outData, "items");
        } else if (sAction.equals("cash_detail_view")) {
            HashMap<String, String> params = getReqParams(req);
            String sBDate = params.get("bdate");
            String sEDate = params.get("edate");
            String sUnitsCondition = getUnitsCondition(params);
            DMSimpleQuery.instance("SELECT SUM(pays.SumCC_wt) AS CASH_SALE_SUM " +
                    "FROM t_SalePays pays " +
                    "  INNER JOIN t_Sales sales ON sales.ChID=pays.ChID " +
                    "WHERE pays.PayformCode=1 " +
                    "AND sales.DocDate BETWEEN  ?  AND ?  AND sales.StockID " + sUnitsCondition)
                    .setParameter(sBDate).setParameter(sEDate)
                    .select(getSessionDBUS(session))
                    .replaceResultItemName("CASH_SALE_SUM", "value")
                    .addToResultItem("label", "Продажи нал")
                    .addResultItemToList(outData, "items");

            DMSimpleQuery.instance("SELECT sum(pays.SumCC_wt)AS CASH_RETURN_SUM " +
                    "FROM t_CRRetPays pays " +
                    "  INNER JOIN t_CRRet returns ON returns.ChID=pays.ChID " +
                    "WHERE pays.PayformCode=1 " +
                    "      AND returns.DocDate BETWEEN  ?  AND ?  AND returns.StockID " + sUnitsCondition)
                    .setParameter(sBDate).setParameter(sEDate)
                    .select(getSessionDBUS(session))
                    .replaceResultItemName("CASH_RETURN_SUM", "value")
                    .addToResultItem("label", "Возвраты нал")
                    .addResultItemToList(outData, "items"); //t_monIntRec

            DMSimpleQuery.instance("SELECT sum(SumCC)AS CASH_IN_SUM " +
                    "FROM t_monIntRec r\n" +
                    " INNER JOIN r_Crs cr ON cr.CRID = r.CRID\n" +
                    "WHERE r.DocDate BETWEEN ? AND ? AND cr.StockID " + sUnitsCondition)
                    .setParameter(sBDate).setParameter(sEDate)
                    .select(getSessionDBUS(session))
                    .replaceResultItemName("CASH_IN_SUM", "value")
                    .addToResultItem("label", "Вносы нал")
                    .addResultItemToList(outData, "items");

            DMSimpleQuery.instance("SELECT sum(SumCC)AS CASH_OUT_SUM " +
                    "FROM t_monIntExp e\n" +
                    " INNER JOIN r_Crs cr ON cr.CRID = e.CRID\n" +
                    "WHERE e.DocDate BETWEEN ? AND ? AND cr.StockID " + sUnitsCondition)
                    .setParameter(sBDate).setParameter(sEDate)
                    .select(getSessionDBUS(session))
                    .replaceResultItemName("CASH_OUT_SUM", "value")
                    .addToResultItem("label", "Выносы нал")
                    .addResultItemToList(outData, "items");
        } else if (sAction.equals("card_detail_view")) {
            HashMap<String, String> params = getReqParams(req);
            String sBDate = params.get("bdate");
            String sEDate = params.get("edate");
            String sUnitsCondition = getUnitsCondition(params);
            DMSimpleQuery.instance("SELECT SUM(pays.SumCC_wt) AS CARD_SALE_SUM " +
                    "FROM t_SalePays pays " +
                    "  INNER JOIN t_Sales sales ON sales.ChID=pays.ChID " +
                    "WHERE pays.PayformCode=2 " +
                    "AND sales.DocDate BETWEEN  ?  AND ?  AND sales.StockID " + sUnitsCondition)
                    .setParameter(sBDate).setParameter(sEDate)
                    .select(getSessionDBUS(session))
                    .replaceResultItemName("CARD_SALE_SUM", "value")
                    .addToResultItem("label", "Продажи ПК")
                    .addResultItemToList(outData, "items");

            DMSimpleQuery.instance("SELECT sum(pays.SumCC_wt)AS CARD_RETURN_SUM " +
                    "FROM t_CRRetPays pays " +
                    "  INNER JOIN t_CRRet returns ON returns.ChID=pays.ChID " +
                    "WHERE pays.PayformCode=2 " +
                    "      AND returns.DocDate BETWEEN  ?  AND ?  AND returns.StockID " + sUnitsCondition)
                    .setParameter(sBDate).setParameter(sEDate)
                    .select(getSessionDBUS(session))
                    .replaceResultItemName("CARD_RETURN_SUM", "value")
                    .addToResultItem("label", "Возвраты ПК")
                    .addResultItemToList(outData, "items");
        } else if (sAction.equals("other_detail_view")) {
            HashMap<String, String> params = getReqParams(req);
            String sBDate = params.get("bdate");
            String sEDate = params.get("edate");
            String sUnitsCondition = getUnitsCondition(params);
            DMSimpleQuery.instance("SELECT SUM(pays.SumCC_wt) AS OTHER_SALE_SUM " +
                    "FROM t_SalePays pays " +
                    "  INNER JOIN t_Sales sales ON sales.ChID=pays.ChID " +
                    "WHERE pays.PayformCode NOT in (1,2) " +
                    "AND sales.DocDate BETWEEN  ?  AND ?  AND sales.StockID " + sUnitsCondition)
                    .setParameter(sBDate).setParameter(sEDate)
                    .select(getSessionDBUS(session))
                    .replaceResultItemName("OTHER_SALE_SUM", "value")
                    .addToResultItem("label", "Продажи прочее")
                    .addResultItemToList(outData, "items");

            DMSimpleQuery.instance("SELECT sum(pays.SumCC_wt)AS OTHER_RETURN_SUM " +
                    "FROM t_CRRetPays pays " +
                    "  INNER JOIN t_CRRet returns ON returns.ChID=pays.ChID " +
                    "WHERE pays.PayformCode NOT in (1,2) " +
                    "      AND returns.DocDate BETWEEN  ?  AND ?  AND returns.StockID " + sUnitsCondition)
                    .setParameter(sBDate).setParameter(sEDate)
                    .select(getSessionDBUS(session))
                    .replaceResultItemName("OTHER_RETURN_SUM", "value")
                    .addToResultItem("label", "Возвраты прочее")
                    .addResultItemToList(outData, "items");
        } else return false;
        return true;
    }
    private String getUnitsCondition(HashMap<String, String> params) {
        String sUnitsCondition, sUnitsList = null;
        Iterator<String> iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            String sParamName = iterator.next();
            if (sParamName.indexOf("unit_") >= 0)
                sUnitsList = (sUnitsList == null) ? params.get(sParamName).toString() : sUnitsList + "," + params.get(sParamName).toString();
        }
        return (sUnitsList == null) ? "is Null" : "in (" + sUnitsList + ")";
    }
}
