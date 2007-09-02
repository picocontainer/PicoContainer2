/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.picocontainer;

import static org.picocontainer.Characteristics.CDI;
import static org.picocontainer.Characteristics.SDI;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.injectors.ConstructorInjection;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.monitors.WriterComponentMonitor;
import org.picocontainer.tck.AbstractPicoContainerTestCase;
import org.picocontainer.testmodel.DecoratedTouchable;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Map;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Ward Cunningham
 * @author Mauro Talevi
 */
public final class DefaultPicoContainerTestCase extends AbstractPicoContainerTestCase {
    protected MutablePicoContainer createPicoContainer(PicoContainer parent) {
        return new DefaultPicoContainer(parent);
    }

    protected Properties[] getProperties() {
        return new Properties[0];
    }

    public void testInstantiationWithNullComponentFactory() {
        try {
            new DefaultPicoContainer((ComponentFactory)null, null);
            fail("NPE expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void testUpDownDependenciesCannotBeFollowed() {
        MutablePicoContainer parent = createPicoContainer(null);
        MutablePicoContainer child = createPicoContainer(parent);

        // ComponentF -> ComponentA -> ComponentB+C
        child.addComponent(ComponentF.class);
        parent.addComponent(ComponentA.class);
        child.addComponent(ComponentB.class);
        child.addComponent(ComponentC.class);

        try {
            child.getComponent(ComponentF.class);
            fail("Thrown " + AbstractInjector.UnsatisfiableDependenciesException.class.getName() + " expected");
        } catch (final AbstractInjector.UnsatisfiableDependenciesException e) {
            assertEquals(ComponentB.class, e.getUnsatisfiedDependencyType());
        }
    }


    public void testComponentsCanBeRemovedByInstance() {
        MutablePicoContainer pico = createPicoContainer(null);
        pico.addComponent(HashMap.class);
        pico.addComponent(ArrayList.class);
        List list = pico.getComponent(List.class);
        pico.removeComponentByInstance(list);
        assertEquals(1, pico.getComponentAdapters().size());
        assertEquals(1, pico.getComponents().size());
        assertEquals(HashMap.class, pico.getComponent(Serializable.class).getClass());
    }

    public void testComponentInstancesListIsReturnedForNullType() {
        MutablePicoContainer pico = createPicoContainer(null);
        List componentInstances = pico.getComponents(null);
        assertNotNull(componentInstances);
        assertEquals(0, componentInstances.size());
    }

    public void testComponentsWithCommonSupertypeWhichIsAConstructorArgumentCanBeLookedUpByConcreteType() {
        MutablePicoContainer pico = createPicoContainer(null);
        pico.addComponent(LinkedList.class, LinkedList.class, Parameter.ZERO);
        pico.addComponent(ArrayList.class);
        assertEquals(ArrayList.class, pico.getComponent((Class)ArrayList.class).getClass());
    }

    /*
     When pico tries to resolve DecoratedTouchable it find as dependency itself and SimpleTouchable.
     Problem is basically the same as above. Pico should not consider self as solution.
     
     JS
     fixed it ( PICO-222 )
     KP
     */
    public void testUnambiguouSelfDependency() {
        MutablePicoContainer pico = createPicoContainer(null);
        pico.addComponent(SimpleTouchable.class);
        pico.addComponent(DecoratedTouchable.class);
        Touchable t = (Touchable)pico.getComponent((Object)DecoratedTouchable.class);
        assertNotNull(t);
    }


    public void testPicoUsedInBuilderStyle() {
        MutablePicoContainer pico = createPicoContainer(null);
        Touchable t = pico.change(Characteristics.CACHE)
            .addComponent(SimpleTouchable.class)
            .addComponent(DecoratedTouchable.class)
            .getComponent(DecoratedTouchable.class);
        SimpleTouchable t2 = pico.getComponent(SimpleTouchable.class);
        assertNotNull(t);
        assertNotNull(t2);
        t.touch();
        assertTrue(t2.wasTouched);
    }

    public static class Thingie {
        public Thingie(List c) {
            assertNotNull(c);
        }
    }

    public void testThangCanBeInstantiatedWithArrayList() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(Thingie.class);
        pico.addComponent(ArrayList.class);
        assertNotNull(pico.getComponent(Thingie.class));
    }

    public void testGetComponentAdaptersOfTypeNullReturnsEmptyList() {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        List adapters = pico.getComponentAdapters(null);
        assertNotNull(adapters);
        assertEquals(0, adapters.size());
    }


    public static class Service {
    }

    public static final class TransientComponent {
        private final Service service;

        public TransientComponent(Service service) {
            this.service = service;
        }
    }

    public void testDefaultPicoContainerReturnsNewInstanceForEachCallWhenUsingTransientComponentAdapter() {


        DefaultPicoContainer picoContainer =
            new DefaultPicoContainer(new Caching().wrap(new ConstructorInjection()));

        picoContainer.addComponent(Service.class);
        picoContainer.as(Characteristics.NO_CACHE)
            .addAdapter(new ConstructorInjector(TransientComponent.class,
                                                TransientComponent.class,
                                                null,
                                                new NullComponentMonitor(),
                                                new NullLifecycleStrategy(), false));
        TransientComponent c1 = picoContainer.getComponent(TransientComponent.class);
        TransientComponent c2 = picoContainer.getComponent(TransientComponent.class);
        assertNotSame(c1, c2);
        assertSame(c1.service, c2.service);
    }

    public static class DependsOnCollection {
        public DependsOnCollection(Collection c) {
        }
    }

    public void testShouldProvideInfoAboutDependingWhenAmbiguityHappens() {
        MutablePicoContainer pico = this.createPicoContainer(null);
        pico.addComponent(new ArrayList());
        pico.addComponent(new LinkedList());
        pico.addComponent(DependsOnCollection.class);
        try {
            pico.getComponent(DependsOnCollection.class);
            fail();
        } catch (AbstractInjector.AmbiguousComponentResolutionException expected) {
            String doc = DependsOnCollection.class.getName();
            assertEquals("class " + doc +
                         " needs a 'java.util.Collection' injected, but there are too many choices to inject. These:[class java.util.ArrayList, class java.util.LinkedList], refer http://picocontainer.org/ambiguous-injectable-help.html",
                         expected.getMessage());
        }
    }

    public void testInstantiationWithMonitorAndParent() {
        StringWriter writer = new StringWriter();
        ComponentMonitor monitor = new WriterComponentMonitor(writer);
        DefaultPicoContainer parent = new DefaultPicoContainer();
        DefaultPicoContainer child = new DefaultPicoContainer(monitor, parent);
        parent.addComponent("st", SimpleTouchable.class);
        child.addComponent("dot", DependsOnTouchable.class);
        DependsOnTouchable dot = (DependsOnTouchable)child.getComponent("dot");
        assertNotNull(dot);
        assertTrue("writer not empty", writer.toString().length() > 0);
    }

    public void testStartCapturedByMonitor() {
        final StringBuffer sb = new StringBuffer();
        DefaultPicoContainer dpc = new DefaultPicoContainer(new NullComponentMonitor() {
            public void invoking(PicoContainer container,
                                 ComponentAdapter componentAdapter,
                                 Member member,
                                 Object instance)
            {
                sb.append(member.toString());
            }
        });
        dpc.as(Characteristics.CACHE).addComponent(DefaultPicoContainer.class);
        dpc.start();
        assertEquals("ComponentMonitor should have been notified that the component had been started",
                     "public abstract void org.picocontainer.Startable.start()", sb.toString());
    }

    public static class StartableClazz implements Startable {
        private MutablePicoContainer _pico;

        public void start() {
            List<SimpleTouchable> cps = _pico
                .getComponents(SimpleTouchable.class);
            assertNotNull(cps);
        }

        public void stop() {
        }

    }

    public void testListComponentsOnStart() {

        // This is really discouraged.  Breaks basic principals of IoC - components should not refer
        // to their containers
        //
        // Might be deleted in due coure, along with adaptersClone stuff in DPC

        DefaultPicoContainer dpc = new DefaultPicoContainer();
        dpc.addComponent(SimpleTouchable.class);
        StartableClazz cl = new StartableClazz();
        cl._pico = dpc;
        dpc.addComponent(cl);
        dpc.start();
    }

    public void testCanChangeMonitor() {
        StringWriter writer1 = new StringWriter();
        ComponentMonitor monitor1 = new WriterComponentMonitor(writer1);
        DefaultPicoContainer pico = new DefaultPicoContainer(monitor1);
        pico.addComponent("t1", SimpleTouchable.class);
        pico.addComponent("t3", SimpleTouchable.class);
        Touchable t1 = (Touchable)pico.getComponent("t1");
        assertNotNull(t1);
        final String s = writer1.toString();
        assertTrue("writer not empty", s.length() > 0);
        StringWriter writer2 = new StringWriter();
        ComponentMonitor monitor2 = new WriterComponentMonitor(writer2);
        pico.changeMonitor(monitor2);
        pico.addComponent("t2", SimpleTouchable.class);
        Touchable t2 = (Touchable)pico.getComponent("t2");
        assertNotNull(t2);
        final String s2 = writer2.toString();
        assertTrue("writer not empty", s2.length() > 0);
        assertTrue("writers of same length", writer1.toString().length() == writer2.toString().length());
        Touchable t3 = (Touchable)pico.getComponent("t3");
        assertNotNull(t3);
        assertTrue("old writer was used", writer1.toString().length() < writer2.toString().length());
    }

    public void testCanChangeMonitorOfChildContainers() {
        StringWriter writer1 = new StringWriter();
        ComponentMonitor monitor1 = new WriterComponentMonitor(writer1);
        DefaultPicoContainer parent = new DefaultPicoContainer();
        DefaultPicoContainer child = new DefaultPicoContainer(monitor1);
        parent.addChildContainer(child);
        child.addComponent("t1", SimpleTouchable.class);
        child.addComponent("t3", SimpleTouchable.class);
        Touchable t1 = (Touchable)child.getComponent("t1");
        assertNotNull(t1);
        assertTrue("writer not empty", writer1.toString().length() > 0);
        StringWriter writer2 = new StringWriter();
        ComponentMonitor monitor2 = new WriterComponentMonitor(writer2);
        parent.changeMonitor(monitor2);
        child.addComponent("t2", SimpleTouchable.class);
        Touchable t2 = (Touchable)child.getComponent("t2");
        assertNotNull(t2);
        assertTrue("writer not empty", writer2.toString().length() > 0);
        String s1 = writer1.toString();
        String s2 = writer2.toString();
        assertTrue("writers of same length", s1.length() == s2.length());
        Touchable t3 = (Touchable)child.getComponent("t3");
        assertNotNull(t3);
        assertTrue("old writer was used", writer1.toString().length() < writer2.toString().length());
    }

    public void testChangeMonitorIsIgnoredIfNotSupportingStrategy() {
        StringWriter writer = new StringWriter();
        ComponentMonitor monitor = new WriterComponentMonitor(writer);
        DefaultPicoContainer parent =
            new DefaultPicoContainer(new ComponentFactoryWithNoMonitor(new ComponentAdapterWithNoMonitor(new SimpleTouchable())));
        parent.addChildContainer(new EmptyPicoContainer());
        parent.addComponent("t1", SimpleTouchable.class);
        parent.changeMonitor(monitor);
        assertTrue("writer empty", writer.toString().length() == 0);
    }

    public void testCanReturnCurrentMonitorFromComponentFactory() {
        StringWriter writer1 = new StringWriter();
        ComponentMonitor monitor1 = new WriterComponentMonitor(writer1);
        DefaultPicoContainer pico = new DefaultPicoContainer(monitor1);
        assertEquals(monitor1, pico.currentMonitor());
        StringWriter writer2 = new StringWriter();
        ComponentMonitor monitor2 = new WriterComponentMonitor(writer2);
        pico.changeMonitor(monitor2);
        assertEquals(monitor2, pico.currentMonitor());
    }

    private static final class ComponentFactoryWithNoMonitor implements ComponentFactory {
        private final ComponentAdapter adapter;

        public ComponentFactoryWithNoMonitor(ComponentAdapter adapter) {
            this.adapter = adapter;
        }

        public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor,
                                                       LifecycleStrategy lifecycleStrategy,
                                                       Properties componentProperties,
                                                       Object componentKey,
                                                       Class componentImplementation,
                                                       Parameter... parameters) throws PicoCompositionException {
            return adapter;
        }
    }

