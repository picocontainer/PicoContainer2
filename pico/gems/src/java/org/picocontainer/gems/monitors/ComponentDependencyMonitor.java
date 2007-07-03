/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.gems.monitors;

import java.lang.reflect.Constructor;

import org.picocontainer.monitors.AbstractComponentMonitor;
import org.picocontainer.gems.monitors.prefuse.ComponentDependencyListener;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

/**
 * Understands how to capture component dependency information from
 * picocontainer.
 * 
 * @author Peter Barry
 * @author Kent R. Spillner
 */
public final class ComponentDependencyMonitor extends AbstractComponentMonitor {

    private final ComponentDependencyListener listener;

    public ComponentDependencyMonitor(ComponentDependencyListener listener) {
        this.listener = listener;
    }

    public void instantiated(PicoContainer container, ComponentAdapter componentAdapter,
                             Constructor constructor,
                             Object instantiated,
                             Object[] injected,
                             long duration) {
        Class componentType = instantiated.getClass();
        int count = injected.length;

        if (count == 0) {
            listener.addDependency(new Dependency(componentType, null));
        }

        for (int i = 0; i < count; i++) {
            Object dependent = injected[i];
            Dependency dependency = new Dependency(componentType, dependent.getClass());
            listener.addDependency(dependency);
        }
    }

    /**
     * Understands which other classes are required to instantiate a component.
     * 
     * @author Peter Barry
     * @author Kent R. Spillner
     */
    public static final class Dependency {

        private final Class componentType;

        private final Class dependencyType;

        public Dependency(Class componentType, Class dependencyType) {
            this.componentType = componentType;
            this.dependencyType = dependencyType;
        }

        public boolean dependsOn(Class type) {
            return (type != null) && type.equals(dependencyType);
        }

        public boolean equals(Object other) {
            if (other instanceof Dependency) {
                Dependency otherDependency = (Dependency) other;
                return areEqualOrNull(componentType, otherDependency.componentType)
                        && areEqualOrNull(dependencyType, otherDependency.dependencyType);
            }
            return false;
        }

        public Class getComponentType() {
            return componentType;
        }

        public Class getDependencyType() {
            return dependencyType;
        }

        public String toString() {
            return componentType + " depends on " + dependencyType;
        }

        private static boolean areEqualOrNull(Class type, Class otherType) {
            if (type != null) {
                return type.equals(otherType);
            }
            return (otherType == null);
        }
    }
}
