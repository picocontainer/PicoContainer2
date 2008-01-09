/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoCompositionException;


/**
 * @author Gr&eacute;gory Joseph
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public class ServletContainerProxyFilterTestCase extends AbstractServletTestCase {
	
	private Mockery mockery = mockeryWithCountingNamingScheme();
	
    @After public void tearDown() throws Exception {
        FooFilter.resetInitCounter();
    }

    @Test public void testSimplestCaseWithClassRegistration() throws Exception {
        FilterDef f = new FilterDef("pico-filter", ServletContainerProxyFilter.class, FooFilter.class, null, null, false);
        initTest("pico.addComponent(org.nanocontainer.nanowar.Foo)\n" +
                "pico.addComponent(org.nanocontainer.nanowar.FooFilter)\n",
                "", "", f);
        String res = doTest();
        assertEquals("zip-empty-zap", res);
        assertEquals(1, FooFilter.getInitCounter());
    }

    @Test public void testSimplestCaseWithKeyRegistration() throws Exception {
        FilterDef f = new FilterDef("pico-filter", ServletContainerProxyFilter.class, null, "foo-filter", null, false);
        initTest("pico.addComponent(org.nanocontainer.nanowar.Foo)\n" +
                "pico.addComponent('foo-filter', org.nanocontainer.nanowar.FooFilter)\n",
                "", "", f);
        String res = doTest();
        assertEquals("zip-empty-zap", res);
        assertEquals(1, FooFilter.getInitCounter());
    }

    @Test public void testFilterRegisteredAtRequestScope() throws Exception {
        FilterDef f = new FilterDef("pico-filter", ServletContainerProxyFilter.class, null, "foo-filter", null, false);
        initTest("", "",
                "pico.addComponent(org.nanocontainer.nanowar.Foo)\n" +
                "pico.addComponent('foo-filter', org.nanocontainer.nanowar.FooFilter)\n",
                f);
        String res = doTest();
        assertEquals("zip-empty-zap", res);
        assertEquals(1, FooFilter.getInitCounter());
    }

    @Test public void testFilterWithInitSetToContextShouldCallInitOnlyOncePerLookup() throws Exception {
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

    @Test public void testFilterWithInitSetToContextShouldCallInitOnlyOncePerLookupWhichMakesItEachTimeIfLookupNotSetToOnlyOnce() throws Exception {
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

    @Test public void testFilterWithInitSetToRequestShouldCallInitAtEachRequest() throws Exception {
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

    @Test public void testFilterRegisteredAtContextScopeWithInitSetToNeverShouldNeverCallInit() throws Exception {
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

    @Test public void testFilterRegisteredAtRequestScopeWithInitSetToNeverShouldNeverCallInit() throws Exception {
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
    
    @Test public void testFilterDestroyIsIgnoredIfDelegateNotFound(){
        ServletContainerProxyFilter filter = new ServletContainerProxyFilter();
        filter.destroy();        
    }

    @Test public void testFilterDestroyIsDelegated() throws Exception{
        ServletContainerProxyFilter filter = new ServletContainerProxyFilter();
        filter.init(mockFilterConfig(null, null, null, FooFilter.class.getName()));
        MutablePicoContainer container = new DefaultPicoContainer();
        container.addComponent(Foo.class);
        container.addComponent("foo-filter", FooFilter.class);
        filter.lookupDelegate(mockRequest(container));
        filter.destroy();
    }
    
    @Test public void testInitDelegateFailsIfDelegateIsNotFound() throws Exception {
        ServletContainerProxyFilter filter = new ServletContainerProxyFilter();
        try {
            filter.initDelegate();
            fail("IllegalStateException expected");
        } catch ( IllegalStateException e) {
            assertEquals("Delegate filter was not set up", e.getMessage());
        }
    }
    
    @Test public void testDelegateLookupFailsIfNeitherKeyOrClassIsSpecified() throws Exception{
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
    
    @Test public void testDelegateLookupFailsIfClassCannotBeLoaded() throws Exception{
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
    
    @Test public void testDelegateLookupFailsIfFilterIsNotFoundInContainer() throws Exception{
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
    
    private FilterConfig mockFilterConfig(final String initType, final String lookupOnce, final String delegateKey, final String delegateClass) {
    	final FilterConfig filterConfig = mockery.mock(FilterConfig.class);
    	mockery.checking(new Expectations(){{
    		one(filterConfig).getInitParameter(with(equal("init-type")));
    		will(returnValue(initType));
    		one(filterConfig).getInitParameter(with(equal("lookup-only-once")));
    		will(returnValue(lookupOnce));
    		one(filterConfig).getInitParameter(with(equal("delegate-key")));
    		will(returnValue(delegateKey));
    		one(filterConfig).getInitParameter(with(equal("delegate-class")));
    		will(returnValue(delegateClass));
    	}});
        return filterConfig;
    }

    private HttpServletRequest mockRequest(final MutablePicoContainer container) {
    	final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
    	mockery.checking(new Expectations(){{
    		one(request).getAttribute(with(equal(KeyConstants.REQUEST_CONTAINER)));
    		will(returnValue(container));
    	}});
        return request;
    }

}