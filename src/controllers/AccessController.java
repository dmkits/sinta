package controllers;

import core.AppLocalConfig;
import model.DataModel;
import model.core.DBUserSession;
import webserver.StartOnJetty;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by dmkits on 14.04.16.
 */
public class AccessController implements Filter {

    static final String APP_LOCAL_CONFIG_ERROR_MSG = "Не удалось загрузить настройки экземпляра!";
    public static final String DB_VALIDATE_ERROR_MSG = "Database validate error!";
    public static AppLocalConfig appLocalConfig = null;
    public static String appLocalConfigMode = "application.mode";
    public static String ErrorMsg = null;
    public static String ValidateErrorMsg = null;
    public static final String DB_SESSION = "db_session";
    public static final String USER_ROLE = "user_role";
    public static final String USER_ROLE_CASHIER = "cashier";
    public static final String USER_ROLE_ADMIN = "admin";
    static {
        appLocalConfig = new AppLocalConfig(StartOnJetty.MODE);
        try {
            appLocalConfig.load();
        } catch (IOException e) {
            ErrorMsg = new String(APP_LOCAL_CONFIG_ERROR_MSG);
        }
        DBUserSession dbus = null;
        try {
            DataModel.init();
            dbus = new DBUserSession(appLocalConfig.getProperties());
            DataModel.instance().validate(dbus);
            dbus.close();
        } catch (Exception e) {
            try { if (dbus!=null) dbus.close(); } catch (Exception e1) {}
            ValidateErrorMsg = new String(DB_VALIDATE_ERROR_MSG+" Reason:"+e.getMessage());
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpSession session = ((HttpServletRequest)servletRequest).getSession();
        if (session.getAttribute(DB_SESSION)==null&&appLocalConfig!=null) {
            try {
                DBUserSession dbus = new DBUserSession(appLocalConfig.getProperties());
                session.setAttribute(DB_SESSION, dbus);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }

        session.setAttribute(USER_ROLE, USER_ROLE_ADMIN);
//        if (session.getAttribute(USER_ROLE)==null) {
//            String sUser = servletRequest.getParameter("usr");
//            String sUserPass = servletRequest.getParameter("pswrd");
//            if("admin".equals(sUser)&&"bataadmin".equals(sUserPass)){
//                session.setAttribute(USER_ROLE, USER_ROLE_ADMIN);
//            } else {
//                session.setAttribute(USER_ROLE, USER_ROLE_CASHIER);
//            }
//        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
