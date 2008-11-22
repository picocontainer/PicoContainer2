/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.remoting;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Properties;
import java.util.Collection;

import org.picocontainer.PicoContainer;
import org.picocontainer.Characteristics;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.injectors.MethodInjection;
import org.picocontainer.injectors.Reinjector;
import org.picocontainer.injectors.ProviderAdapter;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PicoWebRemoting {

    private XStream xStreamNoRoot = new XStream(new JsonHierarchicalStreamDriver(false));
    private XStream xStreamWithRoot = new XStream(new JsonHierarchicalStreamDriver(true));
    private Map<String, Object> paths = new HashMap<String, Object>();

    private final String toStripFromUrls;
    private final String scopesToPublish;

    public PicoWebRemoting(String toStripFromUrls, String scopesToPublish) {

        this.toStripFromUrls = toStripFromUrls;
        this.scopesToPublish = scopesToPublish;
    }

    protected String processRequest(String pathInfo, PicoContainer reqContainer) throws IOException {
        String path = pathInfo.substring(1);
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        path = toStripFromUrls + path;

        Object node = paths.get(path);

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
                        node = reinject(methodName, method, methods.getComponent(), reqContainer);
                    } else {
                        node = null;
                    }
                }
            } else {
                node = null;
            }
        }

        if (node instanceof Directories) {
            Directories directories = (Directories) node;
            return xStreamNoRoot.toXML(directories.toArray()) + "\n";
        } else if (node instanceof WebMethods) {
            WebMethods methods = (WebMethods) node;
            return xStreamNoRoot.toXML(methods.keySet().toArray()) + "\n";
        } else if (node != null && isComposite(node)) {
            return xStreamNoRoot.toXML(node) + "\n";
        } else if (node != null) {
            return node != null ? xStreamWithRoot.toXML(node) + "\n" : null;
        } else {
            return null;
        }
    }

    public void publishAdapters(Collection<ComponentAdapter<?>> adapters, String scope) {
        if (scopesToPublish == null || scopesToPublish.contains(scope)) {
            for (ComponentAdapter<?> ca : adapters) {
                boolean notAProvider = ca.findAdapterOfType(ProviderAdapter.class) == null;
                Object key = ca.getComponentKey();
                if (notAProvider && key != HttpSession.class
                        && key != HttpServletRequest.class
                        && key != HttpServletResponse.class) {
                    publishAdapter(key);
                }
            }
        }
    }

    private static void determineElibibleMethods(Class<?> component, WebMethods webMethods) {
        Method[] methods = component.getDeclaredMethods();
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers()))
                webMethods.put(method.getName(), method);
        }
        Class<?> superClass = component.getSuperclass();
        if (superClass != Object.class) {
            determineElibibleMethods(superClass, webMethods);
        }
    }


    private void publishAdapter(Object key) {
        Class<?> component = (Class<?>) key;
        String path = component.getName().replace('.', '/');
        if (toStripFromUrls != "" || path.startsWith(toStripFromUrls)) {
            paths.put(path, key);
            directorize(paths, path, component);
            directorize(paths, path);
        }
    }

    protected static void directorize(Map<String, Object> paths, String path, Class<?> comp) {
        WebMethods webMethods = new WebMethods(comp);
        paths.put(path, webMethods);
        determineElibibleMethods(comp, webMethods);
    }

    public String errorResult(RuntimeException e) {
        return xStreamNoRoot.toXML(new ErrorReply(e.getMessage())) + "\n";
    }

    private static class ErrorReply {
        private boolean ERROR = true;
        private String message;

        private ErrorReply(String message) {
            this.message = message;
        }
    }

    private boolean isComposite(Object node) {
        return !(node.getClass().isPrimitive() || node instanceof Boolean
                || node instanceof Long || node instanceof Double
                || node instanceof Short || node instanceof Byte
                || node instanceof Integer || node instanceof String
                || node instanceof Float || node instanceof Character);
    }

    private Object reinject(String methodName, Method method, Class<?> component, PicoContainer reqContainer) throws IOException {
        MethodInjection methodInjection = new MethodInjection(method);
        Reinjector reinjector = new Reinjector(reqContainer);
        Properties props = (Properties) Characteristics.USE_NAMES.clone();
        Object rv = reinjector.reinject(component, component, reqContainer.getComponent(component), props, methodInjection);
        if (method.getReturnType() == void.class) {
            return "OK";
        }
        return rv;
    }


    @SuppressWarnings("unchecked")
    protected static void directorize(Map<String, Object> paths, String path) {
        int lastSlashIx = path.lastIndexOf("/");
        if (lastSlashIx != -1) {
            String dir = path.substring(0, lastSlashIx);
            String file = path.substring(lastSlashIx + 1);
            Set<String> dirs = (Set<String>) paths.get(dir);
            if (dirs == null) {
                dirs = new Directories();
                paths.put(dir, dirs);
            }
            dirs.add(file);
            directorize(paths, dir);
        } else {
            Set<String> dirs = (Set<String>) paths.get("/");
            if (dirs == null) {
                dirs = new Directories();
                paths.put("", dirs);
            }
            dirs.add(path);
        }
    }

    public static class Directories extends HashSet<String> {
    }

    public static class WebMethods extends HashMap<String, Method> {
        private final Class<?> component;

        public WebMethods(Class<?> component) {
            this.component = component;
        }

        public Class<?> getComponent() {
            return component;
        }
    }

}
