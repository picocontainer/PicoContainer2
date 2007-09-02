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

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Disposable;
import org.picocontainer.Behavior;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.Startable;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.Characteristics;
import org.picocontainer.behaviors.AbstractBehavior;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.adapters.InstanceAdapter;
import org.picocontainer.parameters.BasicComponentParameter;
import org.picocontainer.parameters.ConstantParameter;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.testmodel.Washable;
import org.picocontainer.testmodel.WashableTouchable;
import org.picocontainer.visitors.AbstractPicoVisitor;
import org.picocontainer.visitors.VerifyingVisitor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Properties;

import junit.framework.Assert;
import org.jmock.MockObjectTestCase;

/** This test tests (at least it should) all the methods in MutablePicoContainer. */
public abstract class AbstractPicoContainerTestCase extends MockObjectTestCase {

    protected abstract MutablePicoContainer createPicoContainer(PicoContainer parent);

    protected final MutablePicoContainer createPicoContainerWithDependsOnTouchableOnly() throws PicoCompositionException {
        MutablePicoContainer pico = createPicoContainer(null);
        pico.addComponent(DependsOnTouchable.class);
        return pico;

    }

    protected final MutablePicoContainer createPicoContainerWithTouchableAndDependsOnTouchable() throws PicoCompositionException {
        MutablePicoContainer pico = createPicoContainerWithDependsOnTouchableOnly();
        pico.as(Characteristics.CACHE).addComponent(Touchable.class, SimpleTouchable.class);
        return pico;
    }

    public void testBasicInstantiationAndContainment() throws PicoException {
        PicoContainer pico = createPicoContainerWithTouchableAndDependsOnTouchable();
        assertTrue("Component should be instance of Touchable",
                   Touchable.class.isAssignableFrom(pico.getComponentAdapter(Touchable.class, null).getComponentImplementation()));
    }

    public void testRegisteredComponentsExistAndAreTheCorrectTypes() throws PicoException {
        PicoContainer pico = createPicoContainerWithTouchableAndDependsOnTouchable();
        assertNotNull("Container should have Touchable addComponent",
                      pico.getComponentAdapter(Touchable.class, null));
        assertNotNull("Container should have DependsOnTouchable addComponent",
                      pico.getComponentAdapter(DependsOnTouchable.class, null));
        assertTrue("Component should be instance of Touchable",
                   pico.getComponent(Touchable.class) != null);
        assertTrue("Component should be instance of DependsOnTouchable",
                   pico.getComponent(DependsOnTouchable.class) != null);
        assertNull("should not have non existent addComponent", pico.getComponentAdapter(Map.class, null));
    }

    public void testRegistersSingleInstance() throws PicoException {
        MutablePicoContainer pico = createPicoContainer(null);
        StringBuffer sb = new StringBuffer();
        pico.addComponent(sb);
        assertSame(sb, pico.getComponent(StringBuffer.class));
    }

    public void testContainerIsSerializable() throws PicoException,
                                                     IOException, ClassNotFoundException
    {

        getTouchableFromSerializedContainer();

    }

