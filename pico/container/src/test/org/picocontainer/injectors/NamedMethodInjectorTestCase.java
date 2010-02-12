package org.picocontainer.injectors;

import org.junit.Test;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.monitors.NullComponentMonitor;

import static com.sun.tools.internal.ws.wsdl.parser.Util.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NamedMethodInjectorTestCase {

    public static class Windmill {
        private String wind;
        public void setWind(String eeeeee) { // it is important to note here that 'eeeee' is not going to match any named comp
            this.wind = eeeeee;
        }
    }

    @Test
    public void shouldMatchBasedOnMethodNameIfComponentAvailable() {
        final String expected = "use this one pico, its key matched the method name (ish)";
        NamedMethodInjector nmi = new NamedMethodInjector(Windmill.class, Windmill.class, Parameter.DEFAULT,
                new NullComponentMonitor());
        Windmill windmill = new DefaultPicoContainer()
                .addAdapter(nmi)
                .addConfig("attemptToConfusePicoContainer", "ha ha, confused you")
                .addConfig("wind", expected) // matches setWind(..)
                .addConfig("woo look here another string", "yup, really fooled you this time")
                .getComponent(Windmill.class);
        assertNotNull(windmill);
        assertNotNull(windmill.wind);
        assertEquals(expected, windmill.wind);
    }
    
    @Test
    public void shouldBeAmbigiousMultipleComponentAvailableOfRightTypeWithoutMatchingName() {
        NamedMethodInjector nmi = new NamedMethodInjector(Windmill.class, Windmill.class, Parameter.DEFAULT,
                new NullComponentMonitor());
        try {
            new DefaultPicoContainer()
                    .addAdapter(nmi)
                    .addConfig("attemptToConfusePicoContainer", "ha ha, confused you")
                    .addConfig("woo look here another", "yup, really fooled you this time")
                    .getComponent(Windmill.class);
            fail("should have barfed");
        } catch (AbstractInjector.AmbiguousComponentResolutionException e) {
            // expected
        }
    }

    @Test
    public void shouldBeUnsatisfiedIfNoComponentAvailableOfTheRightType() {
        NamedMethodInjector nmi = new NamedMethodInjector(Windmill.class, Windmill.class, Parameter.DEFAULT,
                new NullComponentMonitor());
        try {
            new DefaultPicoContainer()
                    .addAdapter(nmi)
                    .addConfig("attemptToConfusePicoContainer", 123)
                    .addConfig("woo look here another", 456)
                    .getComponent(Windmill.class);
            fail("should have barfed");
        } catch (AbstractInjector.UnsatisfiableDependenciesException e) {
            // expected
        }
    }

    @Test
    public void withoutNameMatchWillBeOKTooIfOnlyOneOfRightType() {
        NamedMethodInjector nmi = new NamedMethodInjector(Windmill.class, Windmill.class, Parameter.DEFAULT,
                new NullComponentMonitor());
        Windmill windmill = new DefaultPicoContainer()
                .addAdapter(nmi)
                .addConfig("anything", "hello")
                .getComponent(Windmill.class);
        assertNotNull(windmill);
        assertNotNull(windmill.wind);
        assertEquals("hello", windmill.wind);
    }

}
