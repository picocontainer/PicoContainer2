/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.behaviors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;



/**
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public class CachedTestCase {

	private Mockery mockery = mockeryWithCountingNamingScheme();
	
    @Test public void testComponentIsNotStartedWhenCachedAndCanBeStarted() {
        Cached adapter = new Cached(
                mockComponentAdapterSupportingLifecycleStrategy(true, false, false));
        PicoContainer pico = new DefaultPicoContainer();
        adapter.getComponentInstance(pico);
        adapter.start(pico);
    }

    @Test public void testComponentCanBeStartedAgainAfterBeingStopped() {
        Cached adapter = new Cached(
                mockComponentAdapterSupportingLifecycleStrategy(true, true, false));
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        Object instanceAfterFirstStart = adapter.getComponentInstance(pico);
        adapter.stop(pico);
        adapter.start(pico);
        Object instanceAfterSecondStart = adapter.getComponentInstance(pico);
        assertSame(instanceAfterFirstStart, instanceAfterSecondStart);
    }

    @Test public void testComponentCannotBeStartedIfDisposed() {
        Cached adapter = new Cached(
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

    @Test public void testComponentCannotBeStartedIfAlreadyStarted() {
        Cached adapter = new Cached(
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

    @Test public void testComponentCannotBeStoppeddIfDisposed() {
        Cached adapter = new Cached(
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

    @Test public void testComponentCannotBeStoppedIfNotStarted() {
        Cached adapter = new Cached(
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

    @Test public void testComponentCannotBeDisposedIfAlreadyDisposed() {
        Cached adapter = new Cached(
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

    @Test public void testComponentIsStoppedAndDisposedIfStartedWhenFlushed() {
        Cached adapter = new Cached(
                mockComponentAdapterSupportingLifecycleStrategy(true, true, true));
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        adapter.flush();
    }

    @Test public void testComponentIsNotStoppedAndDisposedWhenFlushedIfNotStarted() {
        Cached adapter = new Cached(
                mockComponentAdapterSupportingLifecycleStrategy(false, false, false));
        adapter.flush();
    }

    @Test public void testComponentIsNotStoppedAndDisposedWhenFlushedIfDelegateDoesNotSupportLifecycle() {
        Cached adapter = new Cached(
                mockComponentAdapterNotSupportingLifecycleStrategy());
        adapter.flush();
    }

    @Test public void testLifecycleIsIgnoredIfDelegateDoesNotSupportIt() {
        Cached adapter = new Cached(
                mockComponentAdapterNotSupportingLifecycleStrategy());
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        adapter.stop(pico);
        adapter.dispose(pico);
    }

    @Test public void testCanStopAComponentThatWasNeverStartedBecauseItHasNoLifecycle() {
        MutablePicoContainer pico = new DefaultPicoContainer();

        pico.addComponent(StringBuffer.class);

        pico.start();

        assertNotNull(pico.getComponent(StringBuffer.class));

        pico.stop();
        pico.dispose();
    }

    private ComponentAdapter mockComponentAdapterNotSupportingLifecycleStrategy() {
        return mockery.mock(ComponentAdapter.class);
    }

    private ComponentAdapter mockComponentAdapterSupportingLifecycleStrategy(
            final boolean start, final boolean stop, final boolean dispose) {
        final boolean hasLifecycle = start || stop || dispose;
        final ComponentAdapterSupportingLifecycleStrategy ca = mockery.mock(ComponentAdapterSupportingLifecycleStrategy.class);
        mockery.checking(new Expectations(){{
            if (start) {
                atLeast(1).of(ca).start(with(any(Touchable.class)));
            }
            if (stop) {
                one(ca).stop(with(any(Touchable.class)));
            }
            if (dispose) {
                one(ca).dispose(with(any(Touchable.class)));
            }
            if (hasLifecycle) {
            	one(ca).getComponentInstance(with(any(PicoContainer.class)), with(same(ComponentAdapter.NOTHING.class)));
            	will(returnValue(new SimpleTouchable()));
            }
            one(ca).getComponentImplementation();
            will(returnValue(SimpleTouchable.class));
            one(ca).hasLifecycle(with(same(SimpleTouchable.class)));
            will(returnValue(hasLifecycle));
        }});
        return ca;
    }

    public static interface ComponentAdapterSupportingLifecycleStrategy extends ComponentAdapter,
            LifecycleStrategy {
    }
}