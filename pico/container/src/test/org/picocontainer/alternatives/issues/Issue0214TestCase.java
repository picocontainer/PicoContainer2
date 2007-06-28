package org.picocontainer.alternatives.issues;

import junit.framework.TestCase;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.visitors.VerifyingVisitor;
import org.picocontainer.behaviors.ImplementationHidingBehaviorFactory;
import org.picocontainer.injectors.ConstructorInjectionFactory;
import org.picocontainer.DefaultPicoContainer;

public class Issue0214TestCase extends TestCase {

    // This bug as described in the bug report, http://jira.codehaus.org/browse/PICO-214, cannot be reproduced.
    public void testTheBug() {
        final MutablePicoContainer pico = new DefaultPicoContainer(new ImplementationHidingBehaviorFactory().forThis(new ConstructorInjectionFactory()));
        pico.addComponent(A.class);

        /* This is a workaround for the bug described further down. Normally
         * this method call should only be needed if specific requirements for
         * parameters exist, but not if PicoContainer shall resolve the
         * dependencies itself. However, with ImplementationHidingPicoContainer
         * this is currently the only way to register a class/interface such
         * that the automatic resolution works.
         */
        pico.addComponent(I1.class, B.class);

        /* The following addAdapter(Object, Class) of
         * ImplementationHidingPicoContainer is buggy, as it contains
         * "ComponentAdapter delegate = componentFactory.createComponentAdapter(componentKey,
         * componentImplementation, new Parameter[0]);". Instead of "new
         * Parameter[0]" it should be "null" to have a behaviour consistent to
         * DefaultPicoContainer, i.e. if PicoContainer shall resolve
         * dependencies itself.
         */
        pico.addComponent(I2.class, C.class);

        /* The following verify() throws the exception, but is expected not to
         * throw: "org.picocontainer.PicoVerificationException:
         * [[org.picocontainer.PicoCompositionException: Either do the
         * specified parameters not match any of the following constructors:
         * [public PicoContainerBugTest$C(PicoContainerBugTest$A)] or the
         * constructors were not accessible for 'class
         * PicoContainerBugTest$C']]".
         *
         * I believe that the error comes this way: In method
         * getGreediestSatisfiableConstructor parameters are checked against
         * null and if parameters is not null it is assumed that specific
         * parameters have been given so that no automatic resolution takes
         * place. As now during registration instead of "null" falsly "new
         * Parameter[0]" was stored, this is now interpreted as if only the
         * nullary constructor shall be used, and if that doesn't exist, the
         * exception is thrown.
         */
        new VerifyingVisitor().traverse(pico);
    }

    public static interface I1 {
    }

    public static interface I2 {
    }

    public static class A {
        public A() {
        }
    }

    public static class B implements I1 {
        public B(final A a) {
        }
    }

    public static class C implements I2 {
        public C(final A a) {
        }
    }
}
