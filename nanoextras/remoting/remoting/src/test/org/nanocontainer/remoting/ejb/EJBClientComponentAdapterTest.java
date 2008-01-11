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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import java.lang.reflect.UndeclaredThrowableException;
import java.net.SocketTimeoutException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Properties;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import javax.ejb.EJBObject;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nanocontainer.remoting.ejb.testmodel.BarHomeImpl;
import org.nanocontainer.remoting.ejb.testmodel.FooBar;
import org.nanocontainer.remoting.ejb.testmodel.FooBarHome;
import org.nanocontainer.remoting.ejb.testmodel.FooHomeImpl;
import org.nanocontainer.remoting.ejb.testmodel.Hello;
import org.nanocontainer.remoting.ejb.testmodel.HelloHome;
import org.nanocontainer.remoting.ejb.testmodel.HelloHomeImpl;
import org.nanocontainer.remoting.ejb.testmodel.HelloImpl;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoCompositionException;


/**
 * Unit test for EJBClientAdapter.
 * @author J&ouml;rg Schaible
 */
public class EJBClientComponentAdapterTest {

private Mockery mockery = mockeryWithClassImposteriser();
	
    private InitialContextMock initialContext = (InitialContextMock) mockery.mock(InitialContextMock.class);
    private Hashtable environment;

    private Mockery mockeryWithClassImposteriser() {
		Mockery mockery = mockeryWithCountingNamingScheme();
		mockery.setImposteriser(ClassImposteriser.INSTANCE);
		return mockery;
	}
    
