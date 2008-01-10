package org.nanocontainer.nanowar.nanoweb;

import static org.junit.Assert.assertEquals;
import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.nanowar.KeyConstants;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ObjectReference;
import org.picocontainer.behaviors.Caching;

/**
 * @author Aslak Helles&oslash;y
 * @author Kouhei Mori
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public class NanoWebServletTestCase {
	
	private Mockery mockery = mockeryWithCountingNamingScheme();
	
    private NanoWebServlet nanoServlet;
    private MutablePicoContainer applicationContainer;
    private MutablePicoContainer requestContainer;

    private HttpServletRequest request = mockery.mock(HttpServletRequest.class);
    private HttpServletResponse response = mockery.mock(HttpServletResponse.class);
    private ServletContext servletContext = mockery.mock(ServletContext.class);
    private ServletConfig servletConfig = mockery.mock(ServletConfig.class);
    private ContainerBuilder containerBuilder = mockery.mock(ContainerBuilder.class);
    private RequestDispatcher requestDispatcher = mockery.mock(RequestDispatcher.class);

    @Before public void setUp() throws Exception {
        nanoServlet = new NanoWebServlet();
        applicationContainer = new DefaultPicoContainer();
        requestContainer = new DefaultPicoContainer(new Caching());
        // url params
        final Vector paramNames = new Vector();
        paramNames.add("year");

        mockery.checking(new Expectations(){{
        	atLeast(4).of(servletConfig).getServletContext();
        	will(returnValue(servletContext));
        	atLeast(1).of(servletConfig).getServletName();
        	will(returnValue("NanoWeb"));
        	one(servletContext).log(with(equal("NanoWeb: init")));
        	one(servletContext).getAttribute(with(equal(KeyConstants.APPLICATION_CONTAINER)));
        	will(returnValue(applicationContainer));
        	one(servletContext).getAttribute(with(equal(KeyConstants.BUILDER)));
        	will(returnValue(containerBuilder));
        	one(servletContext).getResource(with(equal("/test_doit_success.vm")));
        	will(returnValue(new URL("http://dummy/")));
        	atLeast(2).of(request).getAttribute(with(equal(KeyConstants.REQUEST_CONTAINER)));
        	will(returnValue(requestContainer));
        	one(request).getSession(with(equal(true)));
        	will(returnValue(null));
        	atLeast(2).of(request).getAttribute(with(equal("javax.servlet.include.servlet_path")));
        	will(returnValue(null));
        	one(request).getParameterNames();
        	will(returnValue(paramNames.elements()));
        	one(request).getParameter(with(equal("year")));
        	will(returnValue("2004"));
        	one(request).getServletPath();
        	will(returnValue("/test/doit.nano"));
        	one(request).getRequestDispatcher(with(equal("/test_doit_success.vm")));
        	will(returnValue(requestDispatcher));
        	one(containerBuilder).buildContainer(with(any(ObjectReference.class)), with(any(ObjectReference.class)), with(any(Object.class)), with(any(Boolean.class)));
        	one(containerBuilder).killContainer(with(any(ObjectReference.class)));
        	one(requestDispatcher).forward(with(equal(request)), with(equal(response)));
        }});
        nanoServlet.init((ServletConfig) servletConfig);
    }

    @Test public void testParametersShouldBeSetAndExecuteInvokedOnJavaAction() throws IOException, ServletException {
        requestContainer.addComponent("/test", MyAction.class);
        
        mockery.checking(new Expectations(){{
        	one(request).setAttribute(with(equal("action")), with(any(MyAction.class)));
        }});

        nanoServlet.service((HttpServletRequest)request, (HttpServletResponse)response);

        MyAction action = (MyAction) requestContainer.getComponent("/test");
        assertEquals(2004, action.getYear());
        assertEquals("success", action.doit());
    }

    @Test public void testParametersShouldBeSetAndExecuteInvokedOnGroovyAction() throws IOException, ServletException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    	 mockery.checking(new Expectations(){{
         	one(servletContext).getResource(with(equal("/test.groovy")));
         	will(returnValue(getClass().getResource("/test.groovy")));
        	one(request).setAttribute(with(equal("action")), with(any(Object.class)));
         }});
        nanoServlet.service((HttpServletRequest)request, (HttpServletResponse)response);

        Object action = requestContainer.getComponent("/test.groovy");
        Method getYear = action.getClass().getMethod("getProperty", (new Class[] {String.class}));
        Object resultYear = getYear.invoke(action, (new Object[]{"year"}));
        assertEquals("2004", resultYear.toString());
    }
}