    private static final class ComponentAdapterWithNoMonitor implements ComponentAdapter {
        private final Object instance;

        public ComponentAdapterWithNoMonitor(Object instance) {
            this.instance = instance;
        }

        public Object getComponentKey() {
            return instance.getClass();
        }

        public Class getComponentImplementation() {
            return instance.getClass();
        }

        public Object getComponentInstance(PicoContainer container) throws PicoCompositionException {
            return instance;
        }

        public void verify(PicoContainer container) throws PicoCompositionException {
        }

        public void accept(PicoVisitor visitor) {
        }

        public ComponentAdapter getDelegate() {
            return null;
        }

        public ComponentAdapter findAdapterOfType(Class componentAdapterType) {
            return null;
        }
        public String getDescriptor() {
            return null;
        }
        
    }

    public void testMakeChildContainer() {
        MutablePicoContainer parent = new DefaultPicoContainer();
        parent.addComponent("t1", SimpleTouchable.class);
        MutablePicoContainer child = parent.makeChildContainer();
        Object t1 = child.getParent().getComponent("t1");
        assertNotNull(t1);
        assertTrue(t1 instanceof SimpleTouchable);
    }

    public void testCanUseCustomLifecycleStrategyForClassRegistrations() {
        DefaultPicoContainer dpc = new DefaultPicoContainer(new FailingLifecycleStrategy(), null);
        dpc.as(Characteristics.CACHE).addComponent(Startable.class, MyStartable.class);
        try {
            dpc.start();
            fail("should have barfed");
        } catch (RuntimeException e) {
            assertEquals("foo", e.getMessage());
        }
    }

