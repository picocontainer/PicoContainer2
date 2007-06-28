/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;

import org.jmock.Mock;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.DefaultPicoContainer;


/**
 * @author Gr&eacute;gory Joseph
 * @author Mauro Talevi
 */
public class ServletContainerProxyFilterTestCase extends AbstractServletTestCase {
    protected void tearDown() throws Exception {
        FooFilter.resetInitCounter();
    }

    public void testSimplestCaseWithClassRegistration() throws Exception {
        FilterDef f = new FilterDef("pico-filter", ServletContainerProxyFilter.class, FooFilter.class, null, null, false);
        initTest("pico.addComponent(org.nanocontainer.nanowar.Foo)\n" +
                "pico.addComponent(org.nanocontainer.nanowar.FooFilter)\n",
                "", "", f);
        String res = doTest();
        assertEquals("zip-empty-zap", res);
        assertEquals(1, FooFilter.getInitCounter());
    }

    public void testSimplestCaseWithKeyRegistration() throws Exception {
        FilterDef f = new FilterDef("pico-filter", ServletContainerProxyFilter.class, null, "foo-filter", null, false);
        initTest("pico.addComponent(org.nanocontainer.nanowar.Foo)\n" +
                "pico.addComponent('foo-filter', org.nanocontainer.nanowar.FooFilter)\n",
                "", "", f);
        String res = doTest();
        assertEquals("zip-empty-zap", res);
        assertEquals(1, FooFilter.getInitCounter());
    }

    public void testFilterRegisteredAtRequestScope() throws Exception {
        FilterDef f = new FilterDef("pico-filter", ServletContainerProxyFilter.class, null, "foo-filter", null, false);
        initTest("", "",
                "pico.addComponent(org.nanocontainer.nanowar.Foo)\n" +
                "pico.addComponent('foo-filter', org.nanocontainer.nanowar.FooFilter)\n",
                f);
        String res = doTest();
        assertEquals("zip-empty-zap", res);
        assertEquals(1, FooFilter.getInitCounter());
    }

    public void testFilterWithInitSetToContextShouldCallInitOnlyOncePerLookup() throws Exception {
        FilterDef f = new FilterDef("pico-filter", ServletContainerProxyFilter.class, null, "foo-filter", null, true);
        initTest("pico.addComponent(org.nanocontainer.nanowar.Foo)\n" +
                "pico.addComponent('foo-filter', org.nanocontainer.nanowar.FooFilter)\n",
                "", "", f);
        doTest();
        doTest();
        String res = doTest();
        assertEquals("zip-empty-zap", res);
        assertEquals(1, FooFilter.getInitCounter());
    }

    public void testFilterWithInitSetToContextShouldCallInitOnlyOncePerLookupWhichMakesItEachTimeIfLookupNotSetToOnlyOnce() throws Exception {
        FilterDef f = new FilterDef("pico-filter", ServletContainerProxyFilter.class, null, "foo-filter", null, false);
        initTest("pico.addComponent(org.nanocontainer.nanowar.Foo)\n" +
                "pico.addComponent('foo-filter', org.nanocontainer.nanowar.FooFilter)\n",
                "", "", f);
        doTest();
        doTest();
        String res = doTest();
        assertEquals("zip-empty-zap", res);
        assertEquals(3, FooFilter.getInitCounter());
    }

    public void testFilterWithInitSetToRequestShouldCallInitAtEachRequest() throws Exception {
        FilterDef f = new FilterDef("pico-filter", ServletContainerProxyFilter.class, null, "foo-filter", "request", false);
        initTest("pico.addComponent(org.nanocontainer.nanowar.Foo)\n" +
                "pico.addComponent('foo-filter', org.nanocontainer.nanowar.FooFilter)\n",
                "", "", f);
        doTest();
        doTest();
        String res = doTest();

        assertEquals("zip-empty-zap", res);
        assertEquals(3, FooFilter.getInitCounter());
    }

    public void testFilterRegisteredAtContextScopeWithInitSetToNeverShouldNeverCallInit() throws Exception {
        FilterDef f = new FilterDef("pico-filter", ServletContainerProxyFilter.class, null, "foo-filter", "never", false);
        initTest("pico.addComponent(org.nanocontainer.nanowar.Foo)\n" +
                "pico.addComponent('foo-filter', org.nanocontainer.nanowar.FooFilter)\n",
                "", "", f);
        doTest();
        doTest();
        String res = doTest();
        assertEquals("zip-empty-zap", res);
        assertEquals(0, FooFilter.getInitCounter());
    }

