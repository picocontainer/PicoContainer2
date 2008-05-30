package org.picocontainer.web.nanoweb;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface Dispatcher {
    void dispatch(ServletContext servletContext, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String scriptPathWithoutExtension, String actionMethod, String result) throws IOException, ServletException;
}