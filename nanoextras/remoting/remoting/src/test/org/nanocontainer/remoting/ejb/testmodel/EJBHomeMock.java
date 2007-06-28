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

import javax.ejb.EJBHome;
import javax.ejb.EJBMetaData;
import javax.ejb.Handle;
import javax.ejb.HomeHandle;


/**
 * Mock object for an EJBHome interface.
 * @author J&ouml;rg Schaible
 */

public class EJBHomeMock implements EJBHome {

    /**
     * @see javax.ejb.EJBHome#remove(java.lang.Object)
     */
    public void remove(Object arg0) /* throws RemoteException, RemoveException */{
        // do nothing
    }

    /**
     * @see javax.ejb.EJBHome#getEJBMetaData()
     */
    public EJBMetaData getEJBMetaData() /* throws RemoteException */{
        return null;
    }

    /**
     * @see javax.ejb.EJBHome#remove(javax.ejb.Handle)
     */
    public void remove(Handle arg0) /* throws RemoteException, RemoveException */{
        // do nothing
    }

    /**
     * @see javax.ejb.EJBHome#getHomeHandle()
     */
    public HomeHandle getHomeHandle() /* throws RemoteException */{
        return null;
    }

}
