/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.parameters;

import junit.framework.TestCase;

import org.junit.Test;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.NameBinding;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.visitors.VerifyingVisitor;


/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 */
public final class ParameterTestCase extends TestCase {

    public static class FooNameBinding implements NameBinding {
        public String getName() {
            return "";
        }
    }

    final NameBinding pn = new FooNameBinding();

    @Test public void testComponentParameterFetches() throws PicoCompositionException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        ComponentAdapter adapter = pico.addComponent(Touchable.class, SimpleTouchable.class).getComponentAdapter(Touchable.class,
                                                                                                                 (NameBinding) null);
        assertNotNull(adapter);
        assertNotNull(pico.getComponent(Touchable.class));
        Touchable touchable = (Touchable) ComponentParameter.DEFAULT.resolveInstance(pico, null, Touchable.class, pn,
                                                                                     false, null);
        assertNotNull(touchable);
    }

    @Test public void testComponentParameterExcludesSelf() throws PicoCompositionException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        ComponentAdapter adapter = pico.addComponent(Touchable.class, SimpleTouchable.class).getComponentAdapter(Touchable.class,
                                                                                                                 (NameBinding) null);

        assertNotNull(pico.getComponent(Touchable.class));
        Touchable touchable = (Touchable) ComponentParameter.DEFAULT.resolveInstance(pico, adapter, Touchable.class, pn,
                                                                                     false, null);
        assertNull(touchable);
    }

    @Test public void testConstantParameter() throws PicoCompositionException {
        Object value = new Object();
        ConstantParameter parameter = new ConstantParameter(value);
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        assertSame(value, parameter.resolveInstance(picoContainer, null, Object.class, pn, false, null));
    }

    @Test public void testDependsOnTouchableWithTouchableSpecifiedAsConstant() throws PicoCompositionException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        SimpleTouchable touchable = new SimpleTouchable();
        pico.addComponent(DependsOnTouchable.class, DependsOnTouchable.class, new ConstantParameter(touchable));
        pico.getComponents();
        assertTrue(touchable.wasTouched);
    }

    @Test public void testComponentParameterRespectsExpectedType() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        ComponentAdapter adapter = picoContainer.addComponent(Touchable.class, SimpleTouchable.class).getComponentAdapter(Touchable.class,
                                                                                                                          (NameBinding) null);
        assertNull(ComponentParameter.DEFAULT.resolveInstance(picoContainer, adapter, TestCase.class, pn, false, null));
    }
	
	@Test public void testComponentParameterResolvesPrimitiveType() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        ComponentAdapter adapter = picoContainer.addComponent("glarch", 239).getComponentAdapter("glarch");
        assertNotNull(adapter);
		Parameter parameter = new ComponentParameter("glarch");
		assertNotNull(parameter.resolveInstance(picoContainer,null,Integer.TYPE, pn, false, null));
		assertEquals(239, ((Integer)parameter.resolveInstance(picoContainer,null,Integer.TYPE, pn, false, null)).intValue());
	}

    @Test public void testConstantParameterRespectsExpectedType() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        Parameter parameter = new ConstantParameter(new SimpleTouchable());
        ComponentAdapter adapter = picoContainer.addComponent(Touchable.class, SimpleTouchable.class).getComponentAdapter(Touchable.class,
                                                                                                                          (NameBinding) null);
        assertFalse(parameter.isResolvable(picoContainer, adapter, TestCase.class, pn, false, null));
    }

    @Test public void testParameterRespectsExpectedType() throws PicoCompositionException {
        Parameter parameter = new ConstantParameter(Touchable.class);
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        assertFalse(parameter.isResolvable(picoContainer, null, TestCase.class, pn, false, null));

        ComponentAdapter adapter = picoContainer.addComponent(Touchable.class, SimpleTouchable.class).getComponentAdapter(Touchable.class,
                                                                                                                          (NameBinding) null);

        assertNull(ComponentParameter.DEFAULT.resolveInstance(picoContainer, adapter, TestCase.class, pn, false, null));
    }

    @Test public void testConstantParameterWithPrimitives() throws PicoCompositionException {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        Byte byteValue = (byte)5;
        ConstantParameter parameter = new ConstantParameter(byteValue);
        assertSame(byteValue, parameter.resolveInstance(picoContainer, null, Byte.TYPE, pn, false, null));
        assertSame(byteValue, parameter.resolveInstance(picoContainer, null, Byte.class, pn, false, null));
        Short shortValue = (short)5;
        parameter = new ConstantParameter(shortValue);
        assertSame(shortValue, parameter.resolveInstance(picoContainer, null, Short.TYPE, pn, false, null));
        assertSame(shortValue, parameter.resolveInstance(picoContainer, null, Short.class, pn, false, null));
        Integer intValue = 5;
        parameter = new ConstantParameter(intValue);
        assertSame(intValue, parameter.resolveInstance(picoContainer, null, Integer.TYPE, pn, false, null));
        assertSame(intValue, parameter.resolveInstance(picoContainer, null, Integer.class, pn, false, null));
        Long longValue = (long)5;
        parameter = new ConstantParameter(longValue);
        assertSame(longValue, parameter.resolveInstance(picoContainer, null, Long.TYPE, pn, false, null));
        assertSame(longValue, parameter.resolveInstance(picoContainer, null, Long.class, pn, false, null));
        Float floatValue = new Float(5.5);
        parameter = new ConstantParameter(floatValue);
        assertSame(floatValue, parameter.resolveInstance(picoContainer, null, Float.TYPE, pn, false, null));
        assertSame(floatValue, parameter.resolveInstance(picoContainer, null, Float.class, pn, false, null));
        Double doubleValue = 5.5;
        parameter = new ConstantParameter(doubleValue);
        assertSame(doubleValue, parameter.resolveInstance(picoContainer, null, Double.TYPE, pn, false, null));
        assertSame(doubleValue, parameter.resolveInstance(picoContainer, null, Double.class, pn, false, null));
        Boolean booleanValue = true;
        parameter = new ConstantParameter(booleanValue);
        assertSame(booleanValue, parameter.resolveInstance(picoContainer, null, Boolean.TYPE, pn, false, null));
        assertSame(booleanValue, parameter.resolveInstance(picoContainer, null, Boolean.class, pn, false, null));
        Character charValue = 'x';
        parameter = new ConstantParameter(charValue);
        assertSame(charValue, parameter.resolveInstance(picoContainer, null, Character.TYPE, pn, false, null));
        assertSame(charValue, parameter.resolveInstance(picoContainer, null, Character.class, pn, false, null));
    }

    @Test public void testConstantParameterWithPrimitivesRejectsUnexpectedType() throws PicoCompositionException {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        Byte byteValue = (byte)5;
        ConstantParameter parameter = new ConstantParameter(byteValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Integer.TYPE, pn, false, null));
        Short shortValue = (short)5;
        parameter = new ConstantParameter(shortValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Byte.TYPE, pn, false, null));
        Integer intValue = 5;
        parameter = new ConstantParameter(intValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Byte.TYPE, pn, false, null));
        Long longValue = (long)5;
        parameter = new ConstantParameter(longValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Byte.TYPE, pn, false, null));
        Float floatValue = new Float(5.5);
        parameter = new ConstantParameter(floatValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Byte.TYPE, pn, false, null));
        Double doubleValue = 5.5;
        parameter = new ConstantParameter(doubleValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Byte.TYPE, pn, false, null));
        Boolean booleanValue = true;
        parameter = new ConstantParameter(booleanValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Byte.TYPE, pn, false, null));
        Character charValue = 'x';
        parameter = new ConstantParameter(charValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Byte.TYPE, pn, false, null));
    }

    @Test public void testKeyClashBug118() throws PicoCompositionException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addComponent("A", String.class, new ConstantParameter("A"));
        pico.addComponent("B", String.class, new ConstantParameter("A"));
        new VerifyingVisitor().traverse(pico);
    }

}
