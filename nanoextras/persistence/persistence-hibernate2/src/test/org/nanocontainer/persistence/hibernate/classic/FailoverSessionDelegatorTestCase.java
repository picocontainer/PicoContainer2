/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.persistence.hibernate.classic;

import java.io.Serializable;
import java.util.Iterator;

import junit.framework.TestCase;
import net.sf.hibernate.CallbackException;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Interceptor;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.type.Type;


/**
 * test case for session delegator
 * 
 * @author Konstantin Pribluda
 * @version $Revision: 2043 $
 */
public class FailoverSessionDelegatorTestCase extends TestCase {

	public void testSessionCreationAndDisposal() throws Exception {

		SessionFactory factory = (new ConstructableConfiguration()).buildSessionFactory();

		FailoverSessionDelegator delegator = new FailoverSessionDelegator(factory);

		Session session = delegator.getDelegatedSession();
		assertNotNull(session);

		assertSame(session, delegator.getDelegatedSession());

		// test that closing invalidates session
		delegator.close();
		assertNull(delegator.session);
		assertNotSame(session, delegator.getDelegatedSession());

		session = delegator.getDelegatedSession();

		// produce error
		try {
			assertNotNull(delegator.save(new Pojo()));
			fail("did not bombed on hibernate error");
		} catch (HibernateException ex) {
			// that's ok
		}
		assertNotSame(session, delegator.getDelegatedSession());

	}

	boolean wasFlushed = false;

	/**
	 * test that interceptor is injected
	 * 
	 * @throws Exception
	 */
	public void testIterceptorInjection() throws Exception {

		Interceptor interceptor = new Interceptor() {

			public boolean onLoad(Object arg0, Serializable arg1, Object[] arg2, String[] arg3, Type[] arg4) {
				return false;
			}

			public boolean onFlushDirty(Object arg0, Serializable arg1, Object[] arg2, Object[] arg3, String[] arg4, Type[] arg5) {
				return false;
			}

			public boolean onSave(Object arg0, Serializable arg1, Object[] arg2, String[] arg3, Type[] arg4) {
				return false;
			}

			public void onDelete(Object arg0, Serializable arg1, Object[] arg2, String[] arg3, Type[] arg4) {
			}

			public void preFlush(Iterator arg0) {
				wasFlushed = true;
			}

			public void postFlush(Iterator arg0) {
			}

			public Boolean isUnsaved(Object arg0) {
				return null;
			}

			public int[] findDirty(Object arg0, Serializable arg1, Object[] arg2, Object[] arg3, String[] arg4, Type[] arg5) {
				return null;
			}

			public Object instantiate(Class arg0, Serializable arg1) {
				return null;
			}
		};

		ConstructableConfiguration configuration = new ConstructableConfiguration();
		SessionFactory factory = configuration.buildSessionFactory();
	

		
		FailoverSessionDelegator delegator = new FailoverSessionDelegator(factory, interceptor);
	
		delegator.flush();
		assertTrue(wasFlushed);

	}
}
