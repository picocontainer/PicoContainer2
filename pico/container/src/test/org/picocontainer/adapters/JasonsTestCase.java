package org.picocontainer.adapters;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import junit.framework.TestCase;

/** @author Paul Hammant */
public class JasonsTestCase extends TestCase {

    public void testJasonsNeed() {
        MutablePicoContainer mpc = new DefaultPicoContainer();
        mpc.addComponent(FruitBasket.class);
        mpc.addComponent(bindKey(Apple.class, "one"), new AppleImpl(1));
        mpc.addComponent(bindKey(Apple.class, "two"), new AppleImpl(2));
        mpc.addComponent(bindKey(Apple.class, "three"), new AppleImpl(3));
        mpc.addComponent(bindKey(Apple.class, "four"), new AppleImpl(4));
        FruitBasket fb = mpc.getComponent(FruitBasket.class);
//        assertEquals(fb.one.getX(), 1);
//        assertEquals(fb.two.getX(), 2);
//        assertEquals(fb.three.getX(), 3);
//        assertEquals(fb.four.getX(), 4);
    }

    public interface Apple {
        int getX();
    }
    public static class AppleImpl implements Apple {
        int x;
        public AppleImpl(int x) {
            this.x = x;
        }

        public int getX() {
            return x;
        }
    }

    public static class FruitBasket {
        private @Bind(id = "one") Apple one;
        private @Bind(id = "two") Apple two;
        private @Bind(id = "three") Apple three;
        private @Bind(id = "four") Apple four;
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
