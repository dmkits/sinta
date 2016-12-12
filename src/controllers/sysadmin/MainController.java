package controllers.sysadmin;

import controllers.PageController;
import model.core.DBUserSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

/**
 * Created by dmkits on 15.04.16.
 */
public class MainController extends PageController {
    DBUserSession dbus = null;

    public MainController() { sURL="/sysadmin/main"; sPAGE_URL="/pages/sysadmin/main.html"; }

    @Override
    protected boolean doGetAction(String sAction, HttpServletRequest req, HttpSession session, HashMap data) {
        if (sAction.equals("get_")) {
        } else return false;
        return true;
    }

    @Override
    protected boolean doPostAction(String sAction, HttpServletRequest req, HttpSession session, HashMap data) {
        if ("load_app_local_config".equals(sAction)) {
        } else return false;
        return true;
    }
}
