package org.picocontainer.adapters;

import static org.picocontainer.BindKey.bindKey;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import junit.framework.TestCase;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.annotations.Bind;
import org.picocontainer.annotations.Inject;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.injectors.AnnotatedFieldInjection;
import org.picocontainer.injectors.MethodInjection;
import org.picocontainer.injectors.SetterInjection;

/** @author Paul Hammant */
public class TypedBindingAnnotationTestCase extends TestCase {

	public void testFieldInjectionWithBindings() {
        MutablePicoContainer mpc = new DefaultPicoContainer(new AnnotatedFieldInjection());

        addFiveComponents(mpc);
        FruitBasket fb = mpc.getComponent(FruitBasket.class);
        assertFourMemberApplesAreRight(fb);
        assertGettingOfAppleOneWorks(mpc);
    }

    private void assertGettingOfAppleOneWorks(MutablePicoContainer mpc) {
        try {
            mpc.getComponent(Apple.class);
            fail("should have barfed");
        } catch (AbstractInjector.AmbiguousComponentResolutionException e) {
            System.out.println("");
            // expected
        }
        assertNotNull(mpc.getComponent(Apple.class, BindOne.class));
    }

    public void testBindingAnnotationsWithConstructorInjection() {
        MutablePicoContainer mpc = new DefaultPicoContainer();

        addFiveComponents(mpc);
        FruitBasket fb = mpc.getComponent(FruitBasket.class);
        assertFourMemberApplesAreRight(fb);
        assertGettingOfAppleOneWorks(mpc);
    }

    private void assertFourMemberApplesAreRight(FruitBasket fb) {
        assertNotNull(fb);
        assertEquals(fb.one.getX(), 1);
        assertEquals(fb.two.getX(), 2);
        assertEquals(fb.three.getX(), 3);
        assertEquals(fb.four.getX(), 4);
    }

    public void testBindingAnnotationsWithMethodInjection() {
        MutablePicoContainer mpc = new DefaultPicoContainer(new MethodInjection("foo"));
        addFiveComponents(mpc);
        FruitBasket fb = mpc.getComponent(FruitBasket.class);
        assertFourMemberApplesAreRight(fb);
        assertGettingOfAppleOneWorks(mpc);

    }

    public void testBindingAnnotationsWithSetterInjection() {
        MutablePicoContainer mpc = new DefaultPicoContainer(new SetterInjection());
        addFiveComponents(mpc);
        FruitBasket fb = mpc.getComponent(FruitBasket.class);
        assertFourMemberApplesAreRight(fb);
        assertGettingOfAppleOneWorks(mpc);

    }



    private void addFiveComponents(MutablePicoContainer mpc) {
        mpc.addComponent(FruitBasket.class);
        mpc.addComponent(bindKey(Apple.class, BindOne.class), AppleImpl1.class);
        mpc.addComponent(bindKey(Apple.class, BindTwo.class), AppleImpl2.class);
        mpc.addComponent(bindKey(Apple.class, BindThree.class), AppleImpl3.class);
        mpc.addComponent(bindKey(Apple.class, BindFour.class), AppleImpl4.class);
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

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Bind
    public static @interface BindOne {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Bind
    public static @interface BindTwo {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Bind
    public static @interface BindThree {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Bind
    public static @interface BindFour {}

    public static class FruitBasket {
        @Inject
        private @BindOne Apple one;
        @Inject
        private @BindTwo Apple two;
        @Inject
        private @BindThree Apple three;
        @Inject
        private @BindFour Apple four;

        public FruitBasket() {
        }

        // used in testBindingAnnotationsWithConstructorInjection()
        public FruitBasket(@BindOne Apple one, @BindTwo Apple two, @BindThree Apple three, @BindFour Apple four) {
            this.one = one;
            this.two = two;
            this.three = three;
            this.four = four;
        }

        // used in testBindingAnnotationsWithMethodInjection()
        public void foo(@BindOne Apple one, @BindTwo Apple two, @BindThree Apple three, @BindFour Apple four) {
            this.one = one;
            this.two = two;
            this.three = three;
            this.four = four;
        }

        public void setOne(@BindOne Apple one) {
            this.one = one;
        }

        public void setTwo(@BindTwo Apple two) {
            this.two = two;
        }

        public void setThree(@BindThree Apple three) {
            this.three = three;
        }

        public void setFour(@BindFour Apple four) {
            this.four = four;
        }
    }


}
