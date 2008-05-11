/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.webwork2;

import com.opensymphony.xwork2.*;

import java.util.Map;

import org.nanocontainer.nanowar.webwork2.PicoActionInvocation;

/**
 * Extension of XWork's {@link com.opensymphony.xwork.ActionProxyFactory ActionProxyFactory}
 * which creates PicoActionInvocations.
 * 
 * @author Chris Sturm
 * @see PicoActionInvocation
 * @deprecated Use DefaultActionProxyFactory 
 */
public class PicoActionProxyFactory extends DefaultActionProxyFactory {

    public ActionInvocation createActionInvocation(ObjectFactory objectFactory, UnknownHandler unknownHandler, ActionProxy actionProxy, Map extraContext) throws Exception {
        return new PicoActionInvocation(objectFactory,  unknownHandler, actionProxy, extraContext);
    }

    public ActionInvocation createActionInvocation(ObjectFactory objectFactory, UnknownHandler unknownHandler, ActionProxy actionProxy, Map extraContext, boolean pushAction) throws Exception {
        return new PicoActionInvocation(objectFactory,  unknownHandler, actionProxy, extraContext, pushAction);
    }
}
