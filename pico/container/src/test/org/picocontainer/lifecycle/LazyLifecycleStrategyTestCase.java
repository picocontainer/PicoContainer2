package org.picocontainer.lifecycle;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.picocontainer.*;
import static org.picocontainer.Characteristics.CACHE;

public class LazyLifecycleStrategyTestCase {

    @Test
    public void testStartStopAndDisposeCanBeLazy() {
        final StringBuilder sb = new StringBuilder();
        MutablePicoContainer pico = new LazyStartingPicoContainer();
        pico.addComponent(sb);
        pico.as(CACHE).addComponent(MyStartableComp.class);
        pico.start();
        assertEquals("", sb.toString()); // normally would be "<" here
        pico.getComponent(MyStartableComp.class);
        pico.getComponent(MyStartableComp.class);
        assertEquals("<", sb.toString()); // only one start() issued even if two or more getComponents
        pico.stop();
        assertEquals("<>", sb.toString());
        pico.dispose();
        assertEquals("<>!", sb.toString());
    }

    @Test
    public void testStartStopAndDisposeCanBeConditionallyLazy() {
        final StringBuilder sb = new StringBuilder();
        MutablePicoContainer pico = new ConditionallyLazyStartingPicoContainer();
        pico.addComponent(sb);
        pico.as(CACHE).addComponent(MyStartableComp.class);
        pico.as(CACHE).addComponent(MyDifferentStartableComp.class);
        pico.start();
        assertEquals("{", sb.toString()); // one component started, one not
        pico.getComponent(MyStartableComp.class);
        pico.getComponent(MyStartableComp.class);
        assertEquals("{<", sb.toString()); // both components now started, one lazily.
        pico.stop();
        assertEquals("{<}>", sb.toString());
        pico.dispose();
        assertEquals("{<}>?!", sb.toString());
    }

    public static class MyStartableComp implements Startable, Disposable {
        private StringBuilder sb;

        public MyStartableComp(StringBuilder sb) {
            this.sb = sb;
        }

        public void start() {
            sb.append("<");
        }

        public void stop() {
            sb.append(">");
        }

        public void dispose() {
            sb.append("!");
        }
    }

    public static class MyDifferentStartableComp implements Startable, Disposable {
        private StringBuilder sb;

        public MyDifferentStartableComp(StringBuilder sb) {
            this.sb = sb;
        }

        public void start() {
            sb.append("{");
        }

        public void stop() {
            sb.append("}");
        }

        public void dispose() {
            sb.append("?");
        }
    }


    private static class ConditionallyLazyStartingPicoContainer extends DefaultPicoContainer {
        @Override
        public void potentiallyStartAdapter(ComponentAdapter<?> adapter) {
            if (adapter.getComponentImplementation() == MyDifferentStartableComp.class) {
                super.potentiallyStartAdapter(adapter);
            }
        }

        @Override
        protected void instantiateComponentAsIsStartable(ComponentAdapter<?> adapter) {
            if (adapter.getComponentImplementation() == MyDifferentStartableComp.class) {
                super.instantiateComponentAsIsStartable(adapter);
            }
        }

        @Override
        protected Object decorateComponent(Object component, ComponentAdapter<?> componentAdapter) {
            if (componentAdapter instanceof ComponentLifecycle<?>
                && !((ComponentLifecycle<?>) componentAdapter).isStarted()) {
                super.potentiallyStartAdapter(componentAdapter);
            }
            return component;
        }
    }


    private static class LazyStartingPicoContainer extends DefaultPicoContainer {
        @Override
        public void potentiallyStartAdapter(ComponentAdapter<?> adapter) {
            // veto
        }

        @Override
        protected void instantiateComponentAsIsStartable(ComponentAdapter<?> adapter) {
            // veto
        }

        @Override
        protected Object decorateComponent(Object component, ComponentAdapter<?> componentAdapter) {
            if (componentAdapter instanceof ComponentLifecycle<?>
                && !((ComponentLifecycle<?>) componentAdapter).isStarted()) {
                super.potentiallyStartAdapter(componentAdapter);
            }
            return component;
        }
    }
}
