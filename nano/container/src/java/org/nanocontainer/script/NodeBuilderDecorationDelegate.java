/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant and Aslak Helles&oslash;y                   *
 *****************************************************************************/

package org.nanocontainer.script;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ComponentFactory;

import java.util.Map;

/**
 * Implementors of this class can be passed to {@link org.nanocontainer.script.groovy.GroovyNodeBuilder}'s constructor
 * to dynamically extend the core builder syntax.
 *
 * Note: Although this interface is currently only used by the Groovy {@link org.nanocontainer.script.groovy.GroovyNodeBuilder}
 * class, there is nothing groovy going on here - nor in the AOP subclass {@link org.nanocontainer.aop.defaults.AopNodeBuilderDecorationDelegate}
 * class. In other words, it should be easy to add AOP capabilities to the other scripting engines such as the XML ones.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface NodeBuilderDecorationDelegate {

    ComponentFactory decorate(ComponentFactory componentFactory, Map attributes);

    MutablePicoContainer decorate(MutablePicoContainer picoContainer);

    Object createNode(Object name, Map attributes, Object parentElement);

    void rememberComponentKey(Map attributes);
}
