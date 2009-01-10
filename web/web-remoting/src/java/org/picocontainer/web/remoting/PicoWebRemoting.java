/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.remoting;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.extended.ISO8601DateConverter;
import com.thoughtworks.xstream.io.json.JsonWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.WriterWrapper;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import java.io.IOException;
import java.io.Writer;
import java.io.Reader;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.picocontainer.PicoCompositionException;
import org.picocontainer.web.GET;
import org.picocontainer.web.POST;
import org.picocontainer.web.DELETE;
import org.picocontainer.web.NONE;
import org.picocontainer.web.PUT;
import org.picocontainer.injectors.MethodInjection;
import org.picocontainer.injectors.Reinjector;
import org.picocontainer.injectors.ProviderAdapter;
import org.picocontainer.injectors.SingleMemberInjector;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PicoWebRemoting {

    private final XStream xStream;
    private final String toStripFromUrls;
    private final String scopesToPublish;

    private Map<String, Object> paths = new HashMap<String, Object>();

    public PicoWebRemoting(String toStripFromUrls, String scopesToPublish, XStream xStream) {
        this.toStripFromUrls = toStripFromUrls;
        this.scopesToPublish = scopesToPublish;
        this.xStream = xStream;
        this.xStream.registerConverter(new ISO8601DateConverter());
    }

    public Map<String, Object> getPaths() {
        return paths;
    }

    protected String processRequest(String pathInfo, PicoContainer reqContainer, String httpMethod) throws IOException {
        try {
            String path = pathInfo.substring(1);
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            path = toStripFromUrls + path;

            Object node = getNode(reqContainer, httpMethod, path);

            if (node instanceof Directories) {
                Directories directories = (Directories) node;
                return xStream.toXML(directories.toArray()) + "\n";
            } else if (node instanceof WebMethods) {
                WebMethods methods = (WebMethods) node;
                return xStream.toXML(methods.keySet().toArray()) + "\n";
            } else if (node != null && isComposite(node)) {
                return xStream.toXML(node) + "\n";
            } else if (node != null) {
                return node != null ? xStream.toXML(node) + "\n" : null;
            } else {
                throw makeNothingMatchingException();
            }

        } catch (SingleMemberInjector.ParameterCannotBeNullException e) {
            // TODO monitor
            return xStream.toXML(new ErrorReply("Parameter '" + e.getParameterName()+ "' missing")) + "\n";
        } catch (PicoCompositionException e) {
            // TODO monitor
            return errorResult(e);
        } catch (RuntimeException e) {
            // TODO monitor
            return errorResult(e);
        }

    }

    private RuntimeException makeNothingMatchingException() {
        return new RuntimeException("Nothing matches the path requested");
    }

    private Object getNode(PicoContainer reqContainer, String httpMethod, String path) throws IOException {
        Object node = paths.get(path);

        if (node == null) {
            int ix = path.lastIndexOf('/');
            if (ix > 0) {
                String methodName = path.substring(ix + 1);
                path = path.substring(0, ix);
                Object node2 = paths.get(path);
                if (node2 instanceof WebMethods) {
                    node = processWebMethodRequest(reqContainer, httpMethod, methodName, node2);
                }
            } else {
                node = null;
            }
        }
        return node;
    }

    private Object processWebMethodRequest(PicoContainer reqContainer, String verb, String methodName, Object node2) throws IOException {
        WebMethods methods = (WebMethods) node2;
        if (!methods.containsKey(methodName)) {
            throw makeNothingMatchingException();
        }
        Method method = methods.get(methodName);
        String verbs = post(method) + get(method) + put(method) + delete(method);
        if (!verbs.equals("") && !verbs.contains(verb)) {
            throw new RuntimeException("method not allowed for " + verb);
        }
        return reinject(methodName, method, methods.getComponent(), reqContainer);
    }

    private String delete(Method method) {
        return method.getAnnotation(DELETE.class) != null ? "DELETE," : "";
    }

    private String put(Method method) {
        return method.getAnnotation(PUT.class) != null ? "PUT," : "";
    }

    private String get(Method method) {
        return method.getAnnotation(GET.class) != null ? "GET," : "";
    }

    private String post(Method method) {
        return method.getAnnotation(POST.class) != null ? "POST," : "";
    }

    protected void publishAdapters(Collection<ComponentAdapter<?>> adapters, String scope) {
        if (scopesToPublish == null || scopesToPublish.contains(scope)) {
            for (ComponentAdapter<?> ca : adapters) {
                Object key = ca.getComponentKey();
                if (notAProvider(ca) && notServletMechanics(key) && keyIsAType(key)) {
                    publishAdapter((Class<?>) key);
                }
            }
        }
    }

    private boolean notAProvider(ComponentAdapter<?> ca) {
        return ca.findAdapterOfType(ProviderAdapter.class) == null;
    }

    private boolean keyIsAType(Object key) {
        return key instanceof Class;
    }

    protected boolean notServletMechanics(Object key) {
        return key != HttpSession.class
                && key != HttpServletRequest.class
                && key != HttpServletResponse.class;
    }

    private void determineEligibleMethods(Class<?> component, WebMethods webMethods) {
        Method[] methods = component.getDeclaredMethods();
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())
                    && !Modifier.isStatic(method.getModifiers())
                    && method.getAnnotation(NONE.class) == null
                    ) {
                webMethods.put(method.getName(), method);
            }
        }
        Class<?> superClass = component.getSuperclass();
        if (superClass != Object.class) {
            determineEligibleMethods(superClass, webMethods);
        }
    }

    private void publishAdapter(Class<?> key) {
        String path = key.getName().replace('.', '/');
        if (toStripFromUrls != "" || path.startsWith(toStripFromUrls)) {
            paths.put(path, key);
            directorize(path, key);
            directorize(path);
        }
    }

    protected void directorize(String path, Class<?> comp) {
        WebMethods webMethods = new WebMethods(comp);
        paths.put(path, webMethods);
        determineEligibleMethods(comp, webMethods);
    }

    private String errorResult(RuntimeException e) {
        return xStream.toXML(new ErrorReply(e.getMessage())) + "\n";
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
        Object inst = reqContainer.getComponent(component);
        Object rv = reinjector.reinject(component, component, inst, props, methodInjection);
        if (method.getReturnType() == void.class) {
            return "OK";
        }
        return rv;
    }

    @SuppressWarnings("unchecked")
    protected void directorize(String path) {
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
            directorize(dir);
        } else {
            Set<String> dirs = (Set<String>) paths.get("/");
            if (dirs == null) {
                dirs = new Directories();
                paths.put("", dirs);
            }
            dirs.add(path);
        }
    }

    @SuppressWarnings("serial")
	private static class Directories extends HashSet<String> {
    }

    @SuppressWarnings("serial")
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
