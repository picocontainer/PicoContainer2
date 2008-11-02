/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/

package org.picocontainer.web.remoting;

import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.io.IOException;
import java.lang.reflect.Method;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.injectors.Reinjector;
import org.picocontainer.web.PicoServletContainerFilter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletConfig;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

/**
 * All for the calling of methods in a tree of components manages by PicoContainer.
 * JSON is the nature of the reply, the request is plainly mapped from Query Strings
 * and Form params to the method signature.
 *
 * @author Paul Hammant
 */
@SuppressWarnings("serial")
public class PicoWebRemotingServlet extends HttpServlet {

    private Map<String, Object> paths = new HashMap<String, Object>();
    private XStream xStream = new XStream(new JettisonMappedXmlDriver());
    private String prefix;
    private String toPublish;

    public static class ServletFilter extends PicoServletContainerFilter {
        private static ThreadLocal<MutablePicoContainer> currentRequestContainer = new ThreadLocal<MutablePicoContainer>();
        private static ThreadLocal<MutablePicoContainer> currentSessionContainer = new ThreadLocal<MutablePicoContainer>();
        private static ThreadLocal<MutablePicoContainer> currentAppContainer = new ThreadLocal<MutablePicoContainer>();

        protected void setAppContainer(MutablePicoContainer container) {
            currentAppContainer.set(container);
        }

        protected void setRequestContainer(MutablePicoContainer container) {
            currentRequestContainer.set(container);
        }

        protected void setSessionContainer(MutablePicoContainer container) {
            currentSessionContainer.set(container);
        }

        protected static MutablePicoContainer getRequestContainerForThread() {
            return currentRequestContainer.get();
        }

        protected static MutablePicoContainer getSessionContainerForThread() {
            return currentSessionContainer.get();
        }

        protected static MutablePicoContainer getApplicationContainerForThread() {
            return currentAppContainer.get();
        }
    }

    private boolean initialized;

    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!initialized) {
            initialize();
            initialized = true;
        }
        resp.setContentType("text/plain");
        ServletOutputStream out = resp.getOutputStream();

        String pathInfo = req.getPathInfo();

        processRequest(out, pathInfo);


    }

    protected void processRequest(ServletOutputStream out, String pathInfo) throws IOException {
        String path = pathInfo.substring(1);
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        path = prefix + path;


        Object node = paths.get(path);

        System.out.println("--> hit " + path + " " + node);

        if (node == null) {
            int ix = path.lastIndexOf('/');
            if (ix > 0) {
                String methodName = path.substring(ix + 1);
                path = path.substring(0, ix);
                Object node2 = paths.get(path);
                if (node2 instanceof WebMethods) {
                    WebMethods methods = (WebMethods) node2;
                    Method method = methods.get(methodName);
                    if (method != null) {
                        Object o = reinject(out, methodName, method, methods.getComp());
                        node = xStream.toXML(o) + "\n";
                    } else {
                        node = null;
                    }
                }
            } else {
                node = null;
            }
        }

        out.print(node == null ? "*nothing*\n" : "" + node);
    }

    private Object reinject(ServletOutputStream out, String methodName, Method method, Class component) throws IOException {
        PicoContainer reqContainer = ServletFilter.getRequestContainerForThread();
        return new Reinjector(reqContainer).reinject(component, method);
    }

    public void init(ServletConfig servletConfig) throws ServletException {
        prefix = servletConfig.getInitParameter("prefix");
        if (prefix == null) {
            prefix = "";
        } else if (!prefix.endsWith("/")) {
            throw new PicoCompositionException("Prefix must end with '/' char");
        }

        toPublish = servletConfig.getInitParameter("toPublish");
        super.init(servletConfig);
    }

    private void initialize() {

        if (toPublish == null || toPublish.contains("request")) {

            Collection<ComponentAdapter<?>> adapters = ServletFilter.getRequestContainerForThread().getComponentAdapters();
            publishAdapters(adapters);
        }

        if (toPublish != null && toPublish.contains("session")) {
            Collection<ComponentAdapter<?>> adapters = ServletFilter.getSessionContainerForThread().getComponentAdapters();
            publishAdapters(adapters);
        }
    }

    private void publishAdapters(Collection<ComponentAdapter<?>> adapters) {
        for (ComponentAdapter<?> ca : adapters) {

            Object key = ca.getComponentKey();
            Class comp = (Class) key;
            String path = comp.getName().replace('.', '/');
            if (prefix != "" || path.startsWith(prefix)) {
                //path = path.substring(prefix.length());
                paths.put(path, key);
                directorize(paths, path, comp);
                directorize(paths, path);
            }
        }
    }

    protected static void directorize(Map paths, String path, Class comp) {
        Method[] methods = comp.getDeclaredMethods();
        WebMethods webMethods = new WebMethods(comp);
        paths.put(path, webMethods);
        for (Method method : methods) {
            webMethods.put(method.getName(), method);
        }
    }

    protected static void directorize(Map paths, String path) {
        int lastSlashIx = path.lastIndexOf("/");
        if (lastSlashIx != -1) {
            String dir = path.substring(0, lastSlashIx);
            String file = path.substring(lastSlashIx + 1);
            Set set = (Set) paths.get(dir);
            if (set == null) {
                set = new Directories();
                paths.put(dir, set);
            }
            set.add(file);
            directorize(paths, dir);
        }
    }

    public static class Directories extends HashSet<String> {
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (String st : (Iterable<String>) this) {
                sb.append(st).append("\n");
            }
            return sb.toString();
        }
    }

    public static class WebMethods extends HashMap<String, Method> {
        private final Class comp;

        public WebMethods(Class comp) {
            this.comp = comp;
        }

        public Class getComp() {
            return comp;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            Iterator<String> stringIterator = (Iterator<String>) this.keySet().iterator();
            while (stringIterator.hasNext()) {
                sb.append(stringIterator.next()).append("\n");
            }
            return sb.toString();
        }
    }
}
