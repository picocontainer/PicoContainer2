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

import javax.naming.InitialContext;
import javax.naming.NamingException;


/**
 * Mock class, that enables JMock with the CGLIB extension to mock an InitialContext itself. The class can be removed as
 * soon as JMock/CGLIB does not execute the default constructor of the mocked class at construction time automatically.
 * @author J&ouml;rg Schaible
 */
public class InitialContextMock extends InitialContext {
    public InitialContextMock() throws NamingException {
        super(true); // prevent internal call of init(Hashtable)
    }
}