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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nanocontainer.remoting.ejb.testmodel.Hello;
import org.nanocontainer.remoting.ejb.testmodel.HelloHomeImpl;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentFactory;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;


/**
 * Unit test for EJBClientBehaviorFactory.
 * @author J&ouml;rg Schaible
 */
@RunWith(JMock.class)
public class EJBClientBehaviorFactoryTest {

	private Mockery mockery = mockeryWithClassImposteriser();
	
    private InitialContextMock initialContext = (InitialContextMock) mockery.mock(InitialContextMock.class);
    private Properties systemProperties;

    private Mockery mockeryWithClassImposteriser() {
		Mockery mockery = mockeryWithCountingNamingScheme();
		mockery.setImposteriser(ClassImposteriser.INSTANCE);
		return mockery;
	}

    @Before public void setUp() throws Exception {
        systemProperties = System.getProperties();
        InitialContextFactoryMock.setInitialContext(initialContext);
    }

    @After public void tearDown() throws Exception {
        InitialContextFactoryMock.setInitialContext(null);
        System.setProperties(systemProperties);
    }

    /**
     * Test for standard constructor using system InitialContext
     * @throws NamingException 
     */
    @Test public void testSystemInitialContext() throws NamingException {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryMock.class.getName());
        final ComponentFactory componentFactory = new EJBClientBehaviorFactory();
        final ComponentAdapter componentAdapter = componentFactory.createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), null, "Hello", Hello.class, null);
        assertNotNull(componentAdapter);
        final Object hello1 = componentAdapter.getComponentInstance(null);
        final Object hello2 = componentAdapter.getComponentInstance(null);
        assertNotNull(hello1);
        assertTrue(Hello.class.isAssignableFrom(hello1.getClass()));
        mockery.checking(new Expectations(){{
        	one(initialContext).lookup(with(equal("Hello")));
        	will(returnValue(new HelloHomeImpl()));        	
        }});
        assertEquals(hello1.hashCode(), hello2.hashCode());
    }

    /**
     * Test for constructor using a prepared environment for the InitialContext
     * @throws NamingException 
     */
    @Test public void testPreparedInitialContext() throws NamingException {
        final Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryMock.class.getName());
        final ComponentFactory componentFactory = new EJBClientBehaviorFactory(env);
        final ComponentAdapter componentAdapter = componentFactory.createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), null, "Hello", Hello.class, null);
        assertNotNull(componentAdapter);
        final Object hello1 = componentAdapter.getComponentInstance(null);
        final Object hello2 = componentAdapter.getComponentInstance(null);
        assertNotNull(hello1);
        assertTrue(Hello.class.isAssignableFrom(hello1.getClass()));
        mockery.checking(new Expectations(){{
        	one(initialContext).lookup(with(equal("Hello")));
        	will(returnValue(new HelloHomeImpl()));        	
        }});
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
