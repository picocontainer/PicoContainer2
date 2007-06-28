package org.picocontainer.gems.monitors.prefuse;

import junit.framework.TestCase;

import org.picocontainer.gems.monitors.ComponentDependencyMonitor.Dependency;

public class DependencySetTestCase extends TestCase {
    int callCount = 0;

    public void testShouldNotAddDuplicates() throws Exception {
        ComponentDependencyListener mockListener = new ComponentDependencyListener(){
            public void addDependency(Dependency dependency) {
             callCount++;
            }    
        };       
        DependencySet set = new DependencySet(mockListener);
        Dependency dependency = new Dependency(Object.class, String.class);
        set.addDependency(dependency);
        set.addDependency(dependency);
        assertEquals(1, set.getDependencies().length);
        assertEquals(dependency, set.getDependencies()[0]);
        assertEquals("Call count should be called once",1,callCount );
    }
}
