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

import java.util.Collections;
import java.util.HashMap;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.script.NanoContainerMarkupException;
import junit.framework.TestCase;

/**
 * Tests node marking and exceptions
 * @author Michael Rimov
 */
public class TestAppendContainerNode extends TestCase {
    private AppendContainerNode appendContainerNode = null;

    protected void setUp() throws Exception {
        super.setUp();
        appendContainerNode = new AppendContainerNode();
    }

    protected void tearDown() throws Exception {
        appendContainerNode = null;
        super.tearDown();
    }

    public void testCreateNewNodeWithoutParameterThrowsException() {
        try {
            appendContainerNode.createNewNode(null, Collections.EMPTY_MAP);
            fail("Should have thrown exception");
        } catch (NanoContainerMarkupException ex) {
            //ok
        }
    }

    public void testCreateNodeWithParmeterReturnsParameter() throws NanoContainerMarkupException {
        HashMap params = new HashMap();
        NanoContainer nano = new DefaultNanoContainer();
        params.put(AppendContainerNode.CONTAINER, nano);
        NanoContainer nano2 = (NanoContainer)appendContainerNode.createNewNode(null,params);
        assertTrue(nano == nano2);
    }

    public void testCreateWithImproperTypeThrowsClassCastException() throws NanoContainerMarkupException {
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
