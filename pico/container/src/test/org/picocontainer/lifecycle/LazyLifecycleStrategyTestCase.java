package org.picocontainer.lifecycle;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.picocontainer.*;
import static org.picocontainer.Characteristics.CACHE;

public class LazyLifecycleStrategyTestCase {

    @Test
    public void testStartStopAndDisposeCanBeLazy() {

        final StringBuilder sb = new StringBuilder();

        MutablePicoContainer pico = new DefaultPicoContainer() {
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
        };

        pico.addComponent(sb);
        pico.as(CACHE).addComponent(MyStartableComp.class);
        pico.start();
        assertEquals("", sb.toString()); // normally would be "<" here
        Object bar = pico.getComponent(MyStartableComp.class);
        assertEquals("<", sb.toString());
        Object baz = pico.getComponent(MyStartableComp.class);
        assertEquals("<", sb.toString());
        pico.stop();
        assertEquals("<>", sb.toString());
        pico.dispose();
        assertEquals("<>!", sb.toString());
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


}
