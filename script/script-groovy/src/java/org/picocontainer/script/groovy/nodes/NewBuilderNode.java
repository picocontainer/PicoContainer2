/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.script.groovy.nodes;

import java.util.Map;

import groovy.lang.GroovyObject;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.script.ClassName;
import org.picocontainer.script.DefaultScriptedPicoContainer;
import org.picocontainer.script.ScriptedPicoContainer;

/**
 * Handles the child of container 'newBuilder' node.
 * 
 * @author James Strachan
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Michael Rimov
 * @author Mauro Talevi
 */
@SuppressWarnings("serial")
public class NewBuilderNode extends AbstractBuilderNode {

    /**
     * Node name we're handling: 'newBuilder'.
     */
    public static final String NODE_NAME = "newBuilder";

    /**
     * Supported attribute: 'class'.
     */
    public static final String CLASS_ATTRIBUTE = "class";

    /**
     * Supported attribute 'validating'. Indicates that attributes should be
     * validated and ScriptedPicoContainerMarkupException should be thrown if
     * invalid attributes are found.
     * 
     * @todo Not yet implemented. How do we get PicoContainer to register a
     *       component instance? -MR
     */
    public static final String VALIDATE_ATTRIBUTE = "validating";

    public NewBuilderNode() {
        super(NODE_NAME);

        addAttribute(CLASS_ATTRIBUTE);
        addAttribute(VALIDATE_ATTRIBUTE);
    }

    public Object createNewNode(final Object current, final Map<String,Object> attributes) {
        Object builderClass = attributes.remove(CLASS_ATTRIBUTE);

        ScriptedPicoContainer factory = new DefaultScriptedPicoContainer();
        MutablePicoContainer parentPico = ((ScriptedPicoContainer) current);
        factory.addComponent(MutablePicoContainer.class, parentPico);
        if (builderClass instanceof String) {
            factory.addComponent(GroovyObject.class, new ClassName((String) builderClass));
        } else {
            factory.addComponent(GroovyObject.class, builderClass);
        }
        return factory.getComponent(GroovyObject.class);
    }

}