    private Touchable getTouchableFromSerializedContainer() throws IOException, ClassNotFoundException {
        MutablePicoContainer pico = createPicoContainerWithTouchableAndDependsOnTouchable();
        // Add a list too, using a constant parameter
        pico.addComponent("list", ArrayList.class, new ConstantParameter(10));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(pico);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));

        pico = (MutablePicoContainer)ois.readObject();

        DependsOnTouchable dependsOnTouchable = pico.getComponent(DependsOnTouchable.class);
        assertNotNull(dependsOnTouchable);
        return pico.getComponent(Touchable.class);
    }

    public void testSerializedContainerCanRetrieveImplementation() throws PicoException,
                                                                          IOException, ClassNotFoundException
    {

        Touchable touchable = getTouchableFromSerializedContainer();

        SimpleTouchable simpleTouchable = (SimpleTouchable)touchable;

        assertTrue(simpleTouchable.wasTouched);
    }


    public void testGettingComponentWithMissingDependencyFails() throws PicoException {
        PicoContainer picoContainer = createPicoContainerWithDependsOnTouchableOnly();
        try {
            picoContainer.getComponent(DependsOnTouchable.class);
            fail("should need a Touchable");
        } catch (AbstractInjector.UnsatisfiableDependenciesException e) {
            assertSame(picoContainer.getComponentAdapter(DependsOnTouchable.class, null).getComponentImplementation(),
                       e.getUnsatisfiableComponentAdapter().getComponentImplementation());
            final Set unsatisfiableDependencies = e.getUnsatisfiableDependencies();
            assertEquals(1, unsatisfiableDependencies.size());

            // Touchable.class is now inside a List (the list of unsatisfied parameters) -- mparaz
            List unsatisfied = (List)unsatisfiableDependencies.iterator().next();
            assertEquals(1, unsatisfied.size());
            assertEquals(Touchable.class, unsatisfied.get(0));
        }
    }

    public void testDuplicateRegistration() {
        try {
            MutablePicoContainer pico = createPicoContainer(null);
            pico.addComponent(Object.class);
            pico.addComponent(Object.class);
            fail("Should have failed with duplicate registration");
        } catch (PicoCompositionException e) {
            assertTrue("Wrong key", e.getMessage().indexOf(Object.class.toString()) > -1);
        }
    }

    public void testExternallyInstantiatedObjectsCanBeRegisteredAndLookedUp() throws PicoException {
        MutablePicoContainer pico = createPicoContainer(null);
        final HashMap map = new HashMap();
        pico.as(getProperties()).addComponent(Map.class, map);
        assertSame(map, pico.getComponent(Map.class));
    }

    public void testAmbiguousResolution() throws PicoCompositionException {
        MutablePicoContainer pico = createPicoContainer(null);
        pico.addComponent("ping", String.class);
        pico.addComponent("pong", "pang");
        try {
            pico.getComponent(String.class);
        } catch (AbstractInjector.AmbiguousComponentResolutionException e) {
            assertTrue(e.getMessage().indexOf("java.lang.String") != -1);
        }
    }

    public void testLookupWithUnregisteredKeyReturnsNull() throws PicoCompositionException {
        MutablePicoContainer pico = createPicoContainer(null);
        assertNull(pico.getComponent(String.class));
    }

    public void testLookupWithUnregisteredTypeReturnsNull() throws PicoCompositionException {
        MutablePicoContainer pico = createPicoContainer(null);
        assertNull(pico.getComponent(String.class));
    }

    public static class ListAdder {
        public ListAdder(Collection<String> list) {
            list.add("something");
        }
    }

    public void testUnsatisfiableDependenciesExceptionGivesVerboseEnoughErrorMessage() {
        MutablePicoContainer pico = createPicoContainer(null);
        pico.addComponent(ComponentD.class);

        try {
            pico.getComponent(ComponentD.class);
        } catch (AbstractInjector.UnsatisfiableDependenciesException e) {
            Set unsatisfiableDependencies = e.getUnsatisfiableDependencies();
            assertEquals(1, unsatisfiableDependencies.size());

            List list = (List)unsatisfiableDependencies.iterator().next();

            final List<Class> expectedList = new ArrayList<Class>(2);
            expectedList.add(ComponentE.class);
            expectedList.add(ComponentB.class);

            assertEquals(expectedList, list);
        }
    }

    public void testUnsatisfiableDependenciesExceptionGivesUnsatisfiedDependencyTypes() {
        MutablePicoContainer pico = createPicoContainer(null);
        // D depends on E and B
        pico.addComponent(ComponentD.class);

        // first - do not register any dependency
        // should yield first unsatisfied dependency
        try {
            pico.getComponent(ComponentD.class);
        } catch (AbstractInjector.UnsatisfiableDependenciesException e) {
            Set unsatisfiableDependencies = e.getUnsatisfiableDependencies();
            assertEquals(1, unsatisfiableDependencies.size());
            List list = (List)unsatisfiableDependencies.iterator().next();
            final List<Class> expectedList = new ArrayList<Class>(2);
            expectedList.add(ComponentE.class);
            expectedList.add(ComponentB.class);
            assertEquals(expectedList, list);

            Class unsatisfiedDependencyType = e.getUnsatisfiedDependencyType();
            assertNotNull(unsatisfiedDependencyType);
            assertEquals(ComponentE.class, unsatisfiedDependencyType);
        }

        // now register only first dependency
        // should yield second unsatisfied dependency
        pico.addComponent(ComponentE.class);
        try {
            pico.getComponent(ComponentD.class);
        } catch (AbstractInjector.UnsatisfiableDependenciesException e) {
            Set unsatisfiableDependencies = e.getUnsatisfiableDependencies();
            assertEquals(1, unsatisfiableDependencies.size());
            List list = (List)unsatisfiableDependencies.iterator().next();
            final List<Class> expectedList = new ArrayList<Class>(2);
            expectedList.add(ComponentE.class);
            expectedList.add(ComponentB.class);
            assertEquals(expectedList, list);

            Class unsatisfiedDependencyType = e.getUnsatisfiedDependencyType();
            assertNotNull(unsatisfiedDependencyType);
            assertEquals(ComponentB.class, unsatisfiedDependencyType);
        }
    }

    public void testCyclicDependencyThrowsCyclicDependencyException() {
        assertCyclicDependencyThrowsCyclicDependencyException(createPicoContainer(null));
    }

    private static void assertCyclicDependencyThrowsCyclicDependencyException(MutablePicoContainer pico) {
        pico.addComponent(ComponentB.class);
        pico.addComponent(ComponentD.class);
        pico.addComponent(ComponentE.class);

        try {
            pico.getComponent(ComponentD.class);
            fail("CyclicDependencyException expected");
        } catch (AbstractInjector.CyclicDependencyException e) {
            // CyclicDependencyException reports now the stack.
            //final List dependencies = Arrays.asList(ComponentD.class.getConstructors()[0].getParameterTypes());
            final List<Class> dependencies = Arrays.<Class>asList(ComponentD.class, ComponentE.class, ComponentD.class);
            final List<Class> reportedDependencies = Arrays.asList(e.getDependencies());
            assertEquals(dependencies, reportedDependencies);
        } catch (StackOverflowError e) {
            fail();
        }
    }

    public void testCyclicDependencyThrowsCyclicDependencyExceptionWithParentContainer() {
        MutablePicoContainer pico = createPicoContainer(createPicoContainer(null));
        assertCyclicDependencyThrowsCyclicDependencyException(pico);
    }

    public void testRemovalNonRegisteredComponentAdapterWorksAndReturnsNull() {
        final MutablePicoContainer picoContainer = createPicoContainer(null);
        assertNull(picoContainer.removeComponent("COMPONENT DOES NOT EXIST"));
    }

    /** Important! Nanning really, really depends on this! */
    public void testComponentAdapterRegistrationOrderIsMaintained() throws NoSuchMethodException {

        ConstructorInjector c1 = new ConstructorInjector("1", Object.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false);
        ConstructorInjector c2 = new ConstructorInjector("2", String.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false);

        MutablePicoContainer picoContainer = createPicoContainer(null);
        picoContainer.addAdapter(c1).addAdapter(c2);
        Collection<ComponentAdapter<?>> list2 = picoContainer.getComponentAdapters();
        //registration order should be maintained
        assertEquals(2, list2.size());
        assertEquals(c1.getComponentKey(), ((ComponentAdapter)list2.toArray()[0]).getComponentKey());
        assertEquals(c2.getComponentKey(), ((ComponentAdapter)list2.toArray()[1]).getComponentKey());

        picoContainer.getComponents(); // create all the instances at once
        assertFalse("instances should be created in same order as adapters are created",
                    picoContainer.getComponents().get(0) instanceof String);
        assertTrue("instances should be created in same order as adapters are created",
                   picoContainer.getComponents().get(1) instanceof String);

        MutablePicoContainer reversedPicoContainer = createPicoContainer(null);
        reversedPicoContainer.addAdapter(c2);
        reversedPicoContainer.addAdapter(c1);
        //registration order should be maintained
        list2 = reversedPicoContainer.getComponentAdapters();
        assertEquals(2, list2.size());
        assertEquals(c2.getComponentKey(), ((ComponentAdapter)list2.toArray()[0]).getComponentKey());
        assertEquals(c1.getComponentKey(), ((ComponentAdapter)list2.toArray()[1]).getComponentKey());

        reversedPicoContainer.getComponents(); // create all the instances at once
        assertTrue("instances should be created in same order as adapters are created",
                   reversedPicoContainer.getComponents().get(0) instanceof String);
        assertFalse("instances should be created in same order as adapters are created",
                    reversedPicoContainer.getComponents().get(1) instanceof String);
    }

    public static final class NeedsTouchable {
        public final Touchable touchable;

        public NeedsTouchable(Touchable touchable) {
            this.touchable = touchable;
        }
    }

    public static final class NeedsWashable {
        public final Washable washable;

        public NeedsWashable(Washable washable) {
            this.washable = washable;
        }
    }

    public void testSameInstanceCanBeUsedAsDifferentTypeWhenCaching() {
        MutablePicoContainer pico = createPicoContainer(null);
        pico.as(Characteristics.CACHE).addComponent("wt", WashableTouchable.class);
        pico.addComponent("nw", NeedsWashable.class);
        pico.as(Characteristics.CACHE).addComponent("nt", NeedsTouchable.class);

        NeedsWashable nw = (NeedsWashable)pico.getComponent("nw");
        NeedsTouchable nt = (NeedsTouchable)pico.getComponent("nt");
        assertSame(nw.washable, nt.touchable);
    }

    public void testRegisterComponentWithObjectBadType() throws PicoCompositionException {
        MutablePicoContainer pico = createPicoContainer(null);

        try {
            pico.addComponent(Serializable.class, new Object());
            fail("Shouldn't be able to register an Object.class as Serializable because it is not, " +
                 "it does not implement it, Object.class does not implement much.");
        } catch (ClassCastException e) {
        }

    }

    public static class JMSService {
        public final String serverid;
        public final String path;

        public JMSService(String serverid, String path) {
            this.serverid = serverid;
            this.path = path;
        }
    }

    // http://jira.codehaus.org/secure/ViewIssue.jspa?key=PICO-52
    public void testPico52() {
        MutablePicoContainer pico = createPicoContainer(null);

        pico.addComponent("foo", JMSService.class, new ConstantParameter("0"), new ConstantParameter("something"));
        JMSService jms = (JMSService)pico.getComponent("foo");
        assertEquals("0", jms.serverid);
        assertEquals("something", jms.path);
    }

    public static class ComponentA {
        public ComponentA(ComponentB b, ComponentC c) {
            Assert.assertNotNull(b);
            Assert.assertNotNull(c);
        }
    }

    public static class ComponentB {
    }

    public static class ComponentC {
    }

    public static class ComponentD {
        public ComponentD(ComponentE e, ComponentB b) {
            Assert.assertNotNull(e);
            Assert.assertNotNull(b);
        }
    }

    public static class ComponentE {
        public ComponentE(ComponentD d) {
            Assert.assertNotNull(d);
        }
    }

    public static class ComponentF {
        public ComponentF(ComponentA a) {
            Assert.assertNotNull(a);
        }
    }

    public void testAggregatedVerificationException() {
        MutablePicoContainer pico = createPicoContainer(null);
        pico.addComponent(ComponentA.class);
        pico.addComponent(ComponentE.class);
        try {
            new VerifyingVisitor().traverse(pico);
            fail("we expect a PicoVerificationException");
        } catch (PicoVerificationException e) {
            List nested = e.getNestedExceptions();
            assertEquals(2, nested.size());
            assertTrue(-1 != e.getMessage().indexOf(ComponentA.class.getName()));
            assertTrue(-1 != e.getMessage().indexOf(ComponentE.class.getName()));
        }
    }

    // An adapter has no longer a hosting container.

