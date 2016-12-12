package controllers;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by dmkits on 12.02.16.
 */
public class JSONRequest {
    static final String JSONHeaderAtr = "X-Requested-With";
    static boolean isJSON(HttpServletRequest req) {
        String sH = req.getHeader(JSONHeaderAtr); sH = (sH==null) ? "":sH;
        String sCT = req.getContentType(); sCT = (sCT==null) ?"":sCT;
        if (sH.contains("application/json")&&sCT.contains("application/x-www-form-urlencoded")) { return true; }
        return false;
    }
    static boolean isAJAX(HttpServletRequest req) {
        String sH = req.getHeader(JSONHeaderAtr); sH = (sH==null) ? "":sH;
        String sCT = req.getContentType(); sCT = (sCT==null) ?"":sCT;
        if (sH.contains("XMLHttpRequest")&&sCT.contains("application/x-www-form-urlencoded")) { return true; }
        return false;
    }
}
