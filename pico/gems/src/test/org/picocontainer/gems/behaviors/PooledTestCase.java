/*
 * Copyright (C) 2005 J&ouml;rg Schaible
 * Created on 29.08.2005 by J&ouml;rg Schaible
 */
package org.picocontainer.gems.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Behavior;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.gems.behaviors.Pooled;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.tck.AbstractComponentAdapterTestCase;
import org.picocontainer.testmodel.RecordingLifecycle;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.pool.Poolable;


/**
 * @author J&ouml;rg Schaible
 */
public final class PooledTestCase extends AbstractComponentAdapterTestCase {

    public static interface Identifiable {
        int getId();
    }

    public static final class InstanceCounter implements Identifiable, Serializable {
        private static int counter = 0;
        private final int id;

        public InstanceCounter() {
            id = counter++;
        }

        public int getId() {
            return id;
        }

        public boolean equals(Object arg) {
            return arg instanceof Identifiable && id == ((Identifiable)arg).getId();
        }
    }

    protected void setUp() throws Exception {
        InstanceCounter.counter = 0;
    }

    public void testNewIsInstantiatedOnEachRequest() {
        ComponentAdapter componentAdapter = new Pooled(new ConstructorInjector(
                Identifiable.class, InstanceCounter.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false), new Pooled.DefaultContext());

        Object borrowed0 = componentAdapter.getComponentInstance(null);
        Object borrowed1 = componentAdapter.getComponentInstance(null);

        assertNotSame(borrowed0, borrowed1);
    }

    public void testInstancesCanBeRecycled() {
        ComponentAdapter componentAdapter = new Pooled(new ConstructorInjector(
                Identifiable.class, InstanceCounter.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false), new Pooled.DefaultContext());

        Object borrowed0 = componentAdapter.getComponentInstance(null);
        Object borrowed1 = componentAdapter.getComponentInstance(null);
        Object borrowed2 = componentAdapter.getComponentInstance(null);

        assertNotSame(borrowed0, borrowed1);
        assertNotSame(borrowed1, borrowed2);

        //noinspection UnusedAssignment
        borrowed1 = null;
        System.gc();

        Identifiable borrowed = (Identifiable)componentAdapter.getComponentInstance(null);
        assertEquals(1, borrowed.getId());

        ((Poolable)borrowed).returnInstanceToPool();

        Object borrowedReloaded = componentAdapter.getComponentInstance(null);
        assertEquals(borrowed, borrowedReloaded);
    }

    public void testBlocksWhenExhausted() throws InterruptedException {
        final ComponentAdapter componentAdapter = new Pooled(new ConstructorInjector(
                Identifiable.class, InstanceCounter.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false), new Pooled.DefaultContext() {
            public int getMaxSize() {
                return 2;
            }

            public int getMaxWaitInMilliseconds() {
                return 3000;
            }
        });

        final Identifiable[] borrowed = new Identifiable[3];
        final Throwable[] threadException = new Throwable[2];

        final StringBuffer order = new StringBuffer();
        final Thread returner = new Thread() {
            public void run() {
                try {
                    synchronized (this) {
                        notifyAll();
                        wait(200); // ensure, that main thread is blocked
                    }
                    order.append("returner ");
                    ((Poolable)borrowed[0]).returnInstanceToPool();
                } catch (Throwable t) {
                    t.printStackTrace();
                    synchronized (componentAdapter) {
                        componentAdapter.notify();
                    }
                    threadException[1] = t;
                }
            }
        };

        borrowed[0] = (Identifiable)componentAdapter.getComponentInstance(null);
        borrowed[1] = (Identifiable)componentAdapter.getComponentInstance(null);
        synchronized (returner) {
            returner.start();
            returner.wait();
        }

        // should block
        order.append("main ");
        borrowed[2] = (Identifiable)componentAdapter.getComponentInstance(null);
        order.append("main");

        returner.join();

        assertNull(threadException[0]);
        assertNull(threadException[1]);

        assertEquals("main returner main", order.toString());

        assertEquals(borrowed[0].getId(), borrowed[2].getId());
        assertFalse(borrowed[1].getId() == borrowed[2].getId());
    }

