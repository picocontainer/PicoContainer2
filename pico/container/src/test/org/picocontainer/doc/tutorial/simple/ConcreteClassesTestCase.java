/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.doc.tutorial.simple;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;

public class ConcreteClassesTestCase extends TestCase {

    public void testAssembleComponentsAndInstantiateAndUseThem() {
        // START SNIPPET: assemble
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(Boy.class);
        pico.addComponent(Girl.class);
        // END SNIPPET: assemble

        // START SNIPPET: instantiate-and-use
        Girl girl = pico.getComponent(Girl.class);
        girl.kissSomeone();
        // END SNIPPET: instantiate-and-use
    }


}
