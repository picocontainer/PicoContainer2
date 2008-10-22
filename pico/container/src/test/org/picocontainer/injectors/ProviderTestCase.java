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
import static org.junit.Assert.fail;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoCompositionException;

public class ProviderTestCase {
    
    @Test
    public void provideMethodCanParticipateInInjection() {
        DefaultPicoContainer dpc = new DefaultPicoContainer();
        dpc.addAdapter(new Chocolatier(true));
        dpc.addComponent(NeedsChocolate.class);
        dpc.addComponent(CocaoBeans.class);
        dpc.addComponent(String.class, "Cadbury's"); // the only string in the set of components
        NeedsChocolate needsChocolate = dpc.getComponent(NeedsChocolate.class);
        assertNotNull(needsChocolate);
        assertNotNull(needsChocolate.choc);
        assertEquals(true, needsChocolate.choc.milky);
        assertNotNull(needsChocolate.choc.cocaoBeans);
        assertEquals("Cadbury's", needsChocolate.choc.name);
    }

    @Test
    public void provideMethodCanDisambiguateUsingParameterNames() {
        DefaultPicoContainer dpc = new DefaultPicoContainer();
        dpc.addAdapter(new Chocolatier(true));
        dpc.addComponent(NeedsChocolate.class);
        dpc.addComponent(CocaoBeans.class);
        dpc.addComponent("color", "Red"); // not used by virtue of key
        dpc.addComponent("name", "Cadbury's");
        dpc.addComponent("band", "Abba"); // not used by virtue of key
        NeedsChocolate needsChocolate = dpc.getComponent(NeedsChocolate.class);
        assertNotNull(needsChocolate);
        assertNotNull(needsChocolate.choc);
        assertEquals(true, needsChocolate.choc.milky);
        assertNotNull(needsChocolate.choc.cocaoBeans);
        assertEquals("Cadbury's", needsChocolate.choc.name);
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
        private final String name;

        public Chocolate(boolean milky, CocaoBeans cocaoBeans, String name) {
            this.milky = milky;
            this.cocaoBeans = cocaoBeans;
            this.name = name;
        }
    }

    public static class Chocolatier extends ProviderAdapter {
        private final boolean milky;
        public Chocolatier(boolean milky) {
            this.milky = milky;
        }
        public Chocolate provide(CocaoBeans cocaoBeans, String name) {
            return new Chocolate(milky, cocaoBeans, name);
        }
        @Override
        protected boolean useNames() {
            return true;
        }
    }

    public static class NeedsChocolate {
        private Chocolate choc;
        public NeedsChocolate(Chocolate choc) {
            this.choc = choc;
        }
    }

    @Test
    public void providerBarfsIfNoProvideMethod() {
        DefaultPicoContainer dpc = new DefaultPicoContainer();
        try {
            dpc.addAdapter(new ProviderWithoutProvideMethod());
            fail("should have barfed");
        } catch (PicoCompositionException e) {
            assertEquals("There must be a method named 'provide' in the AbstractProvider implementation", e.getMessage());
        }
    }

    @Test
    public void providerBarfsIfBadProvideMethod() {
        DefaultPicoContainer dpc = new DefaultPicoContainer();
        try {
            dpc.addAdapter(new ProviderWithBadProvideMethod());
            fail("should have barfed");
        } catch (PicoCompositionException e) {
            assertEquals("There must be a non void returning method named 'provide' in the AbstractProvider implementation", e.getMessage());
        }
    }

    @Test
    public void providerBarfsIfTooManyProvideMethod() {
        DefaultPicoContainer dpc = new DefaultPicoContainer();
        try {
            dpc.addAdapter(new ProviderWithTooManyProvideMethods());
            fail("should have barfed");
        } catch (PicoCompositionException e) {
            assertEquals("There must be only one method named 'provide' in the AbstractProvider implementation", e.getMessage());
        }
    }

    public static class ProviderWithoutProvideMethod extends ProviderAdapter {
    }
    public static class ProviderWithBadProvideMethod extends ProviderAdapter {
        public void provide() {

        }
    }
    public static class ProviderWithTooManyProvideMethods extends ProviderAdapter {
        public String provide(String str) {
            return null;
        }
        public Integer provide() {
            return null;
        }
    }

    @Test
    public void provideMethodCanParticipateInInjection2() {
        DefaultPicoContainer dpc = new DefaultPicoContainer();
        dpc.addAdapter(new ProviderAdapter(new Chocolatier2(true)));
        dpc.addComponent(NeedsChocolate.class);
        dpc.addComponent(CocaoBeans.class);
        dpc.addComponent(String.class, "Cadbury's"); // the only string in the set of components
        NeedsChocolate needsChocolate = dpc.getComponent(NeedsChocolate.class);
        assertNotNull(needsChocolate);
        assertNotNull(needsChocolate.choc);
        assertEquals(true, needsChocolate.choc.milky);
        assertNotNull(needsChocolate.choc.cocaoBeans);
        assertEquals("Cadbury's", needsChocolate.choc.name);
    }

    public static class Chocolatier2 implements Provider {
        private final boolean milky;
        public Chocolatier2(boolean milky) {
            this.milky = milky;
        }
        public Chocolate provide(CocaoBeans cocaoBeans, String name) {
            return new Chocolate(milky, cocaoBeans, name);
        }
    }

}
