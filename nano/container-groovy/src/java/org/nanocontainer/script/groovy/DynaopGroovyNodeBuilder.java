/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.script.groovy;

import org.nanocontainer.aop.defaults.AopNodeBuilderDecorationDelegate;
import org.nanocontainer.aop.dynaop.DynaopAspectsManager;

/**
 * A {@link org.nanocontainer.script.groovy.GroovyNodeBuilder} that supports
 * scripting of aspects via dynaop.
 *
 * @author Stephen Molitor
 * @version $Revision$
 */
public class DynaopGroovyNodeBuilder extends GroovyNodeBuilder {

    /**
     * Creates a new <code>DynaopGroovyNodeBuilder</code> that will use
     * the default @{link DynaopAspectsManager} to apply aspects.
     */
    public DynaopGroovyNodeBuilder() {
        super(new AopNodeBuilderDecorationDelegate(new DynaopAspectsManager()), GroovyNodeBuilder.SKIP_ATTRIBUTE_VALIDATION);
    }


}
