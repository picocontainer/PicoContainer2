/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.gems.monitors;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.picocontainer.gems.monitors.ComponentDependencyMonitor.Dependency;
import org.picocontainer.gems.monitors.prefuse.ComponentDependencyListener;
import org.picocontainer.testmodel.DependsOnList;

public class ComponentDependencyMonitorTestCase extends TestCase implements ComponentDependencyListener {
    private ComponentDependencyMonitor monitor;

    private Dependency dependency;

    protected void setUp() throws Exception {
        super.setUp();
        monitor = new ComponentDependencyMonitor(this);
        dependency = new Dependency(Object.class, String.class);
    }

    public void testShouldDependOnList() throws Exception {
        List list = new ArrayList();
        DependsOnList dol = new DependsOnList(list);
        monitor.instantiated(null, null, DependsOnList.class.getConstructors()[0], dol, new Object[] { list }, 10);
        assertEquals(new Dependency(DependsOnList.class, ArrayList.class), dependency);
    }

    public void addDependency(Dependency dependency) {
        this.dependency = dependency;
    }

    public void testAShouldBeDependentOnB() throws Exception {
        assertEquals(true, dependency.dependsOn(String.class));
    }

    public void testADoesntDependOnB() throws Exception {
        assertEquals(false, dependency.dependsOn(Boolean.class));
    }

    public void testADoesntDependOnNullB() throws Exception {
        assertEquals(false, dependency.dependsOn(null));
    }

    public void testShouldNotEqualNull() throws Exception {
        assertEquals("not equal to null", false, dependency.equals(null));
    }

    public void testShouldEqualSelf() throws Exception {
        assertEquals("equal to self", dependency, dependency);
    }

    public void testShouldEqualSimilarDependency() throws Exception {
        assertEquals(dependency, new Dependency(Object.class, String.class));
    }

    public void testShouldNotEqualDifferentDependency() throws Exception {
        assertEquals("not equal to different dependency", false, dependency.equals(new Dependency(Object.class,
                Object.class)));
        assertEquals("not equal to different dependency", false, dependency.equals(new Dependency(String.class,
                String.class)));
    }

    public void testShouldNotEqualObjectsWhichArentDependencies() throws Exception {
        assertEquals("not equal to different type", false, dependency.equals(new Object()));
    }

    public void testShouldNotThrowNullPointerExceptionsWhenComparingEmptyDependencies() throws Exception {
        Dependency emptyDependency = new Dependency(null, null);
        assertEquals("not equal to empty dependency", false, dependency.equals(emptyDependency));
        assertEquals("not equal to empty dependency", false, emptyDependency.equals(dependency));
    }

    public void testShouldNotThrowNullPointerExceptionsWhenComparingPartialDependencies() throws Exception {
        Dependency partialDependency = new Dependency(Boolean.class, null);
        assertEquals("not equal to empty dependency", false, dependency.equals(partialDependency));
        assertEquals("not equal to empty dependency", false, partialDependency.equals(dependency));
    }
}
