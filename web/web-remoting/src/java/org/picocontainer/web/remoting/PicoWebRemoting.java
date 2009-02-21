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
import org.picocontainer.PicoCompositionException;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.web.GET;
import org.picocontainer.web.POST;
import org.picocontainer.web.DELETE;
import org.picocontainer.web.NONE;
import org.picocontainer.web.PUT;
import org.picocontainer.web.PicoContainerWebException;
import org.picocontainer.injectors.MethodInjection;
import org.picocontainer.injectors.Reinjector;
import org.picocontainer.injectors.ProviderAdapter;
import org.picocontainer.injectors.SingleMemberInjector;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Paul Hammant
 */
public class PicoWebRemoting {

    private final XStream xStream;
    private PicoWebRemotingMonitor monitor;
    private final String toStripFromUrls;
    private final String suffixToStrip;
    private final String scopesToPublish;
    private final boolean lowerCasePath;
    private final boolean useMethodNamePrefixesForVerbs;

    private Map<String, Object> paths = new HashMap<String, Object>();
    private static final String GET = "GET";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";
    private static final String POST = "POST";
    private static final String FALLBACK = "FALLBACK";

    public PicoWebRemoting(XStream xStream, String prefixToStripFromUrls, String suffixToStrip, String scopesToPublish,
                           boolean lowerCasePath, boolean useMethodNamePrefixesForVerbs) {
        this.xStream = xStream;
        this.toStripFromUrls = prefixToStripFromUrls;
        this.suffixToStrip = suffixToStrip;
        this.scopesToPublish = scopesToPublish;
        this.lowerCasePath = lowerCasePath;
        this.useMethodNamePrefixesForVerbs = useMethodNamePrefixesForVerbs;
        this.xStream.registerConverter(new ISO8601DateConverter());
    }

    public Map<String, Object> getPaths() {
        return paths;
    }

    public void setMonitor(PicoWebRemotingMonitor monitor) {
        this.monitor = monitor;
    }

    protected String processRequest(String pathInfo, PicoContainer reqContainer, String httpMethod) throws IOException {

        try {
            String path = pathInfo.substring(1);
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            path = toStripFromUrls + path;

            if (suffixToStrip != null && path.endsWith(suffixToStrip)) {
                path = path.substring(0, path.indexOf(suffixToStrip));
            }

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
            return errorResult(monitor.nullParameterForMethodInvocation(e.getParameterName()));
        } catch (PicoCompositionException e) {
            return errorResult(monitor.picoCompositionExceptionForMethodInvocation(e));
        } catch (RuntimeException e) {
            Object o = monitor.runtimeExceptionForMethodInvocation(e);
            return errorResult(o);
        }

    }

    private RuntimeException makeNothingMatchingException() {
        return new PicoContainerWebException("Nothing matches the path requested");
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
        HashMap<String, Method> methodz = methods.get(methodName);
        Method method = null;
        if (useMethodNamePrefixesForVerbs) {
            method = methodz.get(verb);
        }
        if (method == null) {
            method = methodz.get(FALLBACK);
        }
        if (method == null) {
            throw new PicoContainerWebException("method not allowed for " + verb);
        }
        return reinject(methodName, method, methods.getComponent(), reqContainer);
    }

    private boolean delete(Method method) {
        return method.getAnnotation(DELETE.class) != null;
    }

    private boolean put(Method method) {
        return method.getAnnotation(PUT.class) != null;
    }

    private boolean get(Method method) {
        return method.getAnnotation(GET.class) != null;
    }

