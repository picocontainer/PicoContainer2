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
import javax.ejb.EJBObject;
import javax.ejb.Handle;


/**
 * Mock class for an EJBObject.
 * @author J&ouml;rg Schaible
 */
public class EJBObjectMock implements EJBObject {

    /**
     * @see javax.ejb.EJBObject#remove()
     */
    public void remove() /* throws RemoteException, RemoveException */{
        // Nothing
    }

    /**
     * @see javax.ejb.EJBObject#getPrimaryKey()
     */
    public Object getPrimaryKey() /* throws RemoteException */{
        return null;
    }

    /**
     * @see javax.ejb.EJBObject#getEJBHome()
     */

    public EJBHome getEJBHome() /* throws RemoteException */{
        return null;
    }

    /**
     * @see javax.ejb.EJBObject#isIdentical(javax.ejb.EJBObject)
     */
    public boolean isIdentical(EJBObject arg0) /* throws RemoteException */{
        return false;
    }

    /**
     * @see javax.ejb.EJBObject#getHandle()
     */
    public Handle getHandle() /* throws RemoteException */{
        return null;
    }
}
