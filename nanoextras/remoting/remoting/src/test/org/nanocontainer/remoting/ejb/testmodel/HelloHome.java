/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.nanocontainer.remoting.ejb.testmodel;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;


/** EJB Home interface */
public interface HelloHome extends EJBHome {
    /**
     * @return Returns the EJB
     * @throws CreateException
     * @throws RemoteException
     */
    public Hello create() throws CreateException;
}