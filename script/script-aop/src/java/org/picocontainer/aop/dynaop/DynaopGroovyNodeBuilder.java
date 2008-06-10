/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.picocontainer.aop.dynaop;

import org.picocontainer.aop.defaults.AopNodeBuilderDecorator;
import org.picocontainer.script.groovy.GroovyNodeBuilder;

/**
 * A {@link org.picocontainer.script.groovy.GroovyNodeBuilder GroovyNodeBuilder} that supports
 * scripting of aspects via dynaop.
 *
 * @author Stephen Molitor
 */
public class DynaopGroovyNodeBuilder extends GroovyNodeBuilder {

    /**
     * Creates a new DynaopGroovyNodeBuilder that will use
     * a {@link DynaopAspectsManager} to apply aspects.
     */
    public DynaopGroovyNodeBuilder() {
        super(new AopNodeBuilderDecorator(new DynaopAspectsManager()), GroovyNodeBuilder.SKIP_ATTRIBUTE_VALIDATION);
    }


}