    public void testTimeoutWhenExhausted() {
        final ComponentAdapter componentAdapter = new Pooled(new ConstructorInjector(
                Identifiable.class, InstanceCounter.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false), new Pooled.DefaultContext() {
            public int getMaxSize() {
                return 2;
            }

            public int getMaxWaitInMilliseconds() {
                return 250;
            }
        });

        Identifiable borrowed0 = (Identifiable)componentAdapter.getComponentInstance(null);
        Identifiable borrowed1 = (Identifiable)componentAdapter.getComponentInstance(null);
        assertNotNull(borrowed0);
        assertFalse(borrowed0.getId() == borrowed1.getId());
        long time = System.currentTimeMillis();
        try {
            componentAdapter.getComponentInstance(null);
            fail("Thrown " + Pooled.PoolException.class.getName() + " expected");
        } catch (final Pooled.PoolException e) {
            assertTrue(e.getMessage().indexOf("Time out") >= 0);
            assertTrue(System.currentTimeMillis() - time >= 250);
        }
    }

    public void testGrowsAlways() {
        Pooled behavior = new Pooled(
                new ConstructorInjector("foo", Object.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false),
                new Pooled.DefaultContext() {

                    public ProxyFactory getProxyFactory() {
                        return new CglibProxyFactory();
                    }
                });

        final Set<Object> set = new HashSet<Object>();
        try {
            final int max = 5;
            int i;
            for (i = 0; i < max; ++i) {
                assertEquals(i, behavior.size());
                final Object object = behavior.getComponentInstance(null);
                set.add(object);
            }
            assertEquals(i, behavior.size());
            assertEquals(i, set.size());

            for (Object aSet : set) {
                Poolable object = (Poolable)aSet;
                object.returnInstanceToPool();
                assertEquals(max, behavior.size());
            }

            for (i = 0; i < max; ++i) {
                assertEquals(max, behavior.size());
                final Object object = behavior.getComponentInstance(null);
                assertNotNull(object);
                set.add(object);
            }

            assertEquals(max, set.size());

        } catch (Pooled.PoolException e) {
            fail("This pool should not get exhausted.");
        }
    }

    public void testFailsWhenExhausted() {
        final Pooled behavior = new Pooled(
                new ConstructorInjector(Identifiable.class, InstanceCounter.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false),
                new Pooled.DefaultContext() {
                    public int getMaxSize() {
                        return 2;
                    }
                });

        assertEquals(0, behavior.size());
        Identifiable borrowed0 = (Identifiable)behavior.getComponentInstance(null);
        assertEquals(1, behavior.size());
        Identifiable borrowed1 = (Identifiable)behavior.getComponentInstance(null);
        assertEquals(2, behavior.size());
        try {
            behavior.getComponentInstance(null);
            fail("Expected ExhaustedException, pool shouldn't be able to grow further.");
        } catch (Pooled.PoolException e) {
            assertTrue(e.getMessage().indexOf("exhausted") >= 0);
        }

        assertFalse(borrowed0.getId() == borrowed1.getId());
    }

    public void testInternalGCCall() {
        ComponentAdapter componentAdapter = new Pooled(new ConstructorInjector(
                Identifiable.class, InstanceCounter.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false), new Pooled.DefaultContext() {
            public int getMaxSize() {
                return 1;
            }

            public boolean autostartGC() {
                return true;
            }
        });

        for (int i = 0; i < 5; i++) {
            final Identifiable borrowed = (Identifiable)componentAdapter.getComponentInstance(null);
            assertNotNull(borrowed);
            assertEquals(0, borrowed.getId());
        }
    }

    /**
     * Prepare the test <em>lifecycleManagerSupport</em>. Prepare the delivered PicoContainer with an adapter, that
     * has a lifecycle and use a StringBuffer registered in the container to record the lifecycle method invocations.
     * The recorded String in the buffer must result in <strong>&qout;&lt;OneOne&gt;!One&qout;</strong>. The addAdapter
     * top test should be registered in the container and delivered as return value.
     * @param picoContainer the container
     * @return the adapter to test
     */
    private ComponentAdapter prepDEF_lifecycleManagerSupport(MutablePicoContainer picoContainer) {
        picoContainer.addComponent(RecordingLifecycle.One.class);
        Pooled poolingBehavior = new Pooled(new ConstructorInjector(
                RecordingLifecycle.Recorder.class, RecordingLifecycle.Two.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false), new Pooled.DefaultContext());
        return picoContainer.addAdapter(poolingBehavior).getComponentAdapter(RecordingLifecycle.One.class, null);
    }

