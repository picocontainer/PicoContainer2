/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.remoting;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Member;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.web.PicoServletContainerFilter;

import com.thoughtworks.xstream.XStream;

/**
 * Abstract Servlet used for the calling of methods in a tree of components managed by PicoContainer.
 * The form of the reply is determined by the XStream implementation passed into the constructor,
 * the request is plainly mapped from Query Strings and form fields to the method signature.
 *
 * @author Paul Hammant
 */
@SuppressWarnings("serial")
public abstract class AbstractPicoWebRemotingServlet extends HttpServlet {

    private XStream xstream;
    private PicoWebRemoting pwr;
    private String mimeType = "text/plain";
    private PicoWebRemotingMonitor monitor;

    private static final String APPLICATION_SCOPE = "application";
    private static final String SESSION_SCOPE = "session";
    private static final String REQUEST_SCOPE = "request";
    private static final String SCOPES_TO_PUBLISH = "scopes_to_publish";
    private static final String PACKAGE_PREFIX_TO_STRIP = "package_prefix_to_strip";
    private static final String SUFFIX_TO_STRIP = "suffix_to_strip";
    private static final String MIME_TYPE = "mime_type";
    private static final String LOWER_CASE_PATH = "lower_case_path";
    private static final String USE_METHOD_NAME_PREFIXES_FOR_VERBS = "use_method_name_prefixes_for_verbs";

    private static ThreadLocal<MutablePicoContainer> currentRequestContainer = new ThreadLocal<MutablePicoContainer>();
    private static ThreadLocal<MutablePicoContainer> currentSessionContainer = new ThreadLocal<MutablePicoContainer>();
    private static ThreadLocal<MutablePicoContainer> currentAppContainer = new ThreadLocal<MutablePicoContainer>();

    Map foo = new HashMap();

    public static class ServletFilter extends PicoServletContainerFilter {

        protected void setAppContainer(MutablePicoContainer container) {
            currentAppContainer.set(container);
        }

        protected void setRequestContainer(MutablePicoContainer container) {
            currentRequestContainer.set(container);
        }

        protected void setSessionContainer(MutablePicoContainer container) {
            currentSessionContainer.set(container);
        }
    }

    public static class Struts1ServletFilter
            extends org.picocontainer.web.struts.PicoActionFactory.ServletFilter {
        protected void setAppContainer(MutablePicoContainer container) {
            super.setAppContainer(container);
            currentAppContainer.set(container);
        }

        protected void setRequestContainer(MutablePicoContainer container) {
            super.setRequestContainer(container);
            currentRequestContainer.set(container);
        }

        protected void setSessionContainer(MutablePicoContainer container) {
            super.setSessionContainer(container);
            currentSessionContainer.set(container);
        }
    }

    public static class Struts2ServletFilter
            extends org.picocontainer.web.struts2.PicoObjectFactory.ServletFilter {
        protected void setAppContainer(MutablePicoContainer container) {
            super.setAppContainer(container);
            currentAppContainer.set(container);
        }

        protected void setRequestContainer(MutablePicoContainer container) {
            super.setRequestContainer(container);
            currentRequestContainer.set(container);
        }

        protected void setSessionContainer(MutablePicoContainer container) {
            super.setSessionContainer(container);
            currentSessionContainer.set(container);
        }
    }

    public static class WebWork1ServletFilter
            extends org.picocontainer.web.webwork.PicoActionFactory.ServletFilter {
        protected void setAppContainer(MutablePicoContainer container) {
            super.setAppContainer(container);
            currentAppContainer.set(container);
        }

        protected void setRequestContainer(MutablePicoContainer container) {
            super.setRequestContainer(container);
            currentRequestContainer.set(container);
        }

        protected void setSessionContainer(MutablePicoContainer container) {
            super.setSessionContainer(container);
            currentSessionContainer.set(container);
        }
    }

    public static class WebWork2ServletFilter
            extends org.picocontainer.web.webwork2.PicoObjectFactory.ServletFilter {
        protected void setAppContainer(MutablePicoContainer container) {
            super.setAppContainer(container);
            currentAppContainer.set(container);
        }

        protected void setRequestContainer(MutablePicoContainer container) {
            super.setRequestContainer(container);
            currentRequestContainer.set(container);
        }

        protected void setSessionContainer(MutablePicoContainer container) {
            super.setSessionContainer(container);
            currentSessionContainer.set(container);
        }
    }

    private boolean initialized;

    protected abstract XStream createXStream();
    
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long b4 = System.currentTimeMillis();

        if (!initialized) {
            publishAdapters();
            initialized = true;
        }

