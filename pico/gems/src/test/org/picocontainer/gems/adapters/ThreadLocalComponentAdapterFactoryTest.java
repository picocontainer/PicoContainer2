/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.picocontainer.gems.adapters;

import junit.framework.TestCase;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentFactory;
import org.picocontainer.injectors.ConstructorInjectionFactory;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

import java.util.ArrayList;
import java.util.List;


/**
 * Test ThreadLocalComponentAdapterFactory.
 * 
 * @author J&ouml;rg Schaible
 */
public class ThreadLocalComponentAdapterFactoryTest extends TestCase {

    /**
     * Test creation of a CA ensuring ThreadLocal-behaviour.
     * 
     * @throws InterruptedException
     */
    public final void testCreateComponentAdapterEnsuringThreadLocal() throws InterruptedException {
        final ComponentFactory componentFactory = new ThreadLocalComponentAdapterFactory().forThis(new ConstructorInjectionFactory());
        final ComponentAdapter componentAdapter = componentFactory.createComponentAdapter(
                new NullComponentMonitor(), new NullLifecycleStrategy(), null, List.class, ArrayList.class);
        final List list = (List)componentAdapter.getComponentInstance(null);
        list.add(this);
        final List list2 = new ArrayList();
        final Thread thread = new Thread(new Runnable() {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run() {
                list2.addAll(list);
                list2.add(Thread.currentThread());
            }
        }, "junit");
        thread.start();
        thread.join();
        assertEquals(1, list2.size());
        assertSame(thread, list2.get(0));
    }

    /**
     * Test creation of a CA failing ThreadLocal-behaviour.
     * 
     * @throws InterruptedException
     */
    public final void testCreateComponentAdapterFailingThreadLocal() throws InterruptedException {
        final ComponentFactory componentFactory = new ThreadLocalComponentAdapterFactory(ThreadLocalComponentAdapterFactory.THREAD_ENSURES_LOCALITY).forThis(new ConstructorInjectionFactory());
        final ComponentAdapter componentAdapter = componentFactory.createComponentAdapter(
                new NullComponentMonitor(), new NullLifecycleStrategy(), null, List.class, ArrayList.class);
        final List list = (List)componentAdapter.getComponentInstance(null);
        list.add(this);
        final List list2 = new ArrayList();
        final Thread thread = new Thread(new Runnable() {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run() {
                list2.addAll(list);
                list2.add(Thread.currentThread());
            }
        }, "junit");
        thread.start();
        thread.join();
        assertEquals(2, list2.size());
        assertSame(this, list2.get(0));
        assertSame(thread, list2.get(1));
    }

    /**
     * Test creation of a CA with ThreadLocal-behaviour works if the thread ensures creation.
     * 
     * @throws InterruptedException
     */
    public final void testCreateComponentAdapterWorksForDifferentThreads() throws InterruptedException {
        final ComponentFactory componentFactory = new ThreadLocalComponentAdapterFactory(ThreadLocalComponentAdapterFactory.THREAD_ENSURES_LOCALITY).forThis(new ConstructorInjectionFactory());
        final ComponentAdapter componentAdapter = componentFactory.createComponentAdapter(
                new NullComponentMonitor(), new NullLifecycleStrategy(), null, List.class, ArrayList.class);
        final List list = (List)componentAdapter.getComponentInstance(null);
        list.add(this);
        final List list2 = new ArrayList();
        final Thread thread = new Thread(new Runnable() {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run() {
                final List newList = (List)componentAdapter.getComponentInstance(null);
                list2.addAll(newList);
                final Thread junitThread = Thread.currentThread();
                list2.add(junitThread);
                if (newList.size() == 0) {
                    synchronized (junitThread) {
                        junitThread.notify();
                        try {
                            junitThread.wait();
                        } catch (InterruptedException e) {
                            // Ignore
                        }
                    }
                    newList.add(list2);
                    run();
                }
            }
        }, "junit");
        synchronized (thread) {
            thread.start();
            thread.wait();
        }
        assertEquals(1, list2.size());
        assertSame(thread, list2.get(0));
        synchronized (thread) {
            thread.notify();
        }
        thread.join();
        assertEquals(3, list2.size());
        assertSame(thread, list2.get(0));
        assertSame(list2, list2.get(1));
        assertSame(thread, list2.get(2));
    }
}
