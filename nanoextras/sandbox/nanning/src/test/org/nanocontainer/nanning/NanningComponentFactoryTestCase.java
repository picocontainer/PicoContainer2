/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Jon Tirs?n                                               *
 *****************************************************************************/

package org.nanocontainer.nanning;

import junit.framework.TestCase;
import org.codehaus.nanning.Aspects;
import org.codehaus.nanning.Invocation;
import org.codehaus.nanning.MethodInterceptor;
import org.codehaus.nanning.config.AspectSystem;
import org.codehaus.nanning.config.InterceptorAspect;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.InstanceComponentAdapter;
import org.picocontainer.defaults.NotConcreteRegistrationException;

/**
 * @author Jon Tirsen
 * @version $Revision$
 */
public class NanningComponentFactoryTestCase extends TestCase {

    public interface Wilma {
        void hello();
    }

    public static class WilmaImpl implements Wilma {
        public void hello() {
        }
    }

    public static class FredImpl {
        public FredImpl(Wilma wilma) {
            assertNotNull("Wilma cannot be passed in as null", wilma);
            wilma.hello();
        }
    }

    private StringBuffer log = new StringBuffer();

    public void testComponentsWithOneInterfaceAreAspected() throws PicoInitializationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {
        NanningComponentAdapter componentAdapter =
                new NanningComponentAdapter(new AspectSystem(), new ConstructorInjectionComponentAdapter(Wilma.class, WilmaImpl.class));
        Object component = componentAdapter.getComponentInstance(null);
        assertTrue(Aspects.isAspectObject(component));
        assertEquals(Wilma.class, Aspects.getAspectInstance(component).getClassIdentifier());
    }

    public void testComponentsWithoutInterfaceNotAspected() throws PicoInitializationException, PicoRegistrationException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        NanningComponentAdapter componentAdapter = new NanningComponentAdapter(new AspectSystem(),
                new ConstructorInjectionComponentAdapter(FredImpl.class, FredImpl.class));
        pico.registerComponent(new InstanceComponentAdapter(Wilma.class, new WilmaImpl()));
        pico.registerComponent(componentAdapter);
        Object component = componentAdapter.getComponentInstance(pico);
        assertFalse(Aspects.isAspectObject(component));
    }


    /**
     * Acceptance test (ie a teeny bit functional, but you'll get over it).
     */
    public void testSimpleLogOfMethodCall()
            throws PicoException, PicoInitializationException {

        AspectSystem aspectSystem = new AspectSystem();
        aspectSystem.addAspect(new InterceptorAspect(new MethodInterceptor() {
            public Object invoke(Invocation invocation) throws Throwable {
                log.append(invocation.getMethod().getName() + " ");
                return invocation.invokeNext();
            }
        }));

        MutablePicoContainer nanningEnabledPicoContainer = new DefaultPicoContainer(new NanningComponentAdapterFactory(aspectSystem, new DefaultComponentAdapterFactory()));
        nanningEnabledPicoContainer.registerComponent(Wilma.class, WilmaImpl.class);
        nanningEnabledPicoContainer.registerComponent(FredImpl.class);

        assertEquals("", log.toString());

        nanningEnabledPicoContainer.getComponents();

        // fred says hello to wilma, even the interceptor knows
        assertEquals("hello ", log.toString());

        Wilma wilma = (Wilma) nanningEnabledPicoContainer.getComponent(Wilma.class);

        assertNotNull(wilma);

        wilma.hello();

        // another entry in the log
        assertEquals("hello hello ", log.toString());

    }
}