        respond(req, resp, req.getPathInfo());
        Logger.getAnonymousLogger().info("total AbstractPicoWebRemotingServlet.service() time = " + (System.currentTimeMillis() - b4) + "ms");
    }

    protected void respond(HttpServletRequest req, HttpServletResponse resp, String pathInfo) throws IOException {
        resp.setContentType(mimeType);

        final String httpMethod = req.getMethod();

        final String[] cacheKey = new String[1];
        final String[] cached = new String[1];
        final long[] time = new long[1];

        long str = System.currentTimeMillis();

        String result = pwr.processRequest(pathInfo, currentRequestContainer.get(), httpMethod, new NullComponentMonitor() {
                            public Object invoking(PicoContainer container, ComponentAdapter<?> componentAdapter, Member member, Object instance, Object[] args) {
                                if (httpMethod.equals("GET")) {
                                    StringBuilder sb = new StringBuilder().append("[").append(instance.toString()).append("].").append(member.getName());
                                    parmsString(sb, args);
                                    cacheKey[0] = sb.toString();
                                    cached[0] = (String) foo.get(cacheKey[0]);
                                    if (cached[0] != null) {
                                        time[0] = System.currentTimeMillis();
                                        return null;
                                    }
                                }
                                time[0] = System.currentTimeMillis();
                                return ComponentMonitor.KEEP;
                            }

                            public void invoked(PicoContainer container, ComponentAdapter<?> componentAdapter, Member member, Object instance, long duration, Object[] args, Object retVal) {
                                Logger.getAnonymousLogger().info("method duration = " + duration + "ms ");
                            }
                        });

        String duration = ", duration = " + (System.currentTimeMillis() - str) + "ms ";

        if (httpMethod.equals("GET")) {
            if (cached[0] != null) {
                Logger.getAnonymousLogger().info("cached" + duration);
                result = cached[0];
            } else {
                Logger.getAnonymousLogger().info("not cached" + duration);
                foo.put(cacheKey[0], result);
            }
        }

        ServletOutputStream outputStream = resp.getOutputStream();
        if (result != null) {
            outputStream.print(result);
        } else {
            resp.sendError(400, "Nothing is mapped to this URL, try removing the last term for directory list.");
        }
    }

    private void parmsString(StringBuilder sb, Object[] parms) {
        for (int i = 0; i < parms.length; i++) {
            Object parm = parms[i];
            sb.append(" ").append(i).append(":").append(parm == null ? "**null**" : parm.toString());
        }
    }


    public void init(ServletConfig servletConfig) throws ServletException {
    	this.xstream = createXStream();
        String packagePrefixToStrip = servletConfig.getInitParameter(PACKAGE_PREFIX_TO_STRIP);
        String prefixToStripFromUrls;
        if (packagePrefixToStrip == null) {
            prefixToStripFromUrls = "";
        } else {
            prefixToStripFromUrls = packagePrefixToStrip.replace('.', '/') + "/";
        }

        String suffixToStrip = servletConfig.getInitParameter(SUFFIX_TO_STRIP);

        String scopesToPublish = servletConfig.getInitParameter(SCOPES_TO_PUBLISH);
        if (scopesToPublish == null) {
            scopesToPublish = "";
        }
        String mimeTypeFromConfig = servletConfig.getInitParameter(MIME_TYPE);
        if (mimeTypeFromConfig != null) {
            mimeType = mimeTypeFromConfig;
        }

        String lowerCasePathStr = servletConfig.getInitParameter(LOWER_CASE_PATH);
        boolean lowerCasePath;
        if (lowerCasePathStr == null) {
            lowerCasePath = false;
        } else {
            lowerCasePath = lowerCasePathStr.toLowerCase().equals(Boolean.TRUE.toString());
        }

        String useMethodNamePrefixesForVerbsStr = servletConfig.getInitParameter(USE_METHOD_NAME_PREFIXES_FOR_VERBS);
        boolean useMethodNamePrefixesForVerbs;
        if (useMethodNamePrefixesForVerbsStr == null) {
            useMethodNamePrefixesForVerbs = true;
        } else {
            useMethodNamePrefixesForVerbs = lowerCasePathStr.toLowerCase().equals(Boolean.TRUE.toString());
        }

        super.init(servletConfig);
        pwr = new PicoWebRemoting(xstream, prefixToStripFromUrls, suffixToStrip, scopesToPublish, lowerCasePath, useMethodNamePrefixesForVerbs);
    }

    private void publishAdapters() {
        pwr.publishAdapters(currentRequestContainer.get().getComponentAdapters(), REQUEST_SCOPE);
        pwr.publishAdapters(currentSessionContainer.get().getComponentAdapters(), SESSION_SCOPE);
        pwr.publishAdapters(currentAppContainer.get().getComponentAdapters(), APPLICATION_SCOPE);

        monitor = currentAppContainer.get().getComponent(PicoWebRemotingMonitor.class);
        if (monitor == null) {
            monitor = new NullPicoWebRemotingMonitor();
        }
        pwr.setMonitor(monitor);
    }

    protected void visitClass(String clazz, MethodVisitor mapv) throws IOException {
        pwr.visitClass(clazz, currentRequestContainer.get(), mapv);
    }

}
