/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.script;

import org.jmock.MockObjectTestCase;
import org.picocontainer.PicoContainer;
import org.picocontainer.ObjectReference;
import org.picocontainer.behaviors.CachingBehavior;

public abstract class AbstractScriptedContainerBuilderTestCase extends MockObjectTestCase {
    private final ObjectReference containerRef = new CachingBehavior.SimpleReference();
    private final ObjectReference parentContainerRef = new CachingBehavior.SimpleReference();

    protected PicoContainer buildContainer(ScriptedContainerBuilder builder, PicoContainer parentContainer, Object scope) {
        parentContainerRef.set(parentContainer);
        builder.buildContainer(containerRef, parentContainerRef, scope, true);
        return (PicoContainer) containerRef.get();
    }
}