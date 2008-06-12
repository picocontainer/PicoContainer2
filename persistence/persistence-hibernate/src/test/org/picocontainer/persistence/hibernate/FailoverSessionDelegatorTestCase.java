/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/

package org.picocontainer.persistence.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.hibernate.EmptyInterceptor;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Test;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;
import org.picocontainer.parameters.ConstantParameter;

/**
 * Test case for failover session delegator
 */
public class FailoverSessionDelegatorTestCase {
	
	
	/**
	 * This is an example showing integration of the Pico-Persistence Hibernate components in action while
	 * playing together.
	 * @throws Exception
	 */
	@Test
	public void testPicoContainerIntegrationLifecycleIntegration() throws Exception {
		MutablePicoContainer mpc = new PicoBuilder().withLifecycle().withCaching().build();
		mpc.addComponent(Configuration.class, ConstructableConfiguration.class, new ConstantParameter("/hibernate.cfg.xml"));
		mpc.addComponent(SessionFactory.class, ConfigurableSessionFactory.class);
		mpc.start();
		
		MutablePicoContainer requestContainer = mpc.makeChildContainer();
		//Normally you would use ThreadLocal Storage with this component.
		requestContainer.addComponent(Session.class, FailoverSessionDelegator.class);
		requestContainer.start();
		
		Session session = requestContainer.getComponent(Session.class);
		assertNotNull(session);
		assertTrue(session instanceof FailoverSessionDelegator);
		FailoverSessionDelegator delegator = (FailoverSessionDelegator)session;
		
        Session realHibernateSession = delegator.getDelegatedSession();
        assertNotNull(realHibernateSession);

        
        assertSame("Repeated simple calls to getDelegatedSession() should return the same Hibernate session",
        		realHibernateSession, delegator.getDelegatedSession());
        
        
        requestContainer.stop();
        requestContainer.start();
        
        assertNotSame("getDelegatedSession() after request container is stopped should not be the same.", 
        		realHibernateSession, delegator.getDelegatedSession());
        assertFalse("After container stop, hibernate sessions should be disconnected", realHibernateSession.isConnected());
        
        requestContainer.stop();
        requestContainer.dispose();
        mpc.removeChildContainer(requestContainer);
        
        try {
        	delegator.getDelegatedSession();
        	fail("Delegated session should not allow interactions after dispose()");
        } catch (Exception ex) {
        	//a-ok
        	assertNotNull(ex.getMessage());
        }
        
        mpc.stop();
        mpc.dispose();
	}

    @Test 
    public void testSessionCreationAndDisposal() throws Exception {    	
        SessionFactory factory = (new ConstructableConfiguration("/hibernate.cfg.xml")).buildSessionFactory();
        FailoverSessionDelegator delegator = new FailoverSessionDelegator(factory);
        Session session = delegator.getDelegatedSession();
        assertNotNull(session);

        assertSame(session, delegator.getDelegatedSession());

        // test that closing invalidates session
        delegator.close();

        assertNotSame(session, delegator.getDelegatedSession());
        session = delegator.getDelegatedSession();

        // produce error
        try {
            assertNotNull(delegator.save(new Pojo()));
            fail("did not bombed on hibernate error");
        } catch (HibernateException e) {
            // that's ok
        	assertNotNull(e.getMessage());
        }

        assertNotSame(session, delegator.getDelegatedSession());
    }

    /**
     * Tests that basic save load works with this session delegator.
     */
    @Test
    public void assertPojoCanBeSaved() {
    	Pojo pojo = new Pojo();
    	pojo.setFoo("This is a test");
        SessionFactory factory = (new ConstructableConfiguration("/hibernate.cfg.xml")).buildSessionFactory();
        FailoverSessionDelegator session = new FailoverSessionDelegator(factory);
        
        try {
            Integer result = (Integer) session.save(pojo);
            assertNotNull(result);

            //Normally Close will force a new session to be created in the background the next time it is used.
            session.close();
            
            //We're just doing it this way to show that it works with normal hibenate procedures.
            session = new FailoverSessionDelegator(factory);

            Fooable pojo2 = (Fooable) session.load(pojo.getClass(), result);
            assertNotNull(pojo);
            assertEquals(pojo.getId(), pojo2.getId());
            assertEquals(pojo.getFoo(), pojo2.getFoo());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
            factory.close();
        }
    }    
    
    @Test
    public void testStringAndHashcodeDoesntBombOnNullSession() {
        SessionFactory factory = (new ConstructableConfiguration("/hibernate.cfg.xml")).buildSessionFactory();
        FailoverSessionDelegator session = new FailoverSessionDelegator(factory);
        int beforeSessionHashCode  = session.hashCode();
        assertTrue(beforeSessionHashCode > 0);
        assertNotNull(session.toString());
        
        //Force lazy creation of a session.
        Session hibernateSession = session.getDelegatedSession();
        assertNotNull(hibernateSession);
        
        //If this is failing, we're getting too many collisions!  Rework null hashcode handling.
        assertNotSame(beforeSessionHashCode, session.hashCode());
        
        assertNotNull(session.toString());
    }
    
   
    @Test
    public void testCannotSetInterceptorAfterConnectionRetrieved() {
        SessionFactory factory = (new ConstructableConfiguration("/hibernate.cfg.xml")).buildSessionFactory();
        FailoverSessionDelegator session = new FailoverSessionDelegator(factory);
        session.setInterceptor(EmptyInterceptor.INSTANCE);
        assertEquals(EmptyInterceptor.INSTANCE, session.getInterceptor());
        
        //Force creation of delegate interceptor.
        session.getDelegatedSession();
        
        try {
			session.setInterceptor(EmptyInterceptor.INSTANCE);
		} catch (IllegalStateException e) {
			//A-ok
			assertNotNull(e.getMessage());
		}
        
    	
    }
}
