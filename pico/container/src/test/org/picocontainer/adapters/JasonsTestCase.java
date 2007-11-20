package org.picocontainer.adapters;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.annotations.Inject;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.injectors.AnnotatedFieldInjection;
import org.picocontainer.parameters.ComponentParameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import junit.framework.TestCase;

/** @author Paul Hammant */
public class JasonsTestCase extends TestCase {

    public void testNotQuiteJasonsNeed() {
        MutablePicoContainer mpc = new DefaultPicoContainer(new AnnotatedFieldInjection());
        // this level of verbosity is one way, not the way ....
        mpc.addComponent(FruitBasket.class, FruitBasket.class, new ComponentParameter(Apple.class.getName()+ "one"),
                         new ComponentParameter(Apple.class.getName()+ "two"), 
                         new ComponentParameter(Apple.class.getName()+ "three"),
                         new ComponentParameter(Apple.class.getName()+ "four"));
        mpc.addComponent(Apple.class.getName()+ "one", AppleImpl1.class);
        mpc.addComponent(Apple.class.getName()+ "two", AppleImpl2.class);
        mpc.addComponent(Apple.class.getName()+ "three", AppleImpl3.class);
        mpc.addComponent(Apple.class.getName()+ "four", AppleImpl4.class);
        FruitBasket fb = mpc.getComponent(FruitBasket.class);
        assertEquals(fb.one.getX(), 1);
        assertEquals(fb.two.getX(), 2);
        assertEquals(fb.three.getX(), 3);
        assertEquals(fb.four.getX(), 4);
    }

    public void testJasonsNeed() {
        MutablePicoContainer mpc = new DefaultPicoContainer(new AnnotatedFieldInjection());
        mpc.addComponent(FruitBasket.class);
        mpc.addComponent(bindKey(Apple.class, "one"), AppleImpl1.class);
        mpc.addComponent(bindKey(Apple.class, "two"), AppleImpl2.class);
        mpc.addComponent(bindKey(Apple.class, "three"), AppleImpl3.class);
        mpc.addComponent(bindKey(Apple.class, "four"), AppleImpl4.class);
        try {
            // this level of terseness is the other way ....
            // this should not be barfing if if we can get binding to annotations working
            FruitBasket fb = mpc.getComponent(FruitBasket.class);
            assertEquals(fb.one.getX(), 1);
            assertEquals(fb.two.getX(), 2);
            assertEquals(fb.three.getX(), 3);
            assertEquals(fb.four.getX(), 4);
            fail();
        } catch (AbstractInjector.AmbiguousComponentResolutionException e) {
            // which Apple to inject into which apple field ?
        }
    }


    public interface Apple {
        int getX();
    }
    public static class AppleImpl1 implements Apple {
        public int getX() {
            return 1;
        }
    }
    public static class AppleImpl2 implements Apple {
        public int getX() {
            return 2;
        }
    }
    public static class AppleImpl3 implements Apple {
        public int getX() {
            return 3;
        }
    }
    public static class AppleImpl4 implements Apple {
        public int getX() {
            return 4;
        }
    }

    public static class FruitBasket {
        @Inject
        private @Bind(id = "one") Apple one;
        @Inject
        private @Bind(id = "two") Apple two;
        @Inject
        private @Bind(id = "three") Apple three;
        @Inject
        private @Bind(id = "four") Apple four;

        public FruitBasket() {
        }
    }

    // to become an annotation
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    public @interface Bind {
        String id();
    }

    // implicitly this goes into the jar somewhere
    public static class BindKey {
      private Class type;
      private String bindingId;
        public BindKey(Class type, String bindingId) {
            this.type = type;
            this.bindingId = bindingId;
        }
    }

    // implicitly this function goes into DPC
    public static BindKey bindKey(Class type, String bindingId) {
        return new BindKey(type, bindingId);
    }

}
