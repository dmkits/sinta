package controllers;

import model.core.DBUserSession;
import model.core.DataModelException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * Created by dmkits on 26.01.16.
 */
public class MainController extends PageController {

    private static String sMOBI_MAIN_PAGE_URI = "/mobile/main.html";

    public MainController() { sPAGE_URL="/pages/main.html"; }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sMobi = req.getParameter("mobi");
        if (sMobi!=null || req.getHeader("User-Agent").indexOf("Mobi") != -1) {
            forvardTo(sMOBI_MAIN_PAGE_URI, req, resp); return;
        }
        super.doGet(req, resp);
    }

    @Override
    protected boolean doGetAction(String sAction, HttpServletRequest req, HttpSession session, HashMap outData) {
        if("get_data".equals(sAction)){
            setModeTo(outData);
            DBUserSession dbus= null;
            boolean no_db_work_session = false;
            try {
                dbus = getSessionDBUS(session);
            } catch (DataModelException e) {
                no_db_work_session= true;
            }
            String sUserRole = (String)session.getAttribute(AccessController.USER_ROLE);
            if(AccessController.USER_ROLE_ADMIN.equals(sUserRole)){
                outData.put("title", "BATA");
            } else if (AccessController.USER_ROLE_CASHIER.equals(sUserRole)) {
                outData.put("title", "BATA for " + sUserRole);
            }
            outData.put("user", sUserRole);
            HashMap<String,Object[]> userMenuItems = getUserMenuAccessItems(sUserRole);
            outData.put("menuBar", getUserMainMenu(no_db_work_session, userMenuItems));
            outData.put("autorun", getAutorun(no_db_work_session, userMenuItems));
        } else return false;
        return true;
    }

    private HashMap<String, Object[]> getUserMenuAccessItems(String sUserRole) {
        HashMap<String, Object[]> menuItems = new HashMap<>();/*menuItemName, closable*/
        if ("admin".equals(sUserRole)){
            menuItems.put("menuBarItemMainPage",flags(false,1));
            menuItems.put("menuItemWRH_Order_Bata-",flags(true,0));
            menuItems.put("menuItemWRH_PInvoice-",flags(true,0));
            menuItems.put("menuBarWRH",flags(true,0));
            menuItems.put("menuItemWRH_GoodsCurBalance",flags(true,0));
            menuItems.put("menuItemWRH_GoodsMoventment",flags(true,0));
            menuItems.put("separator_1wrh",flags(true,0));
            menuItems.put("menuItemWRH_Order",flags(true,0));
            menuItems.put("menuItemWRH_Order_Bata",flags(true,0));
            menuItems.put("separator_2wrh",flags(true,0));
            menuItems.put("menuItemWRH_PInvoice",flags(true,3));
            menuItems.put("menuItemWRH_RetPInvoice",flags(true,0));
            menuItems.put("menuItemRetailReports",flags(true,0));

            menuItems.put("menuBarWRHPrice",flags(true,0));
            menuItems.put("menuItemWRH_RetailPriceRevalution",flags(true,0));

            menuItems.put("menuBarItemDIR",flags(true,0));
            menuItems.put("menuItemDIR_Units",flags(true,0));
            menuItems.put("menuItemDIR_Contractors",flags(true,0));
            menuItems.put("separator_1dir",flags(true,0));
            menuItems.put("menuItemDIR_Products",flags(true,0));
            menuItems.put("menuBarItemSYS",flags(true,0));
            menuItems.put("menuItemSYS_DocsSettings",flags(true,0));
            menuItems.put("menuItemSYS_Product_settings",flags(true,0));

        } else if ("cashier".equals(sUserRole)){
            menuItems.put("menuItemRetailReports",flags(false,1));
        }
        menuItems.put("menuBarItemClose", null);
        menuItems.put("menuBarItemHelpAbout", null);
        return menuItems;
    }

    private ArrayList<HashMap<String,Object>> getUserMainMenu(boolean no_db_work_session, HashMap<String,Object[]> userMenuItems) {
        ArrayList<HashMap<String,Object>> menuBar = new ArrayList<>();
        ArrayList<HashMap<String,Object>> pmenu = null;
        if (AccessController.ErrorMsg==null&& AccessController.ValidateErrorMsg==null&&!no_db_work_session) {
            setMenuItem(menuBar, userMenuItems, "menuBarItemMainPage", "Главная страница", "open", "/mainpage", "main_page", "Главная страница");
            setMenuItem(menuBar, userMenuItems, "menuItemWRH_Order_Bata-", "Заказы (BATA)", "open", "/wrh/order_bata", "wrh_order_bata", "Заказы (BATA)");//!!!IT'S FOR TEST!!!
            setMenuItem(menuBar, userMenuItems, "menuItemWRH_PInvoice-", "Приходные накладные", "open", "/wrh/p_invoice", "wrh_p_invoice", "Приходные накладные");//!!!IT'S FOR TEST!!!

            pmenu= setPopupMenuItem(menuBar, userMenuItems, "menuBarWRH", "Склад");
            setMenuItem(pmenu, userMenuItems, "menuItemWRH_GoodsCurBalance", "Текущие остатки товаров",
                    "open", "/wrh/goods_cur_balance", "wrh_goods_cur_balance", "Текущие остатки товаров");
            setMenuItem(pmenu, userMenuItems, "menuItemWRH_GoodsMoventment", "Движение товаров",
                    "open", "/wrh/goods_moventment", "wrh_goods_moventment", "Движение товаров");
            setMenuItem(pmenu, userMenuItems, "separator_1wrh");
            setMenuItem(pmenu, userMenuItems, "menuItemWRH_Order", "Заказы поставщикам", "open", "/wrh/order", "wrh_order", "Заказы поставщикам");
            setMenuItem(pmenu, userMenuItems, "menuItemWRH_Order_Bata", "Заказы (BATA)", "open", "/wrh/order_bata", "wrh_order_bata", "Заказы (BATA)");
            setMenuItem(pmenu, userMenuItems, "separator_2wrh");
            setMenuItem(pmenu, userMenuItems, "menuItemWRH_PInvoice", "Приходные накладные",
                    "open", "/wrh/p_invoice", "wrh_p_invoice", "Приходные накладные");
            setMenuItem(pmenu, userMenuItems, "menuItemWRH_RetPInvoice", "Возвратные накладные",
                    "open", "/wrh/ret_p_invoice", "wrh_ret_p_invoice", "Возвратные накладные");

            pmenu= setPopupMenuItem(menuBar, userMenuItems, "menuBarWRHPrice", "Цены");
            setMenuItem(pmenu, userMenuItems, "menuItemWRH_RetailPriceRevalution", "Переоценка розничных цен",
                    "open", "/wrh/retail_price_revalution", "wrh_retail_price_revalution", "Переоценка розничных цен");

            setMenuItem(menuBar, userMenuItems, "menuItemRetailReports", "Отчеты Retail", "open", "/reports/retail", "wrh_retail_reports", "Отчеты Retail");

            pmenu= setPopupMenuItem(menuBar, userMenuItems, "menuBarItemDIR", "Справочники");
            setMenuItem(pmenu, userMenuItems, "menuItemDIR_Units", "Подразделения", "open", "/dir/units", "dir_Units", "Подразделения");
            setMenuItem(pmenu, userMenuItems, "menuItemDIR_Contractors", "Контрагенты", "open", "/dir/contractors", "dir_Contractors", "Контрагенты");
            setMenuItem(pmenu, userMenuItems, "separator_1dir");
            setMenuItem(pmenu, userMenuItems, "menuItemDIR_Products", "Товарные номенклатуры", "open", "/dir/products", "dir_Products", "Товарные номенклатуры");
            pmenu= setPopupMenuItem(menuBar, userMenuItems, "menuBarItemSYS", "Настройки");
            setMenuItem(pmenu, userMenuItems, "menuItemSYS_DocsSettings", "Настройки документов",
                    "open", "/sys/documents_settings", "sys_documents_settings", "Настройки документов");
            setMenuItem(pmenu, userMenuItems, "menuItemSYS_Product_settings", "Настройки номенклатур",
                    "open", "/sys/product_settings", "sys_prod_settings", "Настройки номенклатур");
        }
        setMenuItem(menuBar, userMenuItems, "menuBarItemClose", "Выход", "close");
        setMenuItem(menuBar, userMenuItems, "menuBarItemHelpAbout", "О программе", "help_about");
        return menuBar;
    }

    private ArrayList<HashMap<String,Object>> getAutorun(boolean no_db_work_session, HashMap<String,Object[]> userMenuItems) {
        ArrayList<HashMap<String,Object>> autorun = new ArrayList<>();
        HashMap<String,Object> autorunitem;
        if (AccessController.ErrorMsg!=null|| AccessController.ValidateErrorMsg!=null||no_db_work_session) {
            autorun.add(autorunitem = new HashMap<String, Object>());
            setItemValues(autorunitem, atr("runaction", "1"), atr("action", "open"), atr("id", "sys_error"),
                    atr("title", "Системная ошибка"), atr("content", "/pages/syserror.html")); autorunitem.put("closable", false);
            return autorun;
        }
        TreeMap<Integer,String> autorunItems = new TreeMap<>();
        Iterator<String> it = userMenuItems.keySet().iterator();
        while (it.hasNext()){
            String userMenuItemName = it.next();
            Object[] flags = userMenuItems.get(userMenuItemName);
            if (flags!=null && ((Integer)flags[1]).intValue()>0 ){
                autorunItems.put((Integer)flags[1], userMenuItemName);
            }
        }
        Iterator<Integer> autorunIterator = autorunItems.keySet().iterator();
        while (autorunIterator.hasNext()){
            autorun.add(autorunitem=new HashMap<String, Object>()); autorunitem.put("menuitem",autorunItems.get(autorunIterator.next()));
        }
        return autorun;
    }

    private HashMap<String,Object> setMenuItem(ArrayList<HashMap<String,Object>> menuBar, HashMap<String,Object[]> userMenuItems, String itemName){
        if (!userMenuItems.containsKey(itemName)) return null;
        HashMap<String,Object> menuBarItem;
        menuBar.add(menuBarItem = new HashMap<String, Object>());
        setItemValues(menuBarItem, atr("itemname", itemName));
        return menuBarItem;
    }
    private HashMap<String,Object> setMenuItem(ArrayList<HashMap<String,Object>> menuBar, HashMap<String,Object[]> userMenuItems, String itemName, String itemTitle, String action){
        HashMap<String,Object> menuBarItem= setMenuItem(menuBar, userMenuItems, itemName);
        if (menuBarItem==null) return null;
        setItemValues(menuBarItem, atr("itemtitle", itemTitle), atr("action", action));
        return menuBarItem;
    }
    private void setMenuItem(ArrayList<HashMap<String,Object>> menuBar, HashMap<String,Object[]> userMenuItems, String itemName, String itemTitle, String action,
                             String content, String contentID, String contentTitle){
        HashMap<String,Object> menuBarItem = setMenuItem(menuBar, userMenuItems, itemName, itemTitle, action);
        if (menuBarItem==null) return;
        setItemValues(menuBarItem, atr("content", content), atr("id", contentID), atr("title", contentTitle));
        boolean closable = true;
        Object[] flags = userMenuItems.get(itemName);
        if (flags!=null && !((Boolean)flags[0]).booleanValue() ) closable = false;
        menuBarItem.put("closable", closable);
    }
    private ArrayList<HashMap<String,Object>> setPopupMenuItem(ArrayList<HashMap<String,Object>> menuBar, HashMap<String,Object[]> userMenuItems, String itemName, String itemTitle){
        if (!userMenuItems.containsKey(itemName)) return null;
        HashMap<String,Object> menuBarItem;
        menuBar.add(menuBarItem = new HashMap<String, Object>());
        setItemValues(menuBarItem, atr("itemname", itemName), atr("itemtitle", itemTitle));
        ArrayList<HashMap<String,Object>> pmenu;
        menuBarItem.put("menuPopup", pmenu = new ArrayList<>());
        return pmenu;
    }
    private void setItemValues(HashMap<String,Object> pItem, String[]... pValues) {
        for (String[] item : pValues) { pItem.put(item[0],item[1]); }
    }
    private String[] atr(String key,String val) { return new String[]{key,val}; }
    private Object[] flags(boolean closable, int autorunIndex){
        return new Object[]{Boolean.valueOf(closable), Integer.valueOf(autorunIndex)};
    }

    @Override
    protected boolean doPostAction(String sAction, HttpServletRequest req, HttpSession session, HashMap data) throws Exception {
        if ("exit".equals(sAction)) {
            session.invalidate();
            data.put("actionResult","successfull");
            return true;
        } else return super.doPostAction(sAction, req, session, data);
    }

}
