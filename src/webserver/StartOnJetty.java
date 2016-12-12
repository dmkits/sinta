package webserver;

import core.AppCoreLogger;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.JavaUtilLog;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.LogManager;

/** Main starting app with jetty server.
 * @author DMKITS @version 2.2 2015-09-07
 */
public class StartOnJetty {
    
    protected static StartOnJetty startOnJetty = null;

    protected static Logger log = null;
    protected Server server = null;
    protected static String WEBROOT_PATH = "web";//web content in work dir
    public static String MODE = "production";
    
    /* main of start or stop Jetty server.
    */
    public static void main(String[] args) throws Throwable {
        if (startOnJetty!=null) return;
        int serverport = 8080;
        if (args.length!=0) {
            try {
                serverport = Integer.parseInt(args[0]);
            } catch (Exception e) {
                System.out.println("Parameter error!");
            }
        }
        if (args.length>1) MODE = args[1];
        StartOnJetty start = new StartOnJetty();
        start.initJettyLog();
        start.startServer(serverport); 
        //start.serverJoinInNewThread();//отключено за ненадобностью
        if (startOnJetty==null) { startOnJetty = start; }
        System.out.println("SERVER STARTED at port "+serverport+"!");
        String s = "";
        Scanner in = new Scanner(System.in);
        while (!s.equals("e")) {
            System.out.println("For stop server and exit input \"e\",");
            System.out.println("For restart server input \"r\".");
            System.out.print("Input:");
            s = in.nextLine();
            if (s.equals("r")) {
                System.out.println("Stopping server...");
                startOnJetty.stopServer();
                System.out.println("Server stoped.");
                System.out.println("Starting server...");
                startOnJetty.startServer(serverport); 
                //start.serverJoinInNewThread();//отключено за ненадобностью
                System.out.println("SERVER STARTED at port "+serverport+"!");
            }
        }
        System.out.println("Stopping server and exit...");
        startOnJetty.stopServer();
        System.out.println("Server stoped.");
    }
    
    public StartOnJetty() throws Throwable {
        AppCoreLogger.Init();// initing main core logger
        log = Logger.getLogger(StartOnJetty.class);
    }
    
    protected void initJettyLog() {
        //Jetty log initing
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource("logging.properties");
        if (url != null) {
            try(InputStream in = url.openStream()) {
                LogManager.getLogManager().readConfiguration(in);
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
        Log.setLog(new JavaUtilLog());
    }
    
    protected void startServer(int port) throws Throwable {
        server = new Server(port);
        // Setup JMX
        /*MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
        server.addBean(mbContainer);*/
        server.setHandler(getWebappcontext()); // set server context
        try {/*start server*/
            server.start();
        } catch (Throwable t) {
            log.fatal("Cannot starting server!",t);
            server.stop();
            throw new Throwable(t);
        } finally {
            log.debug("Server state: "+server.getState());
        }
    }
    
    protected void stopServer() throws Exception { server.stop(); }
    
    /* create context from web-app 
    */
    protected WebAppContext getWebappcontext() throws Exception {
        String webappPath = WEBROOT_PATH;
        String contextPath = "/";
        WebAppContext webapp = new WebAppContext(webappPath, contextPath);
        //???webapp.addAliasCheck(new AllowSymLinkAliasChecker());//неизвестно зачем!!!

        // This webapp will use jsps and jstl. We need to enable the
        // AnnotationConfiguration in order to correctly set up the jsp container
        Configuration.ClassList classlist = Configuration.ClassList.setServerDefault(server);
        classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration" );
        
        // Set the ContainerIncludeJarPattern so that jetty examines these
        // container-path jars for tlds, web-fragments etc.
        // If you omit the jar that contains the jstl .tlds, the jsp engine will
        // scan for them instead.
        webapp.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$" );
         return webapp;
    }
    
    protected void serverJoinInNewThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server.join();
                } catch (Exception e) {
                    log.fatal("Cannot join server!",e);
                    throw new RuntimeException(e);
                }
            }
        }, "Stop Jetty Hook");
    }
}
