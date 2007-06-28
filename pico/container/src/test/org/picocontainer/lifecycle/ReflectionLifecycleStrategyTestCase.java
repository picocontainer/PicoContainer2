/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.lifecycle;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.Disposable;
import org.picocontainer.Startable;
import org.picocontainer.lifecycle.ReflectionLifecycleStrategy;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Constraint;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author Paul Hammant
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 */
public class ReflectionLifecycleStrategyTestCase extends MockObjectTestCase {

    private ReflectionLifecycleStrategy strategy;
    private Mock componentMonitorMock;
    
    public void setUp(){
        componentMonitorMock = mock(ComponentMonitor.class);
        strategy = new ReflectionLifecycleStrategy((ComponentMonitor)componentMonitorMock.proxy());
    }

    public void testStartable(){
        Object startable = mockComponent(true, false);
        strategy.start(startable);
        strategy.stop(startable);
        strategy.dispose(startable);
    }

    public void testDisposable(){
        Object disposable = mockComponent(false, true);
        strategy.start(disposable);
        strategy.stop(disposable);
        strategy.dispose(disposable);
    }

    public void testNotStartableNorDisposable(){
        Object serializable = mock(Serializable.class);
        assertFalse(strategy.hasLifecycle(serializable.getClass()));
        strategy.start(serializable);
        strategy.stop(serializable);
        strategy.dispose(serializable);
    }
    
    public void testMonitorChanges() {
        Mock componentMonitorMock2 = mock(ComponentMonitor.class);
        Mock mock = mock(Disposable.class);
        Object disposable = mock.proxy();
        mock.expects(once()).method("dispose");
        componentMonitorMock.expects(once()).method("invoking").with(NULL, NULL, method("dispose"), same(mock.proxy()));
        componentMonitorMock.expects(once()).method("invoked").with(new Constraint[] {NULL, NULL, method("dispose"), same(mock.proxy()), ANYTHING});
        strategy.dispose(disposable);
        strategy.changeMonitor((ComponentMonitor)componentMonitorMock2.proxy());
        mock.expects(once()).method("dispose");
        componentMonitorMock2.expects(once()).method("invoking").with(NULL, NULL, method("dispose"), same(mock.proxy()));
        componentMonitorMock2.expects(once()).method("invoked").with(new Constraint[] {NULL, NULL, method("dispose"), same(mock.proxy()), ANYTHING});
        strategy.dispose(disposable);
    }
    
    static interface MyLifecylce {
        void start();
        void stop();
        void dispose();
    }
    
    public void testWithDifferentTypes() {
        Mock anotherStartableMock = mock(MyLifecylce.class);
        anotherStartableMock.expects(once()).method("start");
        anotherStartableMock.expects(once()).method("stop");
        anotherStartableMock.expects(once()).method("dispose");
        componentMonitorMock.expects(once()).method("invoking").with(NULL, NULL, method("start"), same(anotherStartableMock.proxy()));
        componentMonitorMock.expects(once()).method("invoked").with(new Constraint[] {NULL, NULL, method("start"), same(anotherStartableMock.proxy()), ANYTHING});
        componentMonitorMock.expects(once()).method("invoking").with(NULL, NULL, method("stop"), same(anotherStartableMock.proxy()));
        componentMonitorMock.expects(once()).method("invoked").with(new Constraint[] {NULL, NULL, method("stop"), same(anotherStartableMock.proxy()), ANYTHING});
        componentMonitorMock.expects(once()).method("invoking").with(NULL, NULL, method("dispose"), same(anotherStartableMock.proxy()));
        componentMonitorMock.expects(once()).method("invoked").with(new Constraint[] {NULL, NULL, method("dispose"), same(anotherStartableMock.proxy()), ANYTHING});

        Object startable = mockComponent(true, false);
        strategy.start(startable);
        strategy.stop(startable);
        strategy.dispose(startable);
        startable = anotherStartableMock.proxy();
        strategy.start(startable);
        strategy.stop(startable);
        strategy.dispose(startable);
    }
    
    private Object mockComponent(boolean startable, boolean disposable) {
        Mock mock = mock(Serializable.class);
        if ( startable ) {
            mock = mock(Startable.class);
            mock.expects(atLeastOnce()).method("start");
            mock.expects(atLeastOnce()).method("stop");
            componentMonitorMock.expects(once()).method("invoking").with(NULL, NULL, method("start"), same(mock.proxy()));
            componentMonitorMock.expects(once()).method("invoked").with(new Constraint[] {NULL, NULL, method("start"), same(mock.proxy()), ANYTHING});
            componentMonitorMock.expects(once()).method("invoking").with(NULL, NULL, method("stop"), same(mock.proxy()));
            componentMonitorMock.expects(once()).method("invoked").with(new Constraint[] {NULL, NULL, method("stop"), same(mock.proxy()), ANYTHING});
        }
        if ( disposable ) {
            mock = mock(Disposable.class);
            mock.expects(atLeastOnce()).method("dispose");
            componentMonitorMock.expects(once()).method("invoking").with(NULL, NULL, method("dispose"), same(mock.proxy()));
            componentMonitorMock.expects(once()).method("invoked").with(new Constraint[] {NULL, NULL, method("dispose"), same(mock.proxy()), ANYTHING});
        }
        return mock.proxy();
    }
    
    MethodNameIsEqual method(String name) {
        return new MethodNameIsEqual(name);
    }
    
    static class MethodNameIsEqual implements Constraint {

        private final String name;

        public MethodNameIsEqual(String name) {
            this.name = name;
        }
        
        public boolean eval(Object o) {
            return o instanceof Method && ((Method)o).getName().equals(name);
        }

        public StringBuffer describeTo(StringBuffer buffer) {
            buffer.append("a method with name <");
            buffer.append(name);
            return buffer.append('>');
        }
        
    }
}
