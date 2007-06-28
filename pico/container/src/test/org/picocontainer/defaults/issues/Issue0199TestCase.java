package org.picocontainer.defaults.issues;

import junit.framework.TestCase;

import java.util.List;
import java.util.ArrayList;

import org.picocontainer.PicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.injectors.ConstructorInjectionFactory;
import org.picocontainer.behaviors.SynchronizedBehaviorFactory;

public final class Issue0199TestCase extends TestCase {

    public static class A {
        public A(C c) {}
    }

    public static class B {
        public B(C c) {}
    }

    public static class C {}

    final class Runner extends Thread {
        private final PicoContainer container;
        private final Object componentKey;
        private Throwable throwable;
        private boolean finished;

        Runner(String name, PicoContainer container, Object componentKey) {
            super(name);
            this.container = container;
            this.componentKey = componentKey;
        }

        public void run() {
            try {
                report("Started instantiating " + componentKey.toString());
                container.getComponent(componentKey);
                report("Finished instantiating " + componentKey.toString());
                finished = true;
            } catch (Throwable t) {
                this.throwable = t;
            }
        }

        private void report(String messsage) {
            System.out.println(getName() + ": " + messsage);
        }

        public boolean isFinished() {
            return finished;
        }

        public Throwable getThrowable() {
            return throwable;
        }
    }

    public void testPicoContainerCausesDeadlock() throws InterruptedException {
        DefaultPicoContainer container = createContainer();
        container.addComponent("A", A.class);
        container.addComponent("B", B.class);
        container.addComponent("C", C.class);

        final int THREAD_COUNT = 2;
        List runnerList = new ArrayList(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; ++i) {
            Runner runner = new Runner("Runner " + i, container, (i % 2 == 0) ? "A" : "B");
            runnerList.add(runner);
            runner.start();
        }

        final long WAIT_TIME = 1000;

        for (int i = 0; i < THREAD_COUNT; ++i) {
            Runner runner = (Runner) runnerList.get(i);
            runner.join(WAIT_TIME);
            assertTrue("Deadlock occurred", runner.isFinished());
        }
    }

    private DefaultPicoContainer createContainer() {
        return new DefaultPicoContainer(
                new SynchronizedBehaviorFactory().forThis(new ConstructorInjectionFactory()));
    }
}
