package org.nanocontainer.nanowar;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @author Mauro Talevi
 */
public class ServletRequestContainerFilterTestCase extends MockObjectTestCase {

    private ServletRequestContainerFilter filter;
    
    public void setUp(){ 
        filter = new ServletRequestContainerFilter();        
    }
    
    public void testInit() throws Exception {
        filter.init(mockFilterConfig(mockServletContext()));
    }

    public void testDestroy() throws Exception {
        filter.destroy();
        // no-op method
    }

    public void testDoFilterWhenAlreadyFiltered() throws Exception {
        filter.init(mockFilterConfig(null));
        filter.doFilter(mockRequest(ServletRequestContainerFilter.ALREADY_FILTERED_KEY, Boolean.TRUE), mockResponse(), mockFilterChain(true));
    }
        
    private ServletContext mockServletContext() {
        Mock mock = mock(ServletContext.class);
        return (ServletContext)mock.proxy();
    }
    
    private HttpServletRequest mockRequest(Object key, Object attribute) {
        Mock mock = mock(HttpServletRequest.class);
        if ( key != null ){
            mock.expects(once()).method("getAttribute").with(eq(key)).will(returnValue(attribute));
        }
        return (HttpServletRequest)mock.proxy();
    }
    
    private HttpServletResponse mockResponse() {
        Mock mock = mock(HttpServletResponse.class);
        return (HttpServletResponse)mock.proxy();
    }
    
    private FilterChain mockFilterChain(boolean doFilter){
        Mock mock = mock(FilterChain.class);
        if ( doFilter ){
            mock.expects(atLeastOnce()).method("doFilter").withAnyArguments();
        }
        return (FilterChain)mock.proxy();
    }
    
    private FilterConfig mockFilterConfig(ServletContext context) {
        Mock mock = mock(FilterConfig.class);
        mock.expects(once()).method("getServletContext").will(returnValue(context));
        return (FilterConfig)mock.proxy();
    }

}
