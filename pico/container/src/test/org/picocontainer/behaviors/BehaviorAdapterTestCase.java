/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.behaviors;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.Behavior;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.behaviors.AbstractBehavior;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.ComponentMonitorStrategy;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

/**
 * @author Mauro Talevi
 */
public class BehaviorAdapterTestCase extends MockObjectTestCase {

    public void testDecoratingComponentAdapterDelegatesToMonitorThatDoesSupportStrategy() {
        AbstractBehavior adapter = new FooAbstractBehavior(mockComponentAdapterThatDoesSupportStrategy());
        adapter.changeMonitor(mockMonitorWithNoExpectedMethods());
        assertNotNull(adapter.currentMonitor());
    }
    
    public void testDecoratingComponentAdapterDelegatesToMonitorThatDoesNotSupportStrategy() {
        AbstractBehavior adapter = new FooAbstractBehavior(mockComponentAdapter());
        adapter.changeMonitor(mockMonitorWithNoExpectedMethods());
        try {
            adapter.currentMonitor();
            fail("PicoCompositionException expected");
        } catch (PicoCompositionException e) {
            assertEquals("No component monitor found in delegate", e.getMessage());
        }
    }
    
    public void testDecoratingComponentAdapterDelegatesLifecycleManagement() {
        AbstractBehavior adapter = new FooAbstractBehavior(mockComponentAdapterThatCanManageLifecycle());
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        adapter.stop(pico);
        adapter.dispose(pico);
        Touchable touchable = new SimpleTouchable();
        adapter.start(touchable);
        adapter.stop(touchable);
        adapter.dispose(touchable);
    }

    public void testDecoratingComponentAdapterIgnoresLifecycleManagementIfDelegateDoesNotSupportIt() {
        AbstractBehavior adapter = new FooAbstractBehavior(mockComponentAdapter());
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        adapter.stop(pico);
        adapter.dispose(pico);
        Touchable touchable = new SimpleTouchable();
        adapter.start(touchable);
        adapter.stop(touchable);
        adapter.dispose(touchable);
    }
    
    ComponentMonitor mockMonitorWithNoExpectedMethods() {
        Mock mock = mock(ComponentMonitor.class);
        return (ComponentMonitor)mock.proxy();
    }

    private ComponentAdapter mockComponentAdapterThatDoesSupportStrategy() {
        Mock mock = mock(ComponentAdapterThatSupportsStrategy.class);
        mock.expects(once()).method("changeMonitor").withAnyArguments();
        mock.expects(once()).method("currentMonitor").will(returnValue(mockMonitorWithNoExpectedMethods()));
        return (ComponentAdapter)mock.proxy();
    }

    private ComponentAdapter mockComponentAdapter() {
        Mock mock = mock(ComponentAdapter.class);
        return (ComponentAdapter)mock.proxy();
    }
    
    static interface ComponentAdapterThatSupportsStrategy extends ComponentAdapter, ComponentMonitorStrategy {
    }

    private ComponentAdapter mockComponentAdapterThatCanManageLifecycle() {
        Mock mock = mock(ComponentAdapterThatCanManageLifecycle.class);
        mock.expects(once()).method("start").with(isA(PicoContainer.class));
        mock.expects(once()).method("stop").with(isA(PicoContainer.class));
        mock.expects(once()).method("dispose").with(isA(PicoContainer.class));
        mock.expects(once()).method("start").with(isA(Touchable.class));
        mock.expects(once()).method("stop").with(isA(Touchable.class));
        mock.expects(once()).method("dispose").with(isA(Touchable.class));
        return (ComponentAdapter)mock.proxy();
    }

    static interface ComponentAdapterThatCanManageLifecycle extends ComponentAdapter, Behavior, LifecycleStrategy {
    }

    static class FooAbstractBehavior extends AbstractBehavior {

        public FooAbstractBehavior(ComponentAdapter delegate) {
            super(delegate);
        }

        public String getDescriptor() {
            return null;
        }
    }
}