    private boolean post(Method method) {
        return method.getAnnotation(POST.class) != null;
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
                String webMethodName = getMethodName(method);
                String webVerb = getVerbName(method);
                HashMap<String, Method> methodz = webMethods.get(webMethodName);
                if (methodz == null) {
                    methodz = new HashMap<String, Method>();
                    webMethods.put(webMethodName, methodz);
                }
                if (post(method)) {
                    methodz.put(POST, method);
                }
                if (get(method)) {
                    methodz.put(GET, method);
                }
                if (delete(method)) {
                    methodz.put(DELETE, method);
                }
                if (put(method)) {
                    methodz.put(PUT, method);
                }
                methodz.put(webVerb, method);
            }
        }
        Class<?> superClass = component.getSuperclass();
        if (superClass != Object.class) {
            determineEligibleMethods(superClass, webMethods);
        }
    }

    private String getMethodName(Method method) {
        String name = method.getName();
        if (!useMethodNamePrefixesForVerbs) {
            return name;
        }
        if (prefixFolledByUpperChar(name, "get")) {
            return name.substring(3,4).toLowerCase() + name.substring(4);
        } else if (prefixFolledByUpperChar(name, "put")) {
            return name.substring(3,4).toLowerCase() + name.substring(4);
        } else if (prefixFolledByUpperChar(name, "delete")) {
            return name.substring(6,7).toLowerCase() + name.substring(7);
        } else if (prefixFolledByUpperChar(name, "post")) {
            return name.substring(4,5).toLowerCase() + name.substring(5);
        } else {
            return name;
        }
    }

    private String getVerbName(Method method) {
        if (!useMethodNamePrefixesForVerbs) {
            return FALLBACK;
        }
        String name = method.getName();
        if (prefixFolledByUpperChar(name, "get")) {
            return GET;
        } else if (prefixFolledByUpperChar(name, "put")) {
            return PUT;
        } else if (prefixFolledByUpperChar(name, "delete")) {
            return DELETE;
        } else if (prefixFolledByUpperChar(name, "post")) {
            return POST;
        } else {
            return FALLBACK;
        }
    }

    private boolean prefixFolledByUpperChar(String name, String prefix) {
        return name.startsWith(prefix) && name.length() > prefix.length()
                && Character.isUpperCase(name.charAt(prefix.length()));
    }

    private void publishAdapter(Class<?> key) {
        String path = getClassName(key).replace('.', '/');
        if (toStripFromUrls != "" || path.startsWith(toStripFromUrls)) {
            paths.put(path, key);
            directorize(path, key);
            directorize(path);
        }
    }

    private String getClassName(Class<?> key) {
        String name = key.getName();
        if (lowerCasePath) {
            return name.toLowerCase();
        } else {
            return name;
        }
    }

    protected void directorize(String path, Class<?> comp) {
        WebMethods webMethods = new WebMethods(comp);
        paths.put(path, webMethods);
        determineEligibleMethods(comp, webMethods);
    }

    private String errorResult(Object errorResult) {
        return xStream.toXML(errorResult) + "\n";
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

    public void visitClass(String clazz, MutablePicoContainer mutablePicoContainer, MethodVisitor mapv) throws IOException {
        String s = toStripFromUrls + clazz;
        Object node = paths.get(s);
        if (node instanceof WebMethods) {
            WebMethods wm = (WebMethods) node;
            Class<?> x = wm.getComponent();
            Class<?> y = x.getSuperclass();
            if (y != null) {
                String s1 = y.getName().replace(".","/");
                if (s1.startsWith(toStripFromUrls)) {
                    mapv.superClass(s1.substring(toStripFromUrls.length()));
                } else {
                    mapv.superClass(s1);
                }
            }
            Set<?> keys = wm.keySet();
            for (Object o : keys) {
                String methodName = o.toString();
                HashMap<String, Method> foo = wm.get(methodName);
                Method m = foo.get(GET);
                if (m == null) {
                    m = foo.get(FALLBACK);
                }
                mapv.method(methodName, m);
            }
        }
    }

    @SuppressWarnings("serial")
	protected static class Directories extends HashSet<String> {
    }

    @SuppressWarnings("serial")
	public static class WebMethods extends HashMap<String, HashMap<String, Method>> {
        private final Class<?> component;

        public WebMethods(Class<?> component) {
            this.component = component;
        }

        public Class<?> getComponent() {
            return component;
        }
    }
}
