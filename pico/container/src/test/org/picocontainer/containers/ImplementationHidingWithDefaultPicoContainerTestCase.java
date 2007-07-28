/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.containers;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.behaviors.ImplementationHidingBehaviorFactory;
import org.picocontainer.behaviors.CachingBehaviorFactory;
import org.picocontainer.injectors.ConstructorInjectionFactory;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.Characteristics;
import org.picocontainer.tck.AbstractImplementationHidingPicoContainerTestCase;

import java.util.Properties;

import junit.framework.AssertionFailedError;

/**
 *
 * @author Aslak Helles&oslash;y
 */
public class ImplementationHidingWithDefaultPicoContainerTestCase extends AbstractImplementationHidingPicoContainerTestCase {

    protected MutablePicoContainer createImplementationHidingPicoContainer() {
        return createPicoContainer(null);
    }

    protected Properties[] getProperties() {
        return new Properties[] {Characteristics.NO_CACHE, Characteristics.NO_HIDE_IMPL};
    }

    protected MutablePicoContainer createPicoContainer(PicoContainer parent) {
        return new DefaultPicoContainer(new CachingBehaviorFactory().wrap(new ImplementationHidingBehaviorFactory().wrap(new ConstructorInjectionFactory())), parent);
    }
    
    public void testSameInstanceCanBeUsedAsDifferentTypeWhenCaching() {
        // we're choosing a CAF for DPC, thus Caching (a default) not enabled.
        try {
            super.testSameInstanceCanBeUsedAsDifferentTypeWhenCaching();
        } catch (AssertionFailedError e) {
            assertTrue(e.getMessage().indexOf("expected same:<org.picocontainer.testmodel.WashableTouchable@") > -1);
            assertTrue(e.getMessage().indexOf("was not:<org.picocontainer.testmodel.WashableTouchable@") > -1);
        }

    }

    public void testAcceptImplementsBreadthFirstStrategy() {
        super.testAcceptImplementsBreadthFirstStrategy();
    }

}
