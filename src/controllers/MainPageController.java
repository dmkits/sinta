package controllers;

import model.core.DataModelException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

/**
 * Created by dmkits on 29.03.16.
 */
public class MainPageController extends PageController {

    public MainPageController() { sURL="/mainpage"; sPAGE_URL="/pages/mainpage.html"; }

    @Override
    protected boolean doGetAction(String sAction, HttpServletRequest req, HttpSession session, HashMap data) throws Exception {
        return super.doGetAction(sAction, req, session, data);
    }

    @Override
    protected boolean doPostAction(String sAction, HttpServletRequest req, HttpSession session, HashMap data) throws Exception {
        return super.doPostAction(sAction, req, session, data);
    }
}
