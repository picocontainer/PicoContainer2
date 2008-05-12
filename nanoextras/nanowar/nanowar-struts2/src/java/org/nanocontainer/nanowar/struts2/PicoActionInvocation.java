/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.struts2;

import java.util.Map;

import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.DefaultActionInvocation;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.UnknownHandler;

/**
 * Implementation of {@link com.opensymphony.xwork.ActionInvocation ActionInvocation}
 * which uses a PicoContainer to create Action instances.
 * 
 * @author Chris Sturm
 * @author Aslak Helles&oslash;y
 * @deprecated Use DefaultActionInvocation 
 */
public class PicoActionInvocation extends DefaultActionInvocation {
    
    private static final long serialVersionUID = 8877588734538436197L;

    public PicoActionInvocation(ObjectFactory objectFactory, UnknownHandler unknownHandler, ActionProxy proxy, Map extraContext) throws Exception {
        super(objectFactory,  unknownHandler, proxy, extraContext);
    }

    public PicoActionInvocation(ObjectFactory objectFactory, UnknownHandler unknownHandler, ActionProxy proxy, Map extraContext, boolean pushAction) throws Exception {
        super(objectFactory,  unknownHandler, proxy, extraContext, pushAction);
    }

}
