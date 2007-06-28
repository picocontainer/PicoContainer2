/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.parameters.ConstantParameter;
import org.picocontainer.visitors.TraversalCheckingVisitor;
import org.picocontainer.injectors.SetterInjector;

/**
 * @author Michael Rimov
 */
public class TraversalCheckingVisitorTestCase extends TestCase {

    private MutablePicoContainer pico;

    private MutablePicoContainer child;

    private ComponentAdapter parentAdapter;

    private ComponentAdapter childAdapter;

    protected void setUp() throws Exception {
        super.setUp();

        pico = new DefaultPicoContainer();
        SetterInjector componentAdapter = new SetterInjector(StringBuffer.class, StringBuffer.class,
                                                             null, NullComponentMonitor.getInstance(), NullLifecycleStrategy.getInstance());
        parentAdapter = pico.addAdapter(componentAdapter).getComponentAdapter(StringBuffer.class);
        child = pico.makeChildContainer();
        ConstructorInjector adapter = new ConstructorInjector(ArrayList.class, ArrayList.class, new ConstantParameter(3));
        childAdapter = child.addAdapter(adapter).getComponentAdapter(ArrayList.class);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        child = null;
        pico = null;
        parentAdapter = null;
        childAdapter = null;
    }

    public void testVisitComponentAdapter() {
        final int numExpectedComponentAdapters = 2;
        final List<ComponentAdapter> allAdapters = new ArrayList<ComponentAdapter>();

        Set<ComponentAdapter> knownAdapters = new HashSet<ComponentAdapter>();
        knownAdapters.add(parentAdapter);
        knownAdapters.add(childAdapter);

        PicoVisitor containerCollector = new TraversalCheckingVisitor() {
            public void visitComponentAdapter(ComponentAdapter adapter) {
                super.visitComponentAdapter(adapter); //Calls checkTraversal for us.
                allAdapters.add(adapter);
            }
        };
        containerCollector.traverse(pico);

        assertEquals(numExpectedComponentAdapters, allAdapters.size());

        for (ComponentAdapter allAdapter : allAdapters) {
            boolean knownAdapter = knownAdapters.remove(allAdapter);
            assertTrue("Encountered unknown adapter in collection: " + allAdapters.toString(), knownAdapter);
        }

        assertTrue("All adapters should match known adapters.", knownAdapters.size() == 0);
    }

    public void testVisitContainer() {
        final List<PicoContainer> allContainers = new ArrayList<PicoContainer>();
        final int expectedNumberOfContainers = 2;

        PicoVisitor containerCollector = new TraversalCheckingVisitor() {
            public void visitContainer(PicoContainer pico) {
                super.visitContainer(pico); //Calls checkTraversal for us.
                allContainers.add(pico);
            }
        };

        containerCollector.traverse(pico);

        assertTrue(allContainers.size() == expectedNumberOfContainers);

        Set<MutablePicoContainer> knownContainers = new HashSet<MutablePicoContainer>();
        knownContainers.add(pico);
        knownContainers.add(child);
        for (PicoContainer oneContainer : allContainers) {
            boolean knownContainer = knownContainers.remove(oneContainer);
            assertTrue("Found a picocontainer that wasn't previously expected.", knownContainer);
        }

        assertTrue("All containers must match what is returned by traversal.",
            knownContainers.size() == 0);

    }


    public void testVisitParameter() {
        final List allParameters = new ArrayList();

        PicoVisitor containerCollector = new TraversalCheckingVisitor() {
            public void visitParameter(Parameter param) {
                super.visitParameter(param); //Calls checkTraversal for us.
                allParameters.add(param);
            }
        };

        containerCollector.traverse(pico);

        assertTrue(allParameters.size() == 1);
        assertTrue(allParameters.get(0) instanceof ConstantParameter);
        assertTrue( ( (ConstantParameter) allParameters.get(0)).resolveInstance(null, null, null, null) instanceof Integer);
        assertEquals(3, ( (Integer) ( (ConstantParameter) allParameters.get(0)).resolveInstance(null, null,
            null, null)).intValue());
    }

}    