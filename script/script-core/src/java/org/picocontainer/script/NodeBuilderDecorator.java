/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.script;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ComponentFactory;

import java.util.Map;

/**
 * NodeBuilderDecorators allows to dynamically extend node-based builder syntax,
 * such as
 * {@link org.picocontainer.script.groovy.GroovyNodeBuilder GroovyNodeBuilder}.
 * 
 * The
 * {@link org.picocontainer.aop.defaults.AopNodeBuilderDecorator AopNodeBuilderDecorator}
 * allows the scripting of aspects.
 * 
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 */
public interface NodeBuilderDecorator {

    ComponentFactory decorate(ComponentFactory componentFactory, Map attributes);

    MutablePicoContainer decorate(MutablePicoContainer picoContainer);

    Object createNode(Object name, Map attributes, Object parentElement);

    void rememberComponentKey(Map attributes);

}
