/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file. 
 ******************************************************************************/
package org.picocontainer.script.groovy.nodes;

import java.util.Map;

import org.picocontainer.script.DefaultScriptedPicoContainer;
import org.picocontainer.script.ScriptedPicoContainer;

/**
 * @author Paul Hammant
 */
@SuppressWarnings("serial")
public class ClassLoaderNode extends AbstractBuilderNode {

    public static final String NODE_NAME = "classLoader";

    public ClassLoaderNode() {
        super(NODE_NAME);
    }

    public Object createNewNode(Object current, Map<String, Object> attributes) {

        ScriptedPicoContainer container = (ScriptedPicoContainer) current;
        return new DefaultScriptedPicoContainer(container.getComponentClassLoader(), container);
    }

}
