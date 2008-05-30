/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Konstantin Pribluda                                      *
 *****************************************************************************/
package org.picocontainer.web.chain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.picocontainer.PicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.ObjectReference;

/**
 * <p>
 * Filter for building chain of servlet containers, based on servlet path.
 * </p>
 * <p/>
 * <p>
 * Chain is wired to request container and substituted with request scoped
 * reference so nobody notices additional containers.
 * </p>
 * <p/>
 * <p>
 * Chain is started after creation and stopped after request processing.
 * </p>
 * <p/>
 * <p>
 * At end of request processing, the chain is removed and reference to original
 * request container is established again.
 * </p>
 * <p/>
 * <p/>
 * You may specify divertor to provide URL to redirect in cause of failures. it
 * has to implement
 *
 * @author Konstantin Pribluda
 * @author Mauro Talevi
 * @see org.picocontainer.web.chain.Divertor#divert(java.lang.Throwable) it will be
 *      looked up in container either based on divertorKey or directorClass
 *      parameters, or looked up by class name. If no divertor can be found,
 *      servlet exception will be passed back.
 *      <p/>
 *      <p>
 *      The filter requires the following mandatory init params:
 *      <ul>
 *      <li>builderClassName: specifies the name of the ContainerBuilder to use, eg
 *      XMLContainerBuilder</li>
 *      <li>containerScriptName: specifies the container script name found in the
 *      paths, eg nano.xml</li>
 *      <li>emptyContainerScript: specifies the empty container script (as a String)
 *      to use if the path container script is not found</li>
 *      </ul>
 *      </p>
 *      <p/>
 *      <p>
 *      The filter accepts the following optional init params:
 *      <ul>
 *      <li>chainMonitor: specifies the name of the ChainMonitor to use, eg
 *      ConsoleChainMonitorr</li>
 *      <li>divertorKey: string key to lookup URL divertor ( to use in case of
 *      failure) </li>
 *      <li>divertorClass: class name of URL divertor ( use instead of string key to
 *      lookup from container)</li>
 *      </ul>
 *      </p>
 */
public class ContainerChainFilter implements Filter {

    /**
     * The init param name for the chain monitor
     */
    public static final String CHAIN_MONITOR_PARAM = "chainMonitor";

    /**
     * The init param name for the failure url
     */
    public final static String FAILURE_URL_PARAM = "failureUrl";

    /**
     * The init param name for the builder class name
     */
    public static final String BUILDER_CLASSNAME_PARAM = "builderClassName";

    /**
     * The init param name for the container script name
     */
    public static final String CONTAINER_SCRIPT_NAME_PARAM = "containerScriptName";

    /**
     * The init param name for the empty container script
     */
    public static final String EMPTY_CONTAINER_SCRIPT_PARAM = "emptyContainerScript";

    /**
     * init param for specifiying divertor class name
     */
    public final static String DIVERTOR_CLASS_PARAM = "divertorClass";

    /**
     * init param for specifiying key of divertor
     */
    public final static String DIVERTOR_KEY_PARAM = "divertorKey";

    /**
     * Key used to prevent chain creation when already processing
     */
    private final static String ALREADY_FILTERED_KEY = "nanocontainer_chain_filter_already_filtered";

    /**
     * The path separator
     */
    private static final String PATH_SEPARATOR = "/";

    private ServletChainBuilder chainBuilder;

    private ChainMonitor monitor;

    String divertorKey;

    Class divertorClass;

    DivertorRetriever retriever;

    /**
     * @see Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig config) throws ServletException {
        monitor = createMonitor(config.getInitParameter(CHAIN_MONITOR_PARAM));

        String builderClassName = config
                .getInitParameter(BUILDER_CLASSNAME_PARAM);
        String containerScriptName = config
                .getInitParameter(CONTAINER_SCRIPT_NAME_PARAM);
        String emptyContainerScript = config
                .getInitParameter(EMPTY_CONTAINER_SCRIPT_PARAM);
        checkParametersAreSet(builderClassName, containerScriptName,
                emptyContainerScript);
        chainBuilder = new ServletChainBuilder(config.getServletContext(),
                builderClassName, containerScriptName, emptyContainerScript);

        // divertor configuration take key
        divertorKey = config.getInitParameter(DIVERTOR_KEY_PARAM);
        String clazz = config.getInitParameter(DIVERTOR_CLASS_PARAM);
        if (divertorKey != null) {
            retriever = new DivertorRetriever() {

                public Divertor getDivertor(PicoContainer container) {
                    return (Divertor) container
                            .getComponent(divertorKey);
                }

            };
        } else if (clazz != null) {
            try {
                divertorClass = Thread.currentThread().getContextClassLoader()
                        .loadClass(clazz);
            } catch (ClassNotFoundException e) {
                throw new ServletException("can not load divertor class", e);
            }
            retriever = new DivertorRetriever() {
                public Divertor getDivertor(PicoContainer container) {
                    return (Divertor) container
                            .getComponent(divertorClass);
                }
            };
        } else {
            retriever = new DivertorRetriever() {
                public Divertor getDivertor(PicoContainer container) {
                    return container
                            .getComponent(Divertor.class);
                }
            };
        }
    }

    /**
     * Instantiates ChainMonitor from class name or a ConsoleChainMonitor if
     * class name not provided or invalid
     *
     * @param monitorClassName
     * @return A ChainMonitor
     */
    private ChainMonitor createMonitor(String monitorClassName) {
        ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        try {
            return (ChainMonitor) classLoader.loadClass(monitorClassName)
                    .newInstance();
        } catch (Exception e) {
            return new ConsoleChainMonitor();
        }
    }

