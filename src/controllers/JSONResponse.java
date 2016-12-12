package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by dmkits on 12.02.16.
 */
public class JSONResponse {
    String errormsg;
    String target;
    public JSONResponse() { }
    public JSONResponse(String errormsg) { this.errormsg = errormsg; }
    public JSONResponse(String errormsg, String target) { this.errormsg = errormsg; this.target = target; }

    public void doHttpResponse(HttpServletResponse resp) throws IOException {
        String json = (new Gson()).toJson(this);
        doHttpResponse(resp,json);
    }
    public static void doHttpResponse(HttpServletResponse resp, String jsonResp) throws IOException {
        resp.setHeader("X-Requested-With","application/json; charset=utf-8");//or XMLHttpRequest
        resp.setContentType("application/x-www-form-urlencoded");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(jsonResp);
    }
    public static void doHttpResponse(HttpServletResponse resp, Object jsonData) throws IOException {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        String json = gson.toJson(jsonData);
        JSONResponse.doHttpResponse(resp, json);
    }
    static void doXMLHttpResponse(HttpServletResponse resp, String jsonResp) throws IOException {
        resp.setHeader("X-Requested-With","XMLHttpRequest");
        resp.setContentType("application/x-www-form-urlencoded");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(jsonResp);
    }
    static void doXMLHttpResponse(HttpServletResponse resp, Object jsonData) throws IOException {
        JSONResponse.doXMLHttpResponse(resp, (new Gson()).toJson(jsonData));
    }
}
