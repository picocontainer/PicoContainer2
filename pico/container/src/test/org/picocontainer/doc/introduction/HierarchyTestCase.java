/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.doc.introduction;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.DefaultPicoContainer;

/**
 * @author Aslak Helles&oslash;y
 */
public class HierarchyTestCase extends TestCase {
    public void testHierarchy() {
        try {
            // START SNIPPET: wontwork
            // Create x hierarchy of containers
            MutablePicoContainer x = new DefaultPicoContainer();
            MutablePicoContainer y = new DefaultPicoContainer( x );
            MutablePicoContainer z = new DefaultPicoContainer( x );

            // Assemble components
            x.addComponent(Apple.class);
            y.addComponent(Juicer.class);
            z.addComponent(Peeler.class);

            // Instantiate components
            Peeler peeler = z.getComponent(Peeler.class);
            // WON'T WORK! peeler will be null
            peeler = x.getComponent(Peeler.class);
            // WON'T WORK! This will throw an exception
            Juicer juicer = y.getComponent(Juicer.class);
            // END SNIPPET: wontwork
        } catch (AbstractInjector.UnsatisfiableDependenciesException e) {
            // expected
        }
    }

}