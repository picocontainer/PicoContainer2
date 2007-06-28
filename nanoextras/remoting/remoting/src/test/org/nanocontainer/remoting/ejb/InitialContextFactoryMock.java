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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.spi.InitialContextFactory;


/**
 * Mock for an InitialContextFactory. Such a factory has to be provided in the environment creating an InitialContext
 * for the entry Context.INITIAL_CONTEXT_FACTORY. This factory uses a static InitialContext instance, that can be a mock
 * itself.
 * @author J&ouml;rg Schaible
 */
public class InitialContextFactoryMock implements InitialContextFactory {

    private static InitialContext m_initialContext = null;

    public Context getInitialContext(final Hashtable environment) throws NamingException {
        if (m_initialContext != null) {
            return m_initialContext;
        } else {
            throw new NoInitialContextException();
        }
    }

    public static void setInitialContext(final InitialContext initialContext) {
        m_initialContext = initialContext;
    }

}