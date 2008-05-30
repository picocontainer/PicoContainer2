/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Michael Rimov                                            *
 *****************************************************************************/

package org.picocontainer.script.groovy.nodes;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.picocontainer.script.DefaultScriptedPicoContainer;
import org.picocontainer.script.ScriptedPicoContainerMarkupException;
import org.picocontainer.script.ScriptedPicoContainer;
import org.picocontainer.script.groovy.nodes.AppendContainerNode;

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
        } catch (ScriptedPicoContainerMarkupException ex) {
            //ok
        }
    }

    @Test public void testCreateNodeWithParmeterReturnsParameter() throws ScriptedPicoContainerMarkupException {
        HashMap params = new HashMap();
        ScriptedPicoContainer scripted = new DefaultScriptedPicoContainer();
        params.put(AppendContainerNode.CONTAINER, scripted);
        ScriptedPicoContainer scripted2 = (ScriptedPicoContainer)appendContainerNode.createNewNode(null,params);
        assertTrue(scripted == scripted2);
    }

    @Test public void testCreateWithImproperTypeThrowsClassCastException() throws ScriptedPicoContainerMarkupException {
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
