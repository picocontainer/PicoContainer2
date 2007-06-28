/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.tck;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.Characterizations;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class AbstractLazyInstantiationTestCase extends TestCase {

    protected abstract MutablePicoContainer createPicoContainer();

    public static class Kilroy {
        public Kilroy(Havana havana) {
            havana.graffiti("Kilroy was here");
        }
    }

    public static class Havana {
        public String paint = "Clean wall";

        public void graffiti(String paint) {
            this.paint = paint;
        }
    }

    public void testLazyInstantiation() throws PicoException {
        MutablePicoContainer pico = createPicoContainer();

        pico.as(Characterizations.CACHE).addComponent(Kilroy.class);
        pico.as(Characterizations.CACHE).addComponent(Havana.class);

        assertSame(pico.getComponent(Havana.class), pico.getComponent(Havana.class));
        assertNotNull(pico.getComponent(Havana.class));
        assertEquals("Clean wall", pico.getComponent(Havana.class).paint);
        assertNotNull(pico.getComponent(Kilroy.class));
        assertEquals("Kilroy was here", pico.getComponent(Havana.class).paint);
    }
}