//    public void testRegistrationOfAdapterSetsHostingContainerAsSelf() {
//        final InstanceAdapter componentAdapter = new InstanceAdapter("", new Object());
//        final MutablePicoContainer picoContainer = createPicoContainer(null);
//        picoContainer.addAdapter(componentAdapter);
//        assertSame(picoContainer, componentAdapter.getContainer());
//    }

    public static class ContainerDependency {
        public ContainerDependency(PicoContainer container) {
            assertNotNull(container);
        }
    }

    // ImplicitPicoContainer injection is bad. It is an open door for hackers. Developers with
    // special PicoContainer needs should specifically register() a comtainer they want components to
    // be able to pick up on.

//    public void testImplicitPicoContainerInjection() {
//        MutablePicoContainer pico = createPicoContainer(null);
//        pico.addAdapter(ContainerDependency.class);
//        ContainerDependency dep = (ContainerDependency) pico.getComponent(ContainerDependency.class);
//        assertSame(pico, dep.pico);
//    }

    public void testShouldReturnNullWhenUnregistereingUnmanagedComponent() {
        final MutablePicoContainer pico = createPicoContainer(null);
        assertNull(pico.removeComponentByInstance("yo"));
    }

    public void testShouldReturnNullForComponentAdapterOfUnregisteredType() {
        final MutablePicoContainer pico = createPicoContainer(null);
        assertNull(pico.getComponent(List.class));
    }

    public void testShouldReturnNonMutableParent() {
        DefaultPicoContainer parent = new DefaultPicoContainer();
        final MutablePicoContainer picoContainer = createPicoContainer(parent);
        assertNotSame(parent, picoContainer.getParent());
        assertFalse(picoContainer.getParent() instanceof MutablePicoContainer);
    }

    class Foo implements Startable, Disposable {
        public boolean started;
        public boolean stopped;
        public boolean disposed;

        public void start() {
            started = true;
        }

        public void stop() {
            stopped = true;
        }

        public void dispose() {
            disposed = true;
        }

    }

    public void testContainerCascadesDefaultLifecycle() {
        final MutablePicoContainer picoContainer = createPicoContainer(null);
        Foo foo = new Foo();
        picoContainer.addComponent(foo);
        picoContainer.start();
        assertEquals(true, foo.started);
        picoContainer.stop();
        assertEquals(true, foo.stopped);
        picoContainer.dispose();
        assertEquals(true, foo.disposed);
    }

    public void testComponentInstancesFromParentsAreNotDirectlyAccessible2() {
        final MutablePicoContainer a = createPicoContainer(null);
        final MutablePicoContainer b = createPicoContainer(a);
        final MutablePicoContainer c = createPicoContainer(b);

        Object ao = new Object();
        Object bo = new Object();
        Object co = new Object();

        a.addComponent("a", ao);
        b.addComponent("b", bo);
        c.addComponent("c", co);

        assertEquals(1, a.getComponents().size());
        assertEquals(1, b.getComponents().size());
        assertEquals(1, c.getComponents().size());
    }

    public void testStartStopAndDisposeCascadedtoChildren() {
        final MutablePicoContainer parent = createPicoContainer(null);
        parent.addComponent(new StringBuffer());
        final MutablePicoContainer child = createPicoContainer(parent);
        parent.addChildContainer(child);
        child.addComponent(LifeCycleMonitoring.class);
        parent.start();
        try {
            child.start();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            assertEquals("child already started", "Already started", e.getMessage());
        }
        parent.stop();
        try {
            child.stop();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            assertEquals("child not started", "Not started", e.getMessage());
        }
        parent.dispose();
        try {
            child.dispose();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            assertEquals("child already disposed", "Already disposed", e.getMessage());
        }

    }

    public void testMakingOfChildContainer() {
        final MutablePicoContainer parent = createPicoContainer(null);
        MutablePicoContainer child = parent.makeChildContainer();
        assertNotNull(child);
    }

    public void testMakingOfChildContainerPercolatesLifecycleManager() {
        final MutablePicoContainer parent = createPicoContainer(null);
        parent.addComponent("one", TestLifecycleComponent.class);
        MutablePicoContainer child = parent.makeChildContainer();
        assertNotNull(child);
        child.addComponent("two", TestLifecycleComponent.class);
        parent.start();
        try {
            child.start();
        } catch (IllegalStateException e) {
            assertEquals("child already started", "Already started", e.getMessage());
        }
        //TODO - The Behavior reference in child containers is not used. Thus is is almost pointless
        // The reason is because DefaultPicoContainer's accept() method visits child containers' on its own.
        // This may be file for visiting components in a tree for general cases, but for lifecycle, we
        // should hand to each Behavior's start(..) at each appropriate node. See mail-list discussion.
    }

    public static final class TestBehavior extends AbstractBehavior implements Behavior {

        public final ArrayList<PicoContainer> started = new ArrayList<PicoContainer>();

        public TestBehavior(ComponentAdapter delegate) {
            super(delegate);
        }

        public void start(PicoContainer node) {
            started.add(node);
        }

        public void stop(PicoContainer node) {
        }

        public void dispose(PicoContainer node) {
        }

        public boolean componentHasLifecycle() {
            return true;
        }

        public String getDescriptor() {
            return null;
        }
    }

    public static class TestLifecycleComponent implements Startable {
        public boolean started;

        public void start() {
            started = true;
        }

        public void stop() {
        }
    }

    public void testStartStopAndDisposeNotCascadedtoRemovedChildren() {
        final MutablePicoContainer parent = createPicoContainer(null);
        parent.addComponent(new StringBuffer());
        StringBuffer sb = parent.getComponents(StringBuffer.class).get(0);

        final MutablePicoContainer child = createPicoContainer(parent);
        assertEquals(parent, parent.addChildContainer(child));
        child.addComponent(LifeCycleMonitoring.class);
        assertTrue(parent.removeChildContainer(child));
        parent.start();
        assertTrue(sb.toString().indexOf("-started") == -1);
        parent.stop();
        assertTrue(sb.toString().indexOf("-stopped") == -1);
        parent.dispose();
        assertTrue(sb.toString().indexOf("-disposed") == -1);
    }

    public void testShouldCascadeStartStopAndDisposeToChild() {

        StringBuffer sb = new StringBuffer();
        final MutablePicoContainer parent = createPicoContainer(null);
        parent.addComponent(sb);
        parent.addComponent(Map.class, HashMap.class);

        final MutablePicoContainer child = parent.makeChildContainer();
        child.addComponent(LifeCycleMonitoring.class);

        Map map = parent.getComponent(Map.class);
        assertNotNull(map);
        parent.start();
        try {
            child.start();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            assertEquals("child already started", "Already started", e.getMessage());
        }
        parent.stop();
        try {
            child.stop();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            assertEquals("child not started", "Not started", e.getMessage());
        }
        parent.dispose();
        try {
            child.dispose();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            assertEquals("child already disposed", "Already disposed", e.getMessage());
        }
    }

    public static final class LifeCycleMonitoring implements Startable, Disposable {
        final StringBuffer sb;

        public LifeCycleMonitoring(StringBuffer sb) {
            this.sb = sb;
            sb.append("-instantiated");
        }

        public void start() {
            sb.append("-started");
        }

        public void stop() {
            sb.append("-stopped");
        }

        public void dispose() {
            sb.append("-disposed");
        }
    }

    public static class RecordingStrategyVisitor extends AbstractPicoVisitor {

        private final List<Object> list;

        public RecordingStrategyVisitor(List<Object> list) {
            this.list = list;
        }

        public void visitContainer(PicoContainer pico) {
            list.add(pico);
        }

        public void visitComponentAdapter(ComponentAdapter componentAdapter) {
            list.add(componentAdapter);
        }

        public void visitParameter(Parameter parameter) {
            list.add(parameter);
        }

    }

    protected abstract Properties[] getProperties();

    public void testAcceptImplementsBreadthFirstStrategy() {
        final MutablePicoContainer parent = createPicoContainer(null);
        final MutablePicoContainer child = parent.makeChildContainer();
        ComponentAdapter hashMapAdapter =
            parent.as(getProperties()).addAdapter(new ConstructorInjector(HashMap.class, HashMap.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false))
                .getComponentAdapter(HashMap.class, null);
        ComponentAdapter hashSetAdapter =
            parent.as(getProperties()).addAdapter(new ConstructorInjector(HashSet.class, HashSet.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false))
                .getComponentAdapter(HashSet.class, null);
        InstanceAdapter instanceAdapter = new InstanceAdapter(String.class, "foo",
                                                              new NullLifecycleStrategy(),
                                                              new NullComponentMonitor());
        ComponentAdapter stringAdapter = parent.as(getProperties()).addAdapter(instanceAdapter).getComponentAdapter(instanceAdapter.getComponentKey());
        ComponentAdapter arrayListAdapter =
            child.as(getProperties()).addAdapter(new ConstructorInjector(ArrayList.class, ArrayList.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false))
                .getComponentAdapter(ArrayList.class, null);
        Parameter componentParameter = BasicComponentParameter.BASIC_DEFAULT;
        Parameter throwableParameter = new ConstantParameter(new Throwable("bar"));
        ConstructorInjector ci = new ConstructorInjector(Exception.class, Exception.class, new Parameter[] {componentParameter,
                                                         throwableParameter}, new NullComponentMonitor(), new NullLifecycleStrategy(), false);
        ComponentAdapter exceptionAdapter = child.as(getProperties()).addAdapter(ci).getComponentAdapter(Exception.class, null);

        List expectedList = Arrays.asList(parent,
                                          hashMapAdapter,
                                          hashSetAdapter,
                                          stringAdapter,
                                          child,
                                          arrayListAdapter,
                                          exceptionAdapter,
                                          componentParameter,
                                          throwableParameter);
        List<Object> visitedList = new LinkedList<Object>();
        PicoVisitor visitor = new RecordingStrategyVisitor(visitedList);
        visitor.traverse(parent);
        assertEquals(expectedList.size(), visitedList.size());
    }

    public void testAmbiguousDependencies() throws PicoCompositionException {

        MutablePicoContainer pico = this.createPicoContainer(null);

        // Register two Touchables that Fred will be confused about
        pico.addComponent(SimpleTouchable.class);
        pico.addComponent(DerivedTouchable.class);

        // Register a confused DependsOnTouchable
        pico.addComponent(DependsOnTouchable.class);

        try {
            pico.getComponent(DependsOnTouchable.class);
            fail("DependsOnTouchable should have been confused about the two Touchables");
        } catch (AbstractInjector.AmbiguousComponentResolutionException e) {
            List componentImplementations = Arrays.asList(e.getAmbiguousComponentKeys());
            assertTrue(componentImplementations.contains(DerivedTouchable.class));
            assertTrue(componentImplementations.contains(SimpleTouchable.class));

            assertTrue(e.getMessage().indexOf(DerivedTouchable.class.getName()) != -1);
        }
    }


    public static class DerivedTouchable extends SimpleTouchable {
        public DerivedTouchable() {
        }
    }


    public static final class NonGreedyClass {

        public final int value = 0;

        public NonGreedyClass() {
            //Do nothing.
        }

        public NonGreedyClass(ComponentA component) {
            fail("Greedy Constructor should never have been called.  Instead got: " + component);
        }


    }

    public void testNoArgConstructorToBeSelected() {
        MutablePicoContainer pico = this.createPicoContainer(null);
        pico.addComponent(ComponentA.class);
        pico.addComponent(NonGreedyClass.class, NonGreedyClass.class, Parameter.ZERO);


        NonGreedyClass instance = pico.getComponent(NonGreedyClass.class);
        assertNotNull(instance);
    }

}