    /**
     * Checks parameters are set
     *
     * @param builderClassName
     * @param containerScriptName
     * @param emptyContainerScript
     * @throws ServletException if parameters are not all set
     */
    private void checkParametersAreSet(String builderClassName,
                                       String containerScriptName, String emptyContainerScript)
            throws ServletException {
        if (builderClassName == null) {
            throw new ServletException("Parameter '" + BUILDER_CLASSNAME_PARAM
                    + "' must be set in filter init params");
        }
        if (containerScriptName == null) {
            throw new ServletException("Parameter '"
                    + CONTAINER_SCRIPT_NAME_PARAM
                    + "' must be set in filter init params");
        }
        if (emptyContainerScript == null) {
            throw new ServletException("Parameter '"
                    + EMPTY_CONTAINER_SCRIPT_PARAM
                    + "' must be set in filter init params");
        }
    }

    /**
     * @see Filter#doFilter(javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        /**
         * take care of some weirdness in different implementations of
         * servlet  containers (inspired by webwork request utils, fixes
         * problems on BEA WLS 8.1.x +)
         */
        String servletPath = httpRequest.getServletPath();

        // ok, no servlet path there. get from request uri
        if (servletPath == null || "".equals(servletPath)) {
            String requestUri = httpRequest.getRequestURI();

            int startIndex = httpRequest.getContextPath().equals("") ? 0
                    : httpRequest.getContextPath().length();

            int endIndex = httpRequest.getPathInfo() == null ? requestUri
                    .length() : requestUri.lastIndexOf(httpRequest
                    .getPathInfo());

            if (startIndex >= endIndex) {
                //
                servletPath = requestUri.substring(startIndex);
            } else {
                servletPath = requestUri.substring(startIndex, endIndex);
            }
        }
        if (httpRequest.getAttribute(ALREADY_FILTERED_KEY) == null) {
            // we were not here, filter
            httpRequest.setAttribute(ALREADY_FILTERED_KEY, Boolean.TRUE);
            // obtain pico container for chaining
            PicoContainer container = obtainContainer(httpRequest);
            try {

                monitor.filteringURL(servletPath);
                List elements = extractPathElements(servletPath);
                // build chain
                ContainerChain chain = chainBuilder.buildChain(elements
                        .toArray(), container);
                // start chain
                chain.start();
                // inject last container in chain
                injectLastContainerInChain(request, chain);
                // filter
                filterChain.doFilter(request, response);
                // stop chain
                chain.stop();
            } catch (Exception ex) {
                handleException(ex, container, request, response);
            } finally {
                restoreContainer(request, container);
            }

        } else {
            // proceed further
            filterChain.doFilter(request, response);
        }
    }

    private ObjectReference obtainRequestObjectReference(ServletRequest request) {
        //TODO get this working again
        return null;
//        return new RequestScopeReference(request,
//                KeyConstants.REQUEST_CONTAINER);
    }

    private PicoContainer obtainContainer(ServletRequest request) {
        return (PicoContainer) obtainRequestObjectReference(request).get();
    }

    private void injectLastContainerInChain(ServletRequest request,
                                            ContainerChain chain) {
        obtainRequestObjectReference(request).set(chain.getLast());
    }

    private void restoreContainer(ServletRequest request,
                                  PicoContainer container) {
        obtainRequestObjectReference(request).set(container);
    }

    private void handleException(Exception e, PicoContainer container,
                                 ServletRequest request, ServletResponse response)
            throws ServletException, IOException {
        monitor.exceptionOccurred(e);
        Divertor divertor = retriever.getDivertor(container);
        if (divertor != null) {
            String failureUrl = divertor.divert(e);
            if (failureUrl != null) {
                // if we got an exception, we create fake container
                DefaultPicoContainer dpc = new DefaultPicoContainer(container);
                dpc.addComponent("cause", e.getCause());
                // wire container to request
                obtainRequestObjectReference(request).set(dpc);
                // and transfer us to this url
                request.getRequestDispatcher(failureUrl).forward(request, response);
            } else {
                // if there is no configured redirect url for failure, we just
                // wrap in servlet exception and rethrow this.
                throw new ServletException(e);
            }
        }  else {
            // rethrow exception as servlet exception
            throw new ServletException(e);
        }
    }

    /**
     * Extracts the list of path element from url
     *
     * @param url the String with the original url
     * @return A List of String representing the path elements
     */
    private List extractPathElements(String url) {
        List elements = new ArrayList();
        elements.add(PATH_SEPARATOR);
        for (int pos = url.indexOf(PATH_SEPARATOR, 1); pos > 0; pos = url
                .indexOf(PATH_SEPARATOR, pos + 1)) {
            String path = url.substring(0, pos + 1);
            elements.add(path);
            monitor.pathAdded(path, url);
        }
        return elements;
    }

    /**
     * @see Filter#destroy()
     */
    public void destroy() {
        // no-op
    }

    /**
     * poor man closure to
     *
     * @author k.pribluda
     */
	interface DivertorRetriever {
		Divertor getDivertor(PicoContainer container);
	}
}