    public void testDEF_lifecycleManagerSupport() {
        if ((getComponentAdapterNature() & RESOLVING) > 0) {
            final Class type = getComponentAdapterType();
            if (Behavior.class.isAssignableFrom(type)) {
                final StringBuffer buffer = new StringBuffer();
                final MutablePicoContainer picoContainer = new DefaultPicoContainer(
                        createDefaultComponentFactory());
                picoContainer.addComponent(buffer);
                final ComponentAdapter componentAdapter = prepDEF_lifecycleManagerSupport(picoContainer);
                assertSame(getComponentAdapterType(), componentAdapter.getClass());
                assertEquals(0, buffer.length());
                picoContainer.start();
                picoContainer.stop();
                picoContainer.dispose();
                // @todo Move test to AbstractAbstractCATC
                assertEquals("<OneOne>!One", buffer.toString());
            }
        }
    }

    /**
     * Prepare the test <em>lifecycleManagerHonorsInstantiationSequence</em>. Prepare the delivered PicoContainer
     * with addAdapter(s), that have dependend components, have a lifecycle and use a StringBuffer registered in the
     * container to record the lifecycle method invocations. The recorded String in the buffer must result in
     * <strong>&qout;&lt;One&lt;TwoTwo&gt;One&gt;!Two!One&qout;</strong>. The adapter top test should be registered in
     * the container and delivered as return value.
     * @param picoContainer the container
     * @return the adapter to test
     */
    private ComponentAdapter prepRES_lifecycleManagerHonorsInstantiationSequence(MutablePicoContainer picoContainer) {
        picoContainer.addComponent(RecordingLifecycle.One.class);
        Pooled poolingBehavior = new Pooled(new ConstructorInjector(
                RecordingLifecycle.Recorder.class, RecordingLifecycle.Two.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false), new Pooled.DefaultContext());
        return picoContainer.addAdapter(poolingBehavior).getComponentAdapter(RecordingLifecycle.Two.class, null);
    }

    public void testRES_lifecycleManagerHonorsInstantiationSequence() {
        if ((getComponentAdapterNature() & RESOLVING) > 0) {
            final Class type = getComponentAdapterType();
            if (Behavior.class.isAssignableFrom(type)) {
                final StringBuffer buffer = new StringBuffer();
                final MutablePicoContainer picoContainer = new DefaultPicoContainer(
                        createDefaultComponentFactory());
                picoContainer.addComponent(buffer);
                final ComponentAdapter componentAdapter = prepRES_lifecycleManagerHonorsInstantiationSequence(picoContainer);
                assertSame(getComponentAdapterType(), componentAdapter.getClass());
                assertEquals(0, buffer.length());
                picoContainer.start();
                picoContainer.stop();
                picoContainer.dispose();
                // @todo Move test to AbstractAbstractCATC
                assertEquals("<One<TwoTwo>One>!Two!One", buffer.toString());
            }
        }
    }

    // -------- TCK -----------

    protected Class getComponentAdapterType() {
        return Pooled.class;
    }

    protected int getComponentAdapterNature() {
        return super.getComponentAdapterNature() & ~(INSTANTIATING | RESOLVING | VERIFYING);
    }

    private ComponentAdapter createPoolOfTouchables() {
        return new Pooled(new ConstructorInjector(
                Touchable.class, SimpleTouchable.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false), new Pooled.DefaultContext());
    }

    protected ComponentAdapter prepDEF_verifyWithoutDependencyWorks(MutablePicoContainer picoContainer) {
        return createPoolOfTouchables();
    }

    protected ComponentAdapter prepDEF_verifyDoesNotInstantiate(MutablePicoContainer picoContainer) {
        return createPoolOfTouchables();
    }

    protected ComponentAdapter prepDEF_visitable() {
        return createPoolOfTouchables();
    }

    private ComponentAdapter createSerializable() {
        return new Pooled(new ConstructorInjector(
                Identifiable.class, InstanceCounter.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false), new Pooled.DefaultContext());
    }

    protected ComponentAdapter prepSER_isSerializable(MutablePicoContainer picoContainer) {
        return createSerializable();
    }

    protected ComponentAdapter prepSER_isXStreamSerializable(MutablePicoContainer picoContainer) {
        return createSerializable();
    }

}