    public void testCanUseCustomLifecycleStrategyForInstanceRegistrations() {
        DefaultPicoContainer dpc = new DefaultPicoContainer(new FailingLifecycleStrategy(), null);
        Startable myStartable = new MyStartable();
        dpc.addComponent(Startable.class, myStartable);
        try {
            dpc.start();
            fail("should have barfed");
        } catch (RuntimeException e) {
            assertEquals("foo", e.getMessage());
        }
    }

    public static class FailingLifecycleStrategy implements LifecycleStrategy {
        public void start(Object component) {
            throw new RuntimeException("foo");
        }

        public void stop(Object component) {
        }

        public void dispose(Object component) {
        }

        public boolean hasLifecycle(Class type) {
            return true;
        }

    }

    public static class MyStartable implements Startable {
        public MyStartable() {
        }

        public void start() {
        }

        public void stop() {
        }
    }

    public static interface A {

    }

    public static class SimpleA implements A {

    }

    public static class WrappingA implements A {
        private final A wrapped;

        public WrappingA(A wrapped) {
            this.wrapped = wrapped;
        }
    }

    public void testCanRegisterTwoComponentsImplementingSameInterfaceOneWithInterfaceAsKey() throws Exception {
        MutablePicoContainer container = createPicoContainer(null);

        container.addComponent(SimpleA.class);
        container.addComponent(A.class, WrappingA.class);

        container.start();

        assertEquals(WrappingA.class, container.getComponent(A.class).getClass());
    }

