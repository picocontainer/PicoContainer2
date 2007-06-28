package org.picocontainer.doc.tutorial.blocks;

import junit.framework.TestCase;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.behaviors.CachingBehaviorFactory;
import org.picocontainer.behaviors.CachingBehavior;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.adapters.InstanceAdapter;
import org.picocontainer.injectors.SetterInjector;
import org.picocontainer.injectors.SetterInjectionFactory;
import org.picocontainer.behaviors.SynchronizedBehavior;
import org.picocontainer.behaviors.SynchronizedBehaviorFactory;
import org.picocontainer.doc.introduction.Apple;
import org.picocontainer.doc.introduction.Juicer;
import org.picocontainer.doc.introduction.Peeler;


/**
 * Test case for the snippets used in "Component Adapters and Factories"
 * 
 * @author J&ouml;rg Schaible
 */
public class BuildingBlocksTestCase extends TestCase {
    public void testRegisterConvenient() {
        // START SNIPPET: register-convenient
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        picoContainer.addComponent(Juicer.class);
        picoContainer.addComponent("My Peeler", Peeler.class);
        picoContainer.addComponent(new Apple());
        // END SNIPPET: register-convenient
        // START SNIPPET: register-direct
        picoContainer.addAdapter(new InstanceAdapter("Another Apple", new Apple(), NullLifecycleStrategy.getInstance(),
                                                                        NullComponentMonitor.getInstance()));
        // END SNIPPET: register-direct
    }

    public void testRegisterEquivalentConvenient() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        // START SNIPPET: register-equivalent-convenient
        picoContainer.addComponent(Juicer.class);
        // END SNIPPET: register-equivalent-convenient
    }

    public void testRegisterEquivalentAtLength() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        // START SNIPPET: register-equivalent-at-length
        picoContainer.addAdapter(
                new CachingBehavior(
                        new ConstructorInjector(Juicer.class, Juicer.class)));
        // END SNIPPET: register-equivalent-at-length
    }

    public void testRegisterDifferentComponentAdapterFactory() {

        // START SNIPPET: register-different-componentFactory
        MutablePicoContainer picoContainer = new DefaultPicoContainer(
                new SynchronizedBehaviorFactory().forThis(new CachingBehaviorFactory().forThis(new SetterInjectionFactory())));
        // END SNIPPET: register-different-componentFactory
    }

    public void testRegisterEquivalentAtLength2() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        // START SNIPPET: register-equivalent-at-length2
        picoContainer.addAdapter(
                new SynchronizedBehavior(
                        new CachingBehavior(
                                new SetterInjector(
                                        JuicerBean.class, JuicerBean.class, (Parameter[])null, NullComponentMonitor.getInstance(), NullLifecycleStrategy.getInstance()))));
        // END SNIPPET: register-equivalent-at-length2
    }
}
