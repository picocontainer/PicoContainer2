/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.nanocontainer.remoting.ejb;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.nanocontainer.remoting.ejb.testmodel.Hello;
import org.nanocontainer.remoting.ejb.testmodel.HelloHomeImpl;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentFactory;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

import junit.framework.Test;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;


/**
 * Unit test for EJBClientBehaviorFactory.
 * @author J&ouml;rg Schaible
 */
public class EJBClientBehaviorFactoryTest extends MockObjectTestCase {

    private Mock m_initialContextMock;
    private Properties m_systemProperties;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        m_systemProperties = System.getProperties();

        m_initialContextMock = mock(InitialContextMock.class);
        InitialContextFactoryMock.setInitialContext((InitialContext)m_initialContextMock.proxy());
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        InitialContextFactoryMock.setInitialContext(null);
        System.setProperties(m_systemProperties);
        super.tearDown();
    }

    /**
     * Test for standard constructor using system InitialContext
     */
    public final void testSystemInitialContext() {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryMock.class.getName());
        final ComponentFactory componentFactory = new EJBClientBehaviorFactory();
        final ComponentAdapter componentAdapter = componentFactory.createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), null, "Hello", Hello.class, null);
        assertNotNull(componentAdapter);
        final Object hello1 = componentAdapter.getComponentInstance(null);
        final Object hello2 = componentAdapter.getComponentInstance(null);
        assertNotNull(hello1);
        assertTrue(Hello.class.isAssignableFrom(hello1.getClass()));
        m_initialContextMock.expects(once()).method("lookup").with(eq("Hello")).will(returnValue(new HelloHomeImpl()));
        assertEquals(hello1.hashCode(), hello2.hashCode());
    }

    /**
     * Test for constructor using a prepared environment for the InitialContext
     */
    public final void testPreparedInitialContext() {
        final Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryMock.class.getName());
        final ComponentFactory componentFactory = new EJBClientBehaviorFactory(env);
        final ComponentAdapter componentAdapter = componentFactory.createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), null, "Hello", Hello.class, null);
        assertNotNull(componentAdapter);
        final Object hello1 = componentAdapter.getComponentInstance(null);
        final Object hello2 = componentAdapter.getComponentInstance(null);
        assertNotNull(hello1);
        assertTrue(Hello.class.isAssignableFrom(hello1.getClass()));
        m_initialContextMock.expects(once()).method("lookup").with(eq("Hello")).will(returnValue(new HelloHomeImpl()));
        assertEquals(hello1.hashCode(), hello2.hashCode());
    }

    /**
     * Test if a underlaying ClassNotFoundException is converted into a PicoIntrospectionExcpetion
     */
    public void testClassNotFoundIsConverted() {
        final Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryMock.class.getName());
        final ComponentFactory componentFactory = new EJBClientBehaviorFactory(env, true);
        try {
            componentFactory.createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), null, "Foo", Test.class, null);
            fail("Should have thrown a PicoCompositionException");
        } catch (PicoCompositionException e) {
            assertTrue(e.getCause() instanceof ClassNotFoundException);
        }

    }
}
