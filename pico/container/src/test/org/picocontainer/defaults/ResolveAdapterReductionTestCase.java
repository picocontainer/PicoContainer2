/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.Parameter;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.NameBinding;
import org.picocontainer.parameters.ComponentParameter;
import org.picocontainer.injectors.ConstructorInjection;
import org.picocontainer.injectors.ConstructorInjector;

import java.lang.reflect.Type;
import java.lang.annotation.Annotation;

/**
 * @author Paul Hammant
 */
public class ResolveAdapterReductionTestCase {

    int resolveAdapterCalls;

    @Test
    public void testThatResolveAdapterCanBeDoneOnceForASituationWhereItWasPreviouslyDoneAtLeastTwice() throws Exception {
        resolveAdapterCalls = 0;
        DefaultPicoContainer pico = new DefaultPicoContainer(new ConstructorInjection());
        pico.addAdapter(new ConstructorInjector(One.class, One.class, null) {
            protected Parameter[] createDefaultParameters(Type[] parameters) {
                Parameter[] componentParameters = new Parameter[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    componentParameters[i] = new ComponentParameter() {
                        protected <T> ComponentAdapter<T> resolveAdapter(PicoContainer container, ComponentAdapter adapter, Class<T> expectedType, NameBinding expectedNameBinding, boolean useNames, Annotation binding) {
                            resolveAdapterCalls++;
                            return super.resolveAdapter(container, adapter, expectedType, expectedNameBinding, useNames, binding);    //To change body of overridden methods use File | Settings | File Templates.
                        }
                    };
                }
                return componentParameters;
            }
        });
        pico.addComponent(new Two());
        long start = System.currentTimeMillis();
        for (int x = 0; x<30000; x++) {
            One one = pico.getComponent(One.class);
            assertNotNull(one);
            assertNotNull(one.two);
            assertEquals("resolveAdapter should only be called once, regardless of how many getComponents there are",
                    1, resolveAdapterCalls);
        }
        System.out.println("GreediestConstructorTestCase elapsed: " + (System.currentTimeMillis() - start));
    }

    public static class One {
        private final Two two;

        public One(Two two) {
            this.two = two;
        }
    }

    public static class Two {
        public Two() {
        }
    }


}
