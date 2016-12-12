package controllers;

import core.AppCoreLogger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by dmkits on 09.08.16.
 */
public class PrintController extends PageController {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AppCoreLogger.logReqInfo(this, "goGet:", req);
        String sAction = req.getParameter("action");
        if("prod_tags".equals(sAction)){
            forvardTo("/pages/print/print_tags.html", req, resp);
            return;
        }
        forvardTo(sERRORPAGE_URL, req, resp);
    }
}
