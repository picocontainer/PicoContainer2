package org.picocontainer.injectors;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.annotations.Inject;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.injectors.MethodAnnotationInjector;
import org.picocontainer.injectors.SetterInjector;

public class MethodAnnotationInjectorTestCase extends TestCase {

    public static class AnnotatedBurp {

        private Wind wind;

        @Inject
        public void windyWind(Wind wind) {
            this.wind = wind;
        }
    }

    public static class SetterBurp {

        private Wind wind;

        public void setWind(Wind wind) {
            this.wind = wind;
        }
    }

    public static class Wind {
    }

    public void testSetterMethodInjectionToContrastWithThatBelow() {

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new SetterInjector(SetterBurp.class, SetterBurp.class, Parameter.DEFAULT, NullComponentMonitor.getInstance(), NullLifecycleStrategy.getInstance()));
        pico.addComponent(Wind.class, new Wind());
        SetterBurp burp = pico.getComponent(SetterBurp.class);
        assertNotNull(burp);
        assertNotNull(burp.wind);
    }

    public void testNonSetterMethodInjection() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new MethodAnnotationInjector(AnnotatedBurp.class, AnnotatedBurp.class, Parameter.DEFAULT,
                                               NullComponentMonitor.getInstance(), NullLifecycleStrategy.getInstance()) {
            protected String getInjectorPrefix() {
                return "init";
            }
        });
        pico.addComponent(Wind.class, new Wind());
        AnnotatedBurp burp = pico.getComponent(AnnotatedBurp.class);
        assertNotNull(burp);
        assertNotNull(burp.wind);
    }

}
