package core;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

/** Main application logger.
 * @author DMKITS version 2.0 2015-07-17
 */
public class AppCoreLogger {
    
    private static Logger log = null;

    public static void Init() throws IOException {
        File l4jp = new File("log4j.properties");
        if ( ! l4jp.exists() ) l4jp = new File("log4j.xml");

        if ( l4jp.exists() ) PropertyConfigurator.configure(l4jp.getName());
        else {
            Properties log4jpp = new Properties();
            log4jpp.load(AppCoreLogger.class.getClassLoader().getResourceAsStream("log4j.properties"));
            PropertyConfigurator.configure(log4jpp);
        }
        log = Logger.getLogger(AppCoreLogger.class);
        log.debug("core.AppCoreLogger inited.");
    }
    
    public static void log(Object logobj, String note) { log(logobj.getClass(), note); }

    public static void log(Class logclass, String note) {
        Logger l = Logger.getLogger(logclass);
        l.debug(note);
    }
    
    public static void logReqInfo(Object logobj, String note, HttpServletRequest req) {
        Logger l = Logger.getLogger(logobj.getClass());;
        l.debug(note+" Request: "+req.getRequestURI());
        for (Enumeration e = req.getParameterNames(); e.hasMoreElements();) {
            String paramName = (String) e.nextElement();
            l.debug("Request parameter "+paramName+"="+req.getParameter(paramName));
        }
    }
}
