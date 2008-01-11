/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.nanowar.webwork;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import webwork.action.ActionContext;

public class ActionContextScopeObjectReferenceTestCase {    
    
    @Test public void testReferenceCanBeFound() throws Exception {
        ActionContext.getContext().put("foo","bar");
        assertEquals("bar",(new ActionContextScopeReference("foo")).get());
    }
}