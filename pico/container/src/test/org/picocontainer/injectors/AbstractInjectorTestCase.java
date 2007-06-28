package org.picocontainer.injectors;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.Parameter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.containers.EmptyPicoContainer;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;

import junit.framework.TestCase;

public class AbstractInjectorTestCase extends TestCase {

    private AbstractInjector ai;
    Constructor<HashMap> ctor;

    public void setUp() throws NoSuchMethodException {
        ai = new MyAbstractInjector(Map.class, HashMap.class, new Parameter[0],
                                                     NullComponentMonitor.getInstance(),
                                                     NullLifecycleStrategy.getInstance());
        ctor = HashMap.class.getConstructor();
    }

    public void testCaughtIllegalAccessExceptionInvokesMonitorAndThrows() {
        final EmptyPicoContainer epc = new EmptyPicoContainer();
        final IllegalAccessException iae = new IllegalAccessException("foo");
        NullComponentMonitor ncm = new NullComponentMonitor() {
            public void instantiationFailed(PicoContainer container,
                                            ComponentAdapter componentAdapter,
                                            Constructor constructor,
                                            Exception e) {
                assertSame(epc, container);
                assertSame(ai, componentAdapter);
                assertSame(ctor, constructor);
                assertSame(iae, e);
            }
        };
        try {
            ai.caughtIllegalAccessException(ncm, ctor, iae, epc);
        } catch (PicoCompositionException e) {
            assertSame(iae, e.getCause());
        }
    }

    public void testCaughtInstantiationExceptionInvokesMonitorAndThrows() {
        final EmptyPicoContainer epc = new EmptyPicoContainer();
        final InstantiationException ie = new InstantiationException("foo");
        NullComponentMonitor ncm = new NullComponentMonitor() {
            public void instantiationFailed(PicoContainer container,
                                            ComponentAdapter componentAdapter,
                                            Constructor constructor,
                                            Exception e) {
                assertSame(epc, container);
                assertSame(ai, componentAdapter);
                assertSame(ctor, constructor);
                assertSame(ie, e);
            }
        };
        try {
            ai.caughtInstantiationException(ncm, ctor, ie, epc);
        } catch (PicoCompositionException e) {
            assertSame("Should never get here", e.getMessage());
        }
    }

    public void testCaughtInvocationTargetExceptionInvokesMonitorAndReThrowsRuntimeIfRuntimeInTheFirstPlace() {
        final InvocationTargetException ite = new InvocationTargetException(new RuntimeException("foo"));
        NullComponentMonitor ncm = new NullComponentMonitor() {
            public void invocationFailed(Member member, Object instance, Exception e) {
                assertSame(ctor, member);
                assertSame("bar", instance);
                assertSame(ite, e);
            }
        };
        try {
            ai.caughtInvocationTargetException(ncm, ctor, "bar", ite);
        } catch (RuntimeException e) {
            assertSame("foo", e.getMessage());
        }
    }

    public void testCaughtInvocationTargetExceptionInvokesMonitorAndReThrowsErrorIfErrorInTheFirstPlace() {
        final InvocationTargetException ite = new InvocationTargetException(new Error("foo"));
        NullComponentMonitor ncm = new NullComponentMonitor() {
            public void invocationFailed(Member member, Object instance, Exception e) {
                assertSame(ctor, member);
                assertSame("bar", instance);
                assertSame(ite, e);
            }
        };
        try {
            ai.caughtInvocationTargetException(ncm, ctor, "bar", ite);
        } catch (Error e) {
            assertSame("foo", e.getMessage());
        }
    }

    public void testCaughtInvocationTargetExceptionInvokesMonitorAndReThrowsAsCompositionIfNotRuntimeOrError() {
        final InvocationTargetException ite = new InvocationTargetException(new Exception("foo"));
        NullComponentMonitor ncm = new NullComponentMonitor() {
            public void invocationFailed(Member member, Object instance, Exception e) {
                assertSame(ctor, member);
                assertSame("bar", instance);
                assertSame(ite, e);
            }
        };
        try {
            ai.caughtInvocationTargetException(ncm, ctor, "bar", ite);
        } catch (PicoCompositionException e) {
            assertSame("foo", e.getCause().getMessage());
        }
    }



    private static class MyAbstractInjector extends AbstractInjector {

        public MyAbstractInjector(Object componentKey,
                                  Class componentImplementation,
                                  Parameter[] parameters,
                                  ComponentMonitor monitor,
                                  LifecycleStrategy lifecycleStrategy) {
            super(componentKey, componentImplementation, parameters, monitor, lifecycleStrategy);
        }

        public void verify(PicoContainer container) throws PicoCompositionException {
                }

        public Object getComponentInstance(PicoContainer container) throws PicoCompositionException {
            return null;
        }
    }
}
