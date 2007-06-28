package org.nanocontainer.nanowar;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;

/**
*<p>
* ServletContainerProxyFilter is a Filter which delegates to any Filter which is registered in a PicoContainer,
* in any of the web scopes: context, session or request.
* This form of delegation is particularly useful as it brings dependency injections to filters.
* In fact the delegate filter may be implemented via any form of dependency injection.
*</p>
*
*<p>The delegate Filter must be registered via the <code>delegate-key</code> or <code>delegate-class</code>
* init-params of this Filter.  
*</p>
* 
*<p>The initialization is done lazily, using the <code>init-type</code> init-param
* to control it.  Allowed values are:
* <ul>
*   <li>"context": will call init() on the filter only once</li>
*   <li>"request": will re-init it at every request</li>
*   <li>"never": will never init it</li>
*</ul>
*The default is "context".
* </p>
* 
* <p>The lookup in the PicoContainer is by default done for each request, but you
* can control that behaviour with the <code>lookup-only-once</code> init-param.
* If set to "true", ServletContainerProxyFilter will only lookup your delegate filter
* at the first request.
* </p>
* 
* <p><b>Note</b>: Be aware that any dependency on your filter, in this setup, will stay
* referenced by your filter for its whole lifetime, even though this dependency
* might have been set up at request level in your composer!
* </p>
*
* @author Gr&eacute;gory Joseph
* @author Mauro Talevi
*/
public class ServletContainerProxyFilter implements Filter {

    private static final String CONTEXT_INIT_TYPE = "context";
    private static final String REQUEST_INIT_TYPE = "request";

    private String initType;
    private boolean lookupOnlyOnce;
    private FilterConfig filterConfig;
    private Filter delegate;
    private ServletContainerFinder containerFinder;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        initType = filterConfig.getInitParameter("init-type");        
        if ( initType == null ){
            initType = CONTEXT_INIT_TYPE;
        }
        lookupOnlyOnce = Boolean.valueOf(filterConfig.getInitParameter("lookup-only-once"));
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (delegate == null || !lookupOnlyOnce) {            
            lookupDelegate((HttpServletRequest) request);
            if (initType.equals(CONTEXT_INIT_TYPE) ) {
                initDelegate();
            }
        }
        if (initType.equals(REQUEST_INIT_TYPE)) {
            initDelegate();
        }
        delegate.doFilter(request, response, filterChain);
    }

    public void destroy() {
        if (delegate != null) {
            delegate.destroy();
        }
    }
    
    protected void initDelegate() throws ServletException {
        if (delegate == null) {
            throw new IllegalStateException("Delegate filter was not set up");
        }
        delegate.init(filterConfig);
    }
    
    /**
     * Looks up delegate Filter in PicoContainer found in any of the web scopes.
     * 
     * @param request the HttpServletRequest used to find the PicoContainer
     * @throws PicoCompositionException if the delegate Filter cannot be found
     */
    protected void lookupDelegate(HttpServletRequest request) {
        PicoContainer pico = findContainer(request);
        String delegateClassName = filterConfig.getInitParameter("delegate-class");
        String delegateKey = filterConfig.getInitParameter("delegate-key");
        if (delegateClassName != null) {
            try {
                Class delegateClass = getClassLoader().loadClass(delegateClassName);
                delegate = (Filter) pico.getComponent(delegateClass);
            } catch (ClassNotFoundException e) {
                throw new PicoCompositionException("Cannot load " + delegateClassName, e);
            }
        } else if (delegateKey != null) {
            delegate = (Filter) pico.getComponent(delegateKey);
        } else {
            throw new PicoCompositionException("You must specify one of delegate-class or delegate-key in the filter config");
        }

        if (delegate == null) {
            throw new PicoCompositionException("Cannot find delegate for class " + delegateClassName + " or key "+ delegateKey);
        }
    }

    /**
     * Finds PicoContainer via the ServletContainerFinder 
     * @param request the HttpServletRequest
     * @return A PicoContainer 
     * @see ServletContainerFinder
     */
    private PicoContainer findContainer(HttpServletRequest request) {
        if (containerFinder == null) {
            // lazy initialisation
            containerFinder = new ServletContainerFinder();
        }
        return containerFinder.findContainer(request);
    }

    private ClassLoader getClassLoader() {
        return this.getClass().getClassLoader();
    }
    

}
