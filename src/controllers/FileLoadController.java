package controllers;

import core.AppCoreLogger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by dmkits on 26.01.16.
 */
public class FileLoadController implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest sreq, ServletResponse sresp, FilterChain filterChain) throws IOException, ServletException {
        AppCoreLogger.logReqInfo(this, "doFilter: goto FileLoader to loading file:", (HttpServletRequest) sreq);
        // forvard to FileLoader without AccessController
        sreq.getRequestDispatcher( ((HttpServletRequest)sreq).getRequestURI() ).forward(sreq, sresp);
    }
}
