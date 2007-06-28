/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.behaviors;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.behaviors.CachingBehavior;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;


/**
 * @author Mauro Talevi
 * @version $Revision: $
 */
public class CachingBehaviorTestCase extends MockObjectTestCase {

    public void testComponentIsNotStartedWhenCachedAndCanBeStarted() {
        CachingBehavior adapter = new CachingBehavior(
                mockComponentAdapterSupportingLifecycleStrategy(true, false, false));
        PicoContainer pico = new DefaultPicoContainer();
        adapter.getComponentInstance(pico);
        adapter.start(pico);
    }

    public void testComponentCanBeStartedAgainAfterBeingStopped() {
        CachingBehavior adapter = new CachingBehavior(
                mockComponentAdapterSupportingLifecycleStrategy(true, true, false));
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        Object instanceAfterFirstStart = adapter.getComponentInstance(pico);
        adapter.stop(pico);
        adapter.start(pico);
        Object instanceAfterSecondStart = adapter.getComponentInstance(pico);
        assertSame(instanceAfterFirstStart, instanceAfterSecondStart);
    }

    public void testComponentCannotBeStartedIfDisposed() {
        CachingBehavior adapter = new CachingBehavior(
                mockComponentAdapterSupportingLifecycleStrategy(false, false, true));
        PicoContainer pico = new DefaultPicoContainer();
        adapter.dispose(pico);
        try {
            adapter.start(pico);
            fail("IllegalStateException expected");
        } catch (Exception e) {
            assertEquals("Already disposed", e.getMessage());
        }
    }

    public void testComponentCannotBeStartedIfAlreadyStarted() {
        CachingBehavior adapter = new CachingBehavior(
                mockComponentAdapterSupportingLifecycleStrategy(true, false, false));
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        try {
            adapter.start(pico);
            fail("IllegalStateException expected");
        } catch (Exception e) {
            assertEquals("Already started", e.getMessage());
        }
    }

    public void testComponentCannotBeStoppeddIfDisposed() {
        CachingBehavior adapter = new CachingBehavior(
                mockComponentAdapterSupportingLifecycleStrategy(false, false, true));
        PicoContainer pico = new DefaultPicoContainer();
        adapter.dispose(pico);
        try {
            adapter.stop(pico);
            fail("IllegalStateException expected");
        } catch (Exception e) {
            assertEquals("Already disposed", e.getMessage());
        }
    }

    public void testComponentCannotBeStoppedIfNotStarted() {
        CachingBehavior adapter = new CachingBehavior(
                mockComponentAdapterSupportingLifecycleStrategy(true, true, false));
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        adapter.stop(pico);
        try {
        adapter.stop(pico);
            fail("IllegalStateException expected");
        } catch (Exception e) {
            assertEquals("Not started", e.getMessage());
        }
    }

    public void testComponentCannotBeDisposedIfAlreadyDisposed() {
        CachingBehavior adapter = new CachingBehavior(
                mockComponentAdapterSupportingLifecycleStrategy(true, true, true));
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        adapter.stop(pico);
        adapter.dispose(pico);
        try {
            adapter.dispose(pico);
            fail("IllegalStateException expected");
        } catch (Exception e) {
            assertEquals("Already disposed", e.getMessage());
        }
    }

    public void testComponentIsStoppedAndDisposedIfStartedWhenFlushed() {
        CachingBehavior adapter = new CachingBehavior(
                mockComponentAdapterSupportingLifecycleStrategy(true, true, true));
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        adapter.flush();
    }

    public void testComponentIsNotStoppedAndDisposedWhenFlushedIfNotStarted() {
        CachingBehavior adapter = new CachingBehavior(
                mockComponentAdapterSupportingLifecycleStrategy(false, false, false));
        adapter.flush();
    }

    public void testComponentIsNotStoppedAndDisposedWhenFlushedIfDelegateDoesNotSupportLifecycle() {
        CachingBehavior adapter = new CachingBehavior(
                mockComponentAdapterNotSupportingLifecycleStrategy());
        adapter.flush();
    }

    public void testLifecycleIsIgnoredIfDelegateDoesNotSupportIt() {
        CachingBehavior adapter = new CachingBehavior(
                mockComponentAdapterNotSupportingLifecycleStrategy());
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        adapter.stop(pico);
        adapter.dispose(pico);
    }

    public void testCanStopAComponentThatWasNeverStartedBecauseItHasNoLifecycle() {
        MutablePicoContainer pico = new DefaultPicoContainer();

        pico.addComponent(StringBuffer.class);

        pico.start();

        assertNotNull(pico.getComponent(StringBuffer.class));

        pico.stop();
        pico.dispose();
    }

    private ComponentAdapter mockComponentAdapterNotSupportingLifecycleStrategy() {
        Mock mock = mock(ComponentAdapter.class);
        return (ComponentAdapter)mock.proxy();
    }

    private ComponentAdapter mockComponentAdapterSupportingLifecycleStrategy(
            boolean start, boolean stop, boolean dispose) {
        boolean hasLifecycle = start || stop || dispose;
        Mock mock = mock(ComponentAdapterSupportingLifecycleStrategy.class);
        if (start) {
            mock.expects(atLeastOnce()).method("start").with(isA(Touchable.class));
        }
        if (stop) {
            mock.expects(once()).method("stop").with(isA(Touchable.class));
        }
        if (dispose) {
            mock.expects(once()).method("dispose").with(isA(Touchable.class));
        }
        if (hasLifecycle) {
            mock.stubs().method("getComponentInstance").with(isA(PicoContainer.class)).will(
                    returnValue(new SimpleTouchable()));
        }
        mock.expects(once()).method("getComponentImplementation").will(
                returnValue(SimpleTouchable.class));
        mock.expects(once()).method("hasLifecycle").with(same(SimpleTouchable.class)).will(
                returnValue(hasLifecycle));
        return (ComponentAdapter)mock.proxy();
    }

    static interface ComponentAdapterSupportingLifecycleStrategy extends ComponentAdapter,
            LifecycleStrategy {
    }
}