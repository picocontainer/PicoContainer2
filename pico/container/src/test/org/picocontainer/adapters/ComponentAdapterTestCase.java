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

    @SuppressWarnings("serial")
	private static class TestAdapter<T> extends AbstractAdapter<T> {
    	
        TestAdapter(Object componentKey, Class<T> componentImplementation, ComponentMonitor componentMonitor) {
            super(componentKey, componentImplementation, componentMonitor);
        }
        TestAdapter(Object componentKey, Class<T> componentImplementation) {
            super(componentKey, componentImplementation);
        }
        public T getComponentInstance(PicoContainer container) throws PicoCompositionException {
            return null;
        }
        public void verify(PicoContainer container) throws PicoVerificationException {
        }

        public String getDescriptor() {
            return TestAdapter.class.getName() + ":" ;
        }
    }

    @SuppressWarnings("serial")
	private static class TestMonitoringComponentAdapter<T> extends AbstractAdapter<T> {
        TestMonitoringComponentAdapter(ComponentMonitor componentMonitor) {
            super(null, null, componentMonitor);
        }
        public T getComponentInstance(PicoContainer container) throws PicoCompositionException {
            return null;
        }
        public void verify(PicoContainer container) throws PicoVerificationException {
        }
        public Object getComponentKey() {
            return null;
        }
        public Class<T> getComponentImplementation() {
            return null;
        }
        public void accept(PicoVisitor visitor) {
        }

        public String getDescriptor() {
            return null;
        }
    }
    
    @SuppressWarnings("serial")
	private static class TestInstantiatingAdapter<T> extends AbstractInjector<T> {
        TestInstantiatingAdapter(Object componentKey, Class<T> componentImplementation, Parameter... parameters) {
            super(componentKey, componentImplementation, parameters, new NullComponentMonitor(), new NullLifecycleStrategy(), false);
        }
        protected Constructor<T> getGreediestSatisfiableConstructor(PicoContainer container) throws PicoCompositionException {
            return null;
        }

        public void verify(PicoContainer container) throws PicoCompositionException {
        }

        public T getComponentInstance(PicoContainer container) throws PicoCompositionException {
            return null;
        }

        public String getDescriptor() {
            return null;
        }
    }
    
    public void testComponentImplementationMayNotBeNull() {
        try {
            new TestAdapter<Object>("Key", null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            assertEquals("componentImplementation", e.getMessage());
        }
    }

    public void testComponentKeyCanBeNullButNotRequested() {
        ComponentAdapter<String> componentAdapter = new TestAdapter<String>(null, String.class);
        try {
            componentAdapter.getComponentKey();
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            assertEquals("componentKey", e.getMessage());
        }
    }

    public void testComponentMonitorMayNotBeNull() {
        try {
            new TestAdapter<String>("Key", String.class, null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            assertEquals("ComponentMonitor==null", e.getMessage());
        }
        try {
            new TestMonitoringComponentAdapter<Object>(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            assertEquals("ComponentMonitor==null", e.getMessage());
        }
    }

    public void testParameterMayNotBeNull() throws Exception {
        try {
            new TestInstantiatingAdapter<String>("Key", String.class, new Parameter[]{new ConstantParameter("Value"), null});
            fail("Thrown " + NullPointerException.class.getName() + " expected");
        } catch (final NullPointerException e) {
            assertTrue(e.getMessage().endsWith("1 is null"));
        }
    }
    
    public void testStringRepresentation() {
        ComponentAdapter<Integer> componentAdapter = new TestAdapter<Integer>("Key", Integer.class);
        assertEquals(TestAdapter.class.getName() + ":Key", componentAdapter.toString());
    }
}
