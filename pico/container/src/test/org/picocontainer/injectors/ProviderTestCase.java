/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoCompositionException;

public class ProviderTestCase {
    
    @Test
    public void provideMethodCanParticipateInInjection() {
        DefaultPicoContainer dpc = new DefaultPicoContainer();
        dpc.addAdapter(new Chocolatier(true));
        dpc.addComponent(NeedsChocolate.class);
        dpc.addComponent(CocaoBeans.class);
        NeedsChocolate needsChocolate = dpc.getComponent(NeedsChocolate.class);
        assertNotNull(needsChocolate);
        assertNotNull(needsChocolate.choc);
        assertEquals(true, needsChocolate.choc.milky);
        assertNotNull(needsChocolate.choc.cocaoBeans);
    }

    @Test
    public void providerBarfsIfProvideMethodsParamsCanNotBeSatisfied() {
        DefaultPicoContainer dpc = new DefaultPicoContainer();
        dpc.addAdapter(new Chocolatier(true));
        dpc.addComponent(NeedsChocolate.class);
        try {
            dpc.getComponent(NeedsChocolate.class);
        } catch (PicoCompositionException e) {
            assertTrue(e.getMessage().contains("Parameter 0 "));
            assertTrue(e.getMessage().contains("cannot be null"));
        }
    }


    public static class CocaoBeans {
    }

    public static class Chocolate {
        private boolean milky;
        private final CocaoBeans cocaoBeans;
        public Chocolate(boolean milky, CocaoBeans cocaoBeans) {
            this.milky = milky;
            this.cocaoBeans = cocaoBeans;
        }
    }

    public static class Chocolatier extends Provider {
        private final boolean milky;
        public Chocolatier(boolean milky) {
            this.milky = milky;
        }
        public Chocolate provide(CocaoBeans cocaoBeans) {
            return new Chocolate(milky, cocaoBeans);
        }
    }

    public static class NeedsChocolate {
        private Chocolate choc;
        public NeedsChocolate(Chocolate choc) {
            this.choc = choc;
        }
    }


}
