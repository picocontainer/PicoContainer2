package org.nanocontainer.nanowar.nanoweb;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.nanowar.KeyConstants;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.behaviors.Caching;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Vector;

/**
 * @author Aslak Helles&oslash;y
 * @author Kouhei Mori
 * @version $Revision$
 */
public class NanoWebServletTestCase extends MockObjectTestCase {
    private NanoWebServlet nanoServlet;
    private MutablePicoContainer applicationContainer;
    private MutablePicoContainer requestContainer;

    private Mock requestMock;
    private Mock responseMock;
    private Mock servletContextMock;
    private Mock servletConfigMock;
    private Mock containerBuilderMock;
    private Mock requestDispatcherMock;

    protected void setUp() throws Exception {
        super.setUp();
        nanoServlet = new NanoWebServlet();
        requestMock = mock(HttpServletRequest.class);
        responseMock = mock(HttpServletResponse.class);
        servletContextMock = mock(ServletContext.class);
        servletConfigMock = mock(ServletConfig.class);
        servletConfigMock.expects(once())
                         .method("getServletContext")
                         .withNoArguments()
                         .will(returnValue(servletContextMock.proxy()));
        servletConfigMock.expects(once())
                         .method("getServletContext")
                         .withNoArguments()
                         .will(returnValue(servletContextMock.proxy()));
        servletConfigMock.expects(atLeastOnce())
                         .method("getServletName")
                         .withNoArguments()
                         .will(returnValue("NanoWeb"));
        servletConfigMock.expects(once())
                         .method("getServletContext")
                         .withNoArguments()
                         .will(returnValue(servletContextMock.proxy()));
        servletConfigMock.expects(once())
                         .method("getServletContext")
                         .withNoArguments()
                         .will(returnValue(servletContextMock.proxy()));
        servletContextMock.expects(once())
                          .method("log")
                          .with(eq("NanoWeb: init"));
        applicationContainer = new DefaultPicoContainer();
        servletContextMock.expects(once())
                          .method("getAttribute")
                          .with(eq(KeyConstants.APPLICATION_CONTAINER)).will(returnValue(applicationContainer));

        servletContextMock.expects(once())
                          .method("getResource")
                          .with(eq("/test_doit_success.vm"))
                          .will(returnValue(new URL("http://dummy/")));
        containerBuilderMock = mock(ContainerBuilder.class);
        servletContextMock.expects(once())
                          .method("getAttribute")
                          .with(eq(KeyConstants.BUILDER))
                          .will(returnValue(containerBuilderMock.proxy()));
        requestContainer = new DefaultPicoContainer(new Caching());
        requestMock.expects(once())
                   .method("getAttribute")
                   .with(eq(KeyConstants.REQUEST_CONTAINER))
                   .will(returnValue(requestContainer));
        requestMock.expects(once())
                    .method("getSession")
                    .with(eq(Boolean.TRUE))
                    .will(returnValue(null));
        containerBuilderMock.expects(once())
                            .method("buildContainer")
                            .withAnyArguments();
        containerBuilderMock.expects(once())
                            .method("killContainer")
                            .withAnyArguments();
        requestMock.expects(once())
                   .method("getAttribute")
                   .with(eq("javax.servlet.include.servlet_path"))
                   .will(returnValue(null));
        requestMock.expects(once())
                   .method("getAttribute")
                   .with(eq("javax.servlet.include.servlet_path"))
                   .will(returnValue(null));
        requestMock.expects(once())
                   .method("getAttribute")
                   .with(eq(KeyConstants.REQUEST_CONTAINER))
                   .will(returnValue(requestContainer));
        requestDispatcherMock = mock(RequestDispatcher.class);
        requestDispatcherMock.expects(once())
                             .method("forward")
                             .with(eq(requestMock.proxy()), eq(responseMock.proxy()));

        // url params
        Vector paramtereNames = new Vector();
        paramtereNames.add("year");
        requestMock.expects(once())
                   .method("getParameterNames")
                   .withNoArguments()
                   .will(returnValue(paramtereNames.elements()));
        requestMock.expects(once())
                   .method("getParameter")
                   .with(eq("year"))
                   .will(returnValue("2004"));

        // path, action and view
        requestMock.expects(once())
                   .method("getServletPath")
                   .withNoArguments()
                   .will(returnValue("/test/doit.nano"));
        requestMock.expects(once())
                   .method("getRequestDispatcher")
                   .with(eq("/test_doit_success.vm"))
                   .will(returnValue(requestDispatcherMock.proxy()));

        nanoServlet.init((ServletConfig) servletConfigMock.proxy());
    }

    public void testParametersShouldBeSetAndExecuteInvokedOnJavaAction() throws IOException, ServletException {
        requestContainer.addComponent("/test", MyAction.class);
        requestMock.expects(once())
                   .method("setAttribute")
                   .with(eq("action"), isA(MyAction.class));

        nanoServlet.service((HttpServletRequest)requestMock.proxy(), (HttpServletResponse)responseMock.proxy());

        MyAction action = (MyAction) requestContainer.getComponent("/test");
        assertEquals(2004, action.getYear());
        assertEquals("success", action.doit());
    }

    public void testParametersShouldBeSetAndExecuteInvokedOnGroovyAction() throws IOException, ServletException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        servletConfigMock.expects(once())
                         .method("getServletContext")
                         .withNoArguments()
                         .will(returnValue(servletContextMock.proxy()));
        servletContextMock.expects(once())
                          .method("getResource")
                          .with(eq("/test.groovy"))
                          .will(returnValue(getClass().getResource("/test.groovy")));
        requestMock.expects(once())
                   .method("setAttribute")
                   .with(eq("action"), isA(Object.class));

        nanoServlet.service((HttpServletRequest)requestMock.proxy(), (HttpServletResponse)responseMock.proxy());

        Object action = requestContainer.getComponent("/test.groovy");
        Method getYear = action.getClass().getMethod("getProperty", (new Class[] {String.class}));
        Object resultYear = getYear.invoke(action, (new Object[]{"year"}));
        assertEquals("2004", resultYear.toString());
    }
}