    @Before public void setUp() throws Exception {       
        InitialContextFactoryMock.setInitialContext(initialContext);        
        environment = new Hashtable();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryMock.class.getName());
    }
    
    @After public void tearDown() throws Exception {
        InitialContextFactoryMock.setInitialContext(null);
    }

    /**
     * Test for constructor of EJBClientAdapter
     */
    @Test public void testConstructors() {
        try {
            new EJBClientAdapter("Foo", Test.class);
            fail("Should have thrown a ClassNotFoundException");
        } catch (ClassNotFoundException e) {
            assertTrue(e.getMessage().indexOf("TestHome") > 0);
        }
        try {
            new EJBClientAdapter("Bar", FooBar.class, FooBar.class, environment, false);
            fail("Should have thrown a AssignabilityRegistrationException");
        } catch (ClassCastException e) {
            assertTrue(e.getMessage().indexOf(EJBHome.class.getName()) > 0);
        }
        try {
            new EJBClientAdapter("Bar", FooBarHome.class, FooBarHome.class, environment, false);
            fail("Should have thrown a AssignabilityRegistrationException");
        } catch (ClassCastException e) {
            assertTrue(e.getMessage().indexOf(EJBObject.class.getName()) > 0);
        }
        try {
            new EJBClientAdapter("Hello", HelloImpl.class, HelloHome.class, environment, false);
            fail("Should have thrown a PicoCompositionException");
        } catch (PicoCompositionException e) {
            assertTrue(e.getMessage().endsWith("interface"));
        }
    }

    /**
     * Test lookup failures with JNDI
     * @throws ClassNotFoundException
     * @throws RemoteException
     * @throws NamingException 
     */
    @Test public void testJNDILookup() throws ClassNotFoundException, RemoteException, NamingException {
    	mockery.checking(new Expectations(){{
    		one(initialContext).lookup(with(equal("Foo")));
          	will(returnValue(new FooHomeImpl()));        	
        }});
        try {
            new EJBClientAdapter("Foo", FooBar.class, environment, true);
            fail("Should have thrown a PicoCompositionException");
        } catch (PicoCompositionException e) {
            assertTrue(e.getCause() instanceof NoSuchMethodException);
        }
       	mockery.checking(new Expectations(){{
    		one(initialContext).lookup(with(equal("Bar")));
          	will(returnValue(new BarHomeImpl()));        	
        }});
        try {
            new EJBClientAdapter("Bar", FooBar.class, environment, true);
            fail("Should have thrown a PicoCompositionException");
        } catch (PicoCompositionException e) {
            assertTrue(e.getCause() instanceof ClassCastException);
        }
      	mockery.checking(new Expectations(){{
    		one(initialContext).lookup(with(equal("NoEntry")));
          	will(throwException(new NameNotFoundException()));        	
        }});
        try {
            new EJBClientAdapter("NoEntry", Hello.class, environment, true);
            fail("Should have thrown a UndeclaredThrowableException with a NamingException as cause");
        } catch (UndeclaredThrowableException e) {
            assertTrue(e.getCause() instanceof NamingException);
        }
        final Properties systemProperties = System.getProperties();
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "foo.Bar");
        final ComponentAdapter componentAdapter = new EJBClientAdapter("Home", Hello.class);
        final Hello hello = (Hello)componentAdapter.getComponentInstance(null);
        try {
            hello.getHelloWorld();
            fail("Should have thrown a PicoCompositionException");
        } catch (PicoCompositionException e) {
            assertTrue(e.getCause() instanceof NoInitialContextException);
        } finally {
            System.setProperties(systemProperties);
        }
    }

    /**
     * Test failures creating the EJB.
     * @throws ClassNotFoundException
     * @throws RemoteException
     * @throws NamingException 
     * @throws CreateException 
     */
    @Test public void testComponentCreationFailures() throws ClassNotFoundException, RemoteException, NamingException, CreateException {
        final HelloHome helloHome = mockery.mock(HelloHome.class);
        final Hello hello = mockery.mock(Hello.class);
        mockery.checking(new Expectations(){{
    		atLeast(1).of(initialContext).lookup(with(equal("Hello")));
          	will(returnValue(helloHome));      	
        }});
        final Throwable t = new SecurityException("junit");
    	mockery.checking(new Expectations(){{
    		one(initialContext).lookup(with(equal("NoEntry")));
          	will(throwException(new NameNotFoundException()));        	
        }});
      	mockery.checking(new Expectations(){{
    		one(helloHome).create();
          	will(throwException(t));        	
        }});
        try {
            new EJBClientAdapter("Hello", Hello.class, environment, true);
            fail("Should have thrown a SecurityException");
        } catch (SecurityException e) {
            assertSame(t, e);
        }
        final Throwable t2 = new RuntimeException("junit");
        mockery.checking(new Expectations(){{
    		one(helloHome).create();
          	will(throwException(t2));        	
        }});
        try {
            new EJBClientAdapter("Hello", Hello.class, environment, true);
            fail("Should have thrown a RuntimeException");
        } catch (RuntimeException e) {
            assertSame(t2, e);
        }
        final Throwable t3 = new Error("junit");
        mockery.checking(new Expectations(){{
    		one(helloHome).create();
          	will(throwException(t3));        	
        }});
        try {
            new EJBClientAdapter("Hello", Hello.class, environment, true);
            fail("Should have thrown an Error");
        } catch (Error e) {
            assertSame(t3, e);
        }
        final Throwable t4 = new CreateException("junit");
        mockery.checking(new Expectations(){{
    		one(helloHome).create();
          	will(throwException(t4));        	
        }});
        try {
            new EJBClientAdapter("Hello", Hello.class, environment, true);
            fail("Should have thrown a UndeclaredThrowableException");
        } catch (UndeclaredThrowableException e) {
            assertSame(t4, e.getCause());
        }
        final ComponentAdapter componentAdapter = new EJBClientAdapter(
                "Hello", Hello.class, environment, false);
        mockery.checking(new Expectations(){{
    		one(helloHome).create();
          	will(returnValue(hello));  	
        }});
        final Hello helloInstance = (Hello)componentAdapter.getComponentInstance(null);
        final Throwable t5 = new RemoteException("junit");
        mockery.checking(new Expectations(){{
    		one(hello).getHelloWorld();
          	will(throwException(t5));
        }});
        try {
            hello.getHelloWorld();
            fail("Should have thrown a RemoteException");
        } catch (Exception e) {
            assertSame(t5, e);
        }
        final Throwable t6 = new NoSuchObjectException("junit");
        mockery.checking(new Expectations(){{
    		one(helloHome).create();
          	will(returnValue(hello));      	
    		one(hello).getHelloWorld();
          	will(throwException(t6));
        }});
        try {
            hello.getHelloWorld();
            fail("Should have thrown a NoSuchObjectException");
        } catch (Exception e) {
            assertSame(t6, e);
        }
    }

    /**
     * Test for failover capability.
     * @throws ClassNotFoundException
     * @throws RemoteException
     * @throws NamingException 
     */
    @Test public void testFailover() throws ClassNotFoundException, RemoteException, NamingException {
        final ComponentAdapter componentAdapter = new EJBClientAdapter(
                "Hello", Hello.class, environment, false);
        final Hello hello = (Hello)componentAdapter.getComponentInstance(null);
        assertNotNull(hello);
        final NamingException exception = new CommunicationException();
        exception.setRootCause(new SocketTimeoutException());
        mockery.checking(new Expectations(){{
    		one(initialContext).lookup(with(equal("Hello"))); 
          	will(throwException(exception));
        }});
        try {
            hello.getHelloWorld();
            fail("Should have thrown a UndeclaredThrowableException");
        } catch (UndeclaredThrowableException e) {
            assertTrue(((NamingException)e.getCause()).getRootCause() instanceof SocketTimeoutException);
        }
        exception.setRootCause(new NoSuchObjectException("Hello"));
        final Sequence sequence2 = mockery.sequence("failover2");
        mockery.checking(new Expectations(){{
    		one(initialContext).lookup(with(equal("Hello"))); 
          	will(throwException(exception)); inSequence(sequence2);
    		one(initialContext).lookup(with(equal("Hello")));
          	will(returnValue(new HelloHomeImpl())); inSequence(sequence2);     	
        }});
        try {
            hello.getHelloWorld();
            fail("Should have thrown a UndeclaredThrowableException");
        } catch (UndeclaredThrowableException e) {
            assertTrue(((NamingException)e.getCause()).getRootCause() instanceof NoSuchObjectException);
        }
        assertEquals("Hello World!", hello.getHelloWorld());
    }

    /**
     * Test for EJBClientAdapter.getComponent()
     * @throws ClassNotFoundException
     * @throws RemoteException
     * @throws NamingException 
     */
    @Test public void testGetComponentInstance() throws ClassNotFoundException, RemoteException, NamingException {
    	mockery.checking(new Expectations(){{
    		one(initialContext).lookup(with(equal("Hello")));
          	will(returnValue(new HelloHomeImpl()));        	
        }});
        final ComponentAdapter componentAdapter = new EJBClientAdapter(
                "Hello", Hello.class, environment, true);
        componentAdapter.verify(null); // Dummy call, done for coverage
        final Hello hello = (Hello)componentAdapter.getComponentInstance(null);
        assertNotNull(hello);
        assertEquals("Hello World!", hello.getHelloWorld());
    }

    /**
     * Test for EJBClientAdapter.getComponentKey()
     * @throws ClassNotFoundException
     * @throws NamingException 
     */
    @Test public void testGetComponentKey() throws ClassNotFoundException, NamingException {
    	mockery.checking(new Expectations(){{
    		one(initialContext).lookup(with(equal("Hello")));
          	will(returnValue(new HelloHomeImpl()));        	
        }});
        final ComponentAdapter componentAdapter = new EJBClientAdapter(
                "Hello", Hello.class, environment, true);
        assertSame("Hello", componentAdapter.getComponentKey());
    }

    /**
     * Test for EJBClientAdapter.getComponentImplementation()
     * @throws ClassNotFoundException
     * @throws NamingException 
     */
    @Test public void testGetComponentImplementation() throws ClassNotFoundException, NamingException {
    	mockery.checking(new Expectations(){{
    		one(initialContext).lookup(with(equal("Hello")));
          	will(returnValue(new HelloHomeImpl()));        	
        }});
        final ComponentAdapter componentAdapter = new EJBClientAdapter(
                "Hello", Hello.class, environment, true);
        assertSame(Hello.class, componentAdapter.getComponentImplementation());
    }
}
