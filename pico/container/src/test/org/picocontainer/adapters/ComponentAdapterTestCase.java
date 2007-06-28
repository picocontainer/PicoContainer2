/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.adapters;

import junit.framework.TestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.parameters.ConstantParameter;
import org.picocontainer.adapters.AbstractAdapter;
import org.picocontainer.injectors.AbstractInjector;

import java.lang.reflect.Constructor;

/**
 * Test AbstractAdapter behaviour
 * @author J&ouml;rg Schaible
 */
public class ComponentAdapterTestCase
        extends TestCase {

    private static class TestAdapter extends AbstractAdapter {
        TestAdapter(Object componentKey, Class componentImplementation, ComponentMonitor componentMonitor) {
            super(componentKey, componentImplementation, componentMonitor);
        }
        TestAdapter(Object componentKey, Class componentImplementation) {
            super(componentKey, componentImplementation);
        }
        public Object getComponentInstance(PicoContainer container) throws
                                                                    PicoCompositionException
        {
            return null;
        }
        public void verify(PicoContainer container) throws PicoVerificationException {
        }
        
    }

    private static class TestMonitoringComponentAdapter extends AbstractAdapter {
        TestMonitoringComponentAdapter(ComponentMonitor componentMonitor) {
            super(null, null, componentMonitor);
        }
        public Object getComponentInstance(PicoContainer container) throws
                                                                    PicoCompositionException
        {
            return null;
        }
        public void verify(PicoContainer container) throws PicoVerificationException {
        }
        public Object getComponentKey() {
            return null;
        }
        public Class getComponentImplementation() {
            return null;
        }
        public void accept(PicoVisitor visitor) {
        }        
    }
    
    private static class TestInstantiatingAdapter extends AbstractInjector {
        TestInstantiatingAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) {
            super(componentKey, componentImplementation, parameters, NullComponentMonitor.getInstance(), NullLifecycleStrategy.getInstance());
        }
        protected Constructor getGreediestSatisfiableConstructor(PicoContainer container) throws
                                                                                          PicoCompositionException
        {
            return null;
        }

        public void verify(PicoContainer container) throws PicoCompositionException {
        }

        public Object getComponentInstance(PicoContainer container) throws
                                                                    PicoCompositionException
        {
            return null;
        }
    }
    
    public void testComponentImplementationMayNotBeNull() {
        try {
            new TestAdapter("Key", null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            assertEquals("componentImplementation", e.getMessage());
        }
    }

    public void testComponentKeyCanBeNullButNotRequested() {
        ComponentAdapter componentAdapter = new TestAdapter(null, String.class);
        try {
            componentAdapter.getComponentKey();
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            assertEquals("componentKey", e.getMessage());
        }
    }

    public void testComponentMonitorMayNotBeNull() {
        try {
            new TestAdapter("Key", String.class, null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            assertEquals("ComponentMonitor==null", e.getMessage());
        }
        try {
            new TestMonitoringComponentAdapter(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            assertEquals("ComponentMonitor==null", e.getMessage());
        }
    }

    public void testParameterMayNotBeNull() throws Exception {
        try {
            new TestInstantiatingAdapter("Key", String.class, new Parameter[]{new ConstantParameter("Value"), null});
            fail("Thrown " + NullPointerException.class.getName() + " expected");
        } catch (final NullPointerException e) {
            assertTrue(e.getMessage().endsWith("1 is null"));
        }
    }
    
    public void testStringRepresentation() {
        ComponentAdapter componentAdapter = new TestAdapter("Key", Integer.class);
        assertEquals(TestAdapter.class.getName() + "[Key]", componentAdapter.toString());
    }
}
