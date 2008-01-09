/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Michael Rimov                                            *
 *****************************************************************************/

package org.nanocontainer.script.groovy.buildernodes;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.script.NanoContainerMarkupException;

/**
 * Tests node marking and exceptions
 * @author Michael Rimov
 */
public class TestAppendContainerNode {
    private AppendContainerNode appendContainerNode = null;

    @Before public void setUp() throws Exception {
        appendContainerNode = new AppendContainerNode();
    }

    @After public void tearDown() throws Exception {
        appendContainerNode = null;
    }

    @Test public void testCreateNewNodeWithoutParameterThrowsException() {
        try {
            appendContainerNode.createNewNode(null, Collections.EMPTY_MAP);
            fail("Should have thrown exception");
        } catch (NanoContainerMarkupException ex) {
            //ok
        }
    }

    @Test public void testCreateNodeWithParmeterReturnsParameter() throws NanoContainerMarkupException {
        HashMap params = new HashMap();
        NanoContainer nano = new DefaultNanoContainer();
        params.put(AppendContainerNode.CONTAINER, nano);
        NanoContainer nano2 = (NanoContainer)appendContainerNode.createNewNode(null,params);
        assertTrue(nano == nano2);
    }

    @Test public void testCreateWithImproperTypeThrowsClassCastException() throws NanoContainerMarkupException {
        HashMap params = new HashMap();
        params.put(AppendContainerNode.CONTAINER, "This is a test");
        try {
            appendContainerNode.createNewNode(null, params);
            fail("Should have thrown exception");
        } catch (ClassCastException ex) {
            //ok
        }
    }

}