    public void testCanRegisterTwoComponentsWithSameImplementionAndDifferentKey() throws Exception {
        MutablePicoContainer container = createPicoContainer(null);

        container.addComponent(SimpleA.class);
        container.addComponent("A", SimpleA.class);

        container.start();

        assertNotNull(container.getComponent("A"));
        assertNotNull(container.getComponent(SimpleA.class));
        assertNotSame(container.getComponent("A"), container.getComponent(SimpleA.class));
    }

    public void testPicoCanDifferentiateBetweenNamedStringsThatWouldOtherwiseBeAmbiguous() {
        MutablePicoContainer mpc = createPicoContainer(null);
        mpc.addComponent("greeting", "1");
        mpc.addComponent("message", "2");
        mpc.as(Characteristics.USE_NAMES).addComponent(PicoCompositionException.class, PicoCompositionException.class);
        assertEquals("2", mpc.getComponent(PicoCompositionException.class).getMessage());
    }

    public void testPicoCanDifferentiateBetweenNamedObjectsThatWouldOtherwiseBeAmbiguous() {
        MutablePicoContainer mpc = createPicoContainer(null);
        Horse dobbin = new Horse();
        Horse redRum = new Horse();
        mpc.addComponent("dobbin", dobbin);
        mpc.addComponent("horse", redRum);
        mpc.as(Characteristics.USE_NAMES).addComponent(CdiTurtle.class);
        assertEquals(redRum, mpc.getComponent(CdiTurtle.class).horse);
    }

    public void testPicoCanDifferentiateBetweenNamedIntsThatWouldOtherwiseBeAmbiguous() {
        MutablePicoContainer mpc = createPicoContainer(null);
        mpc.addComponent("one", 1);
        mpc.addComponent("two", 2);
        mpc.as(Characteristics.USE_NAMES).addComponent(NeedsTwo.class);
        assertEquals(2, mpc.getComponent(NeedsTwo.class).two);
    }

    public static class NeedsTwo {
        private final int two;

        public NeedsTwo(Integer two) {
            this.two = two;
        }
    }

    public static class Horse {
    }

    public static class CdiTurtle {
        public final Horse horse;

        public CdiTurtle(Horse horse) {
            this.horse = horse;
        }
    }

    public static class SdiDonkey {
        public Horse horse;

        public void setHorse(Horse horse) {
            this.horse = horse;
        }
    }

    public static class SdiRabbit {
        public Horse horse;

        public void setHorse(Horse horse) {
            this.horse = horse;
        }
    }

