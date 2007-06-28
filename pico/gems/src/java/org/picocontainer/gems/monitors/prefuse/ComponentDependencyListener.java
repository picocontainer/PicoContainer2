package org.picocontainer.gems.monitors.prefuse;

import org.picocontainer.gems.monitors.ComponentDependencyMonitor.Dependency;

/**
 * Interprets dependency-related events.
 * 
 * @author Peter Barry
 * @author Kent R. Spillner
 */
public interface ComponentDependencyListener {
    public void addDependency(Dependency dependency);
}
