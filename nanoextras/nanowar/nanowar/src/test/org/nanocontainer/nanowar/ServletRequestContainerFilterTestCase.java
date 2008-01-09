package org.nanocontainer.nanowar;

import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public class ServletRequestContainerFilterTestCase {

	private Mockery mockery = mockeryWithCountingNamingScheme();

	private ServletRequestContainerFilter filter = new ServletRequestContainerFilter();   
    
    @Test public void testInit() throws Exception {
        filter.init(mockFilterConfig(mockServletContext()));
    }

    @Test public void testDestroy() throws Exception {
        filter.destroy();
        // no-op method
    }

    @Test public void testDoFilterWhenAlreadyFiltered() throws Exception {
        filter.init(mockFilterConfig(null));
        filter.doFilter(mockRequest(ServletRequestContainerFilter.ALREADY_FILTERED_KEY, Boolean.TRUE), mockResponse(), mockFilterChain(true));
    }
        
    private ServletContext mockServletContext() {
    	return mockery.mock(ServletContext.class);
    }
    
    private HttpServletRequest mockRequest(final String key, final Object attribute) {
    	final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        if ( key != null ){
        	mockery.checking(new Expectations(){{
        		one(request).getAttribute(with(equal(key)));
        		will(returnValue(attribute));
        	}});
        }
        return request;
    }
    
    private HttpServletResponse mockResponse() {
    	return mockery.mock(HttpServletResponse.class);
    }
    
    private FilterChain mockFilterChain(boolean doFilter) throws IOException, ServletException{
    	final FilterChain chain = mockery.mock(FilterChain.class);
        if ( doFilter ){
        	mockery.checking(new Expectations(){{
        		one(chain).doFilter(with(any(ServletRequest.class)), with(any(ServletResponse.class)));
        	}});
        }
        return chain;
    }
    
    private FilterConfig mockFilterConfig(final ServletContext context) {
    	final FilterConfig config = mockery.mock(FilterConfig.class);
        mockery.checking(new Expectations(){{
        	one(config).getServletContext();
        	will(returnValue(context));
        }});
        return config;
    }

}
