/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/

package org.picocontainer.web.call;

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
import org.picocontainer.injectors.Reinjector;
import org.picocontainer.web.PicoServletContainerFilter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;

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
public class PicoCallServlet extends HttpServlet {

    private Map<String, Object> paths = new HashMap<String, Object>();
    private XStream xStream = new XStream(new JettisonMappedXmlDriver());

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

    protected void service(HttpServletRequest req,
                           HttpServletResponse resp)
            throws ServletException, IOException {

        if (!initialized) {
            initialize();
            initialized = true;
        }

        String path = req.getPathInfo().substring(1);
        resp.setContentType("text/plain");
        Object node = paths.get(path);
        ServletOutputStream out = resp.getOutputStream();

        if (node == null) {
            int ix = path.lastIndexOf('/');
            if (ix > 0) {
                executeMethod(path, ix, out);
            } else {
                out.print("*nothing*\n");
            }
        } else {
            out.print("" + node);
        }

    }

    private void executeMethod(String path, int ix, ServletOutputStream out) throws IOException {
        String methodName = path.substring(ix + 1);
        path = path.substring(0, ix);
        WebMethods methods = (WebMethods) paths.get(path);
        Method method = methods.get(methodName);
        PicoContainer reqContainer = ServletFilter.getRequestContainerForThread();
        Reinjector reinjector = new Reinjector(reqContainer);
        Object o = reinjector.reinject(methods.getComp(), method);
        out.print(xStream.toXML(o) + "\n");
    }

    private void initialize() {
        PicoContainer reqContainer = ServletFilter.getRequestContainerForThread();
        Collection<ComponentAdapter<?>> adapters = reqContainer.getComponentAdapters();
        for (ComponentAdapter<?> ca : adapters) {
            Object key = ca.getComponentKey();
            Class comp = (Class) key;
            String path = comp.getName().replace('.', '/');
            paths.put(path, key);
            directorize(paths, path, comp);
            directorize(paths, path);
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