    public void testFilterRegisteredAtRequestScopeWithInitSetToNeverShouldNeverCallInit() throws Exception {
        FilterDef f = new FilterDef("pico-filter", ServletContainerProxyFilter.class, null, "foo-filter", "never", false);
        initTest("", "",
                "pico.addComponent(org.nanocontainer.nanowar.Foo)\n" +
                "pico.addComponent('foo-filter', org.nanocontainer.nanowar.FooFilter)\n",
                f);
        doTest();
        doTest();
        String res = doTest();
        assertEquals("zip-empty-zap", res);
        assertEquals(0, FooFilter.getInitCounter());    
    }
    
    public void testFilterDestroyIsIgnoredIfDelegateNotFound(){
        ServletContainerProxyFilter filter = new ServletContainerProxyFilter();
        filter.destroy();        
    }

    public void testFilterDestroyIsDelegated() throws Exception{
        ServletContainerProxyFilter filter = new ServletContainerProxyFilter();
        filter.init(mockFilterConfig(null, null, null, FooFilter.class.getName()));
        MutablePicoContainer container = new DefaultPicoContainer();
        container.addComponent(Foo.class);
        container.addComponent("foo-filter", FooFilter.class);
        filter.lookupDelegate(mockRequest(container));
        filter.destroy();
    }
    
    public void testInitDelegateFailsIfDelegateIsNotFound() throws Exception {
        ServletContainerProxyFilter filter = new ServletContainerProxyFilter();
        try {
            filter.initDelegate();
            fail("IllegalStateException expected");
        } catch ( IllegalStateException e) {
            assertEquals("Delegate filter was not set up", e.getMessage());
        }
    }
    
    public void testDelegateLookupFailsIfNeitherKeyOrClassIsSpecified() throws Exception{
        ServletContainerProxyFilter filter = new ServletContainerProxyFilter();
        String delegateKey = null;
        String delegateClassName = null;
        filter.init(mockFilterConfig(null, null, delegateKey, delegateClassName));
        MutablePicoContainer container = new DefaultPicoContainer();
        container.addComponent(Foo.class);
        try {
            filter.lookupDelegate(mockRequest(container));
            fail("PicoCompositionException expected");
        } catch ( PicoCompositionException e) {
            assertEquals("You must specify one of delegate-class or delegate-key in the filter config", e.getMessage());
        }
    }
    
    public void testDelegateLookupFailsIfClassCannotBeLoaded() throws Exception{
        ServletContainerProxyFilter filter = new ServletContainerProxyFilter();
        String delegateKey = null;
        String delegateClassName = "inexistentClass";
        filter.init(mockFilterConfig(null, null, delegateKey, delegateClassName));
        MutablePicoContainer container = new DefaultPicoContainer();
        container.addComponent(Foo.class);
        try {
            filter.lookupDelegate(mockRequest(container));
            fail("PicoCompositionException expected");
        } catch ( PicoCompositionException e) {
            assertEquals("Cannot load "+delegateClassName, e.getMessage());
        }
    }
    
    public void testDelegateLookupFailsIfFilterIsNotFoundInContainer() throws Exception{
        ServletContainerProxyFilter filter = new ServletContainerProxyFilter();
        String delegateKey = null;
        String delegateClassName = FooFilter.class.getName();
        filter.init(mockFilterConfig(null, null, delegateKey, delegateClassName));
        MutablePicoContainer container = new DefaultPicoContainer();
        container.addComponent(Foo.class);
        try {
            filter.lookupDelegate(mockRequest(container));
            fail("PicoCompositionException expected");
        } catch ( PicoCompositionException e) {
            assertEquals("Cannot find delegate for class " + delegateClassName + " or key "+ delegateKey, e.getMessage());
        }
    }    
    
    private FilterConfig mockFilterConfig(String initType, String lookupOnce, String delegateKey, String delegateClass) {
        Mock mock = mock(FilterConfig.class);
        mock.expects(once()).method("getInitParameter").with(eq("init-type")).will(returnValue(initType));
        mock.expects(once()).method("getInitParameter").with(eq("lookup-only-once")).will(returnValue(lookupOnce));
        mock.expects(once()).method("getInitParameter").with(eq("delegate-key")).will(returnValue(delegateKey));
        mock.expects(once()).method("getInitParameter").with(eq("delegate-class")).will(returnValue(delegateClass));
        return (FilterConfig)mock.proxy();
    }

    private HttpServletRequest mockRequest(MutablePicoContainer container) {
        Mock mock = mock(HttpServletRequest.class);
        mock.expects(once()).method("getAttribute").with(eq(KeyConstants.REQUEST_CONTAINER)).will(returnValue(container));
        return (HttpServletRequest)mock.proxy();
    }

}