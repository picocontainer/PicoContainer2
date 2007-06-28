/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/

package org.nanocontainer.persistence.hibernate;

import junit.framework.TestCase;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Test case for failover session delegator
 * 
 * @version $Revision: 2043 $
 */
public class FailoverSessionDelegatorTestCase extends TestCase {

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
        } catch (HibernateException ex) {
            // that's ok
        }

        assertNotSame(session, delegator.getDelegatedSession());
    }

}
