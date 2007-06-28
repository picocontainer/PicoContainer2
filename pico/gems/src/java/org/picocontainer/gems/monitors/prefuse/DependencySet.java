package org.picocontainer.gems.monitors.prefuse;

import java.util.HashSet;
import java.util.Set;

import org.picocontainer.gems.monitors.ComponentDependencyMonitor.Dependency;

/**
 * Understands non-duplicated dependencies.
 * 
 * @author Peter Barry
 * @author Kent R. Spillner
 */
public final class DependencySet implements ComponentDependencyListener {

    private final Set uniqueDependencies = new HashSet();

    private final ComponentDependencyListener listener;

    public DependencySet(ComponentDependencyListener listener) {
        this.listener = listener;
    }

    public void addDependency(Dependency dependency) {
        if (uniqueDependencies.add(dependency)) {
            listener.addDependency(dependency);
        }
    }

    public Dependency[] getDependencies() {
        return (Dependency[]) uniqueDependencies.toArray(new Dependency[uniqueDependencies.size()]);
    }
}