    public void testMixingOfSDIandCDI() {

        MutablePicoContainer container = createPicoContainer(null).change(Characteristics.CACHE);
        container.addComponent(Horse.class);
        container.change(SDI);
        container.addComponent(SdiDonkey.class);
        container.addComponent(SdiRabbit.class);
        container.change(CDI);
        container.addComponent(CdiTurtle.class);

        SdiDonkey donkey = container.getComponent(SdiDonkey.class);
        SdiRabbit rabbit = container.getComponent(SdiRabbit.class);
        CdiTurtle turtle = container.getComponent(CdiTurtle.class);

        assertions(donkey, rabbit, turtle);
    }

    public void testMixingOfSDIandCDIDifferently() {

        MutablePicoContainer container = createPicoContainer(null).change(Characteristics.CACHE);
        container.addComponent(Horse.class);
        container.addComponent(CdiTurtle.class);
        container.change(SDI);
        container.addComponent(SdiDonkey.class);
        container.addComponent(SdiRabbit.class);

        SdiDonkey donkey = container.getComponent(SdiDonkey.class);
        SdiRabbit rabbit = container.getComponent(SdiRabbit.class);
        CdiTurtle turtle = container.getComponent(CdiTurtle.class);

        assertions(donkey, rabbit, turtle);
    }

    public void testMixingOfSDIandCDIInBuilderStyle() {

        MutablePicoContainer container = createPicoContainer(null).change(Characteristics.CACHE);
        container.addComponent(Horse.class).change(SDI)
            .addComponent(SdiDonkey.class).addComponent(SdiRabbit.class).change(CDI).addComponent(CdiTurtle.class);

        SdiDonkey donkey = container.getComponent(SdiDonkey.class);
        SdiRabbit rabbit = container.getComponent(SdiRabbit.class);
        CdiTurtle turtle = container.getComponent(CdiTurtle.class);

        assertions(donkey, rabbit, turtle);
    }

    private void assertions(SdiDonkey donkey, SdiRabbit rabbit, CdiTurtle turtle) {
        assertNotNull(rabbit);
        assertNotNull(donkey);
        assertNotNull(turtle);
        assertNotNull(turtle.horse);
        assertNotNull(donkey.horse);
        assertNotNull(rabbit.horse);
        assertSame(donkey.horse, turtle.horse);
        assertSame(rabbit.horse, turtle.horse);
    }

    public void testMixingOfSDIandCDIWithTemporaryCharacterizations() {

        MutablePicoContainer container = createPicoContainer(null).change(Characteristics.CACHE);
        container.addComponent(Horse.class);
        container.addComponent(CdiTurtle.class);
        container.as(SDI).addComponent(SdiDonkey.class);
        container.as(SDI).addComponent(SdiRabbit.class);

        SdiDonkey donkey = container.getComponent(SdiDonkey.class);
        SdiRabbit rabbit = container.getComponent(SdiRabbit.class);
        CdiTurtle turtle = container.getComponent(CdiTurtle.class);

        assertions(donkey, rabbit, turtle);
    }

    public void testMixingOfSDIandCDIWithTemporaryCharacterizationsDifferently() {

        MutablePicoContainer container = createPicoContainer(null).change(Characteristics.CACHE);
        container.as(SDI).addComponent(SdiDonkey.class);
        container.as(SDI).addComponent(SdiRabbit.class);
        container.addComponent(Horse.class);
        container.addComponent(CdiTurtle.class);

        SdiDonkey donkey = container.getComponent(SdiDonkey.class);
        SdiRabbit rabbit = container.getComponent(SdiRabbit.class);
        CdiTurtle turtle = container.getComponent(CdiTurtle.class);

        assertions(donkey, rabbit, turtle);
    }

    public void testNoComponentIsMonitoredAndPotentiallyLateProvided() {
        final String[] missingKey = new String[1];

        String foo = (String)new DefaultPicoContainer(new NullComponentMonitor() {
            public Object noComponentFound(MutablePicoContainer container, Object componentKey) {
                missingKey[0] = (String)componentKey;
                return "foo";
            }
        }).getComponent("missingKey");

        assertNotNull(missingKey[0]);
        assertEquals("missingKey", missingKey[0]);
        assertEquals("foo", foo);

    }

    public void testThatComponentCannotBeRemovedFromStartedContainer() {
        MutablePicoContainer container = createPicoContainer(null);
        container.addComponent(Map.class, HashMap.class);
        container.start();
        try {
            container.removeComponent(Map.class);
            fail("should have barfed");
        } catch (PicoCompositionException e) {
        }
    }
}