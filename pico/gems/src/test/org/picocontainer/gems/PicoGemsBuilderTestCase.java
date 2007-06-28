package org.picocontainer.gems;

import junit.framework.TestCase;
import com.thoughtworks.xstream.XStream;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;

import static org.picocontainer.gems.PicoGemsBuilder.IMPL_HIDING;
import static org.picocontainer.gems.PicoGemsBuilder.LOG4J;
import org.picocontainer.gems.monitors.Log4JComponentMonitor;
import org.picocontainer.gems.monitors.CommonsLoggingComponentMonitor;

public class PicoGemsBuilderTestCase extends TestCase {

    XStream xs;

    protected void setUp() throws Exception {
        xs = new XStream();
        xs.alias("PICO", DefaultPicoContainer.class);
        xs.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
    }

    public void testWithImplementationHiding() {
        MutablePicoContainer mpc = new PicoBuilder().withBehaviors(IMPL_HIDING()).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.gems.behaviors.ImplementationHidingBehaviorFactory\n" +
                "    delegate=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithLog4JComponentMonitor() {
        MutablePicoContainer mpc = new PicoBuilder().withMonitor(Log4JComponentMonitor.class).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.gems.monitors.Log4JComponentMonitor\n" +
                "    delegate=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithLog4JComponentMonitorByInstance() {
        MutablePicoContainer mpc = new PicoBuilder().withMonitor(LOG4J()).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.gems.monitors.Log4JComponentMonitor\n" +
                "    delegate=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithCommonsLoggingComponentMonitor() {
        MutablePicoContainer mpc = new PicoBuilder().withMonitor(CommonsLoggingComponentMonitor.class).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.gems.monitors.CommonsLoggingComponentMonitor\n" +
                "    delegate=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    private String simplifyRepresentation(MutablePicoContainer mpc) {
        String foo = xs.toXML(mpc);
        foo = foo.replace('$','_');
        foo = foo.replaceAll("/>","");
        foo = foo.replaceAll("</","");
        foo = foo.replaceAll("<","");
        foo = foo.replaceAll(">","");
        foo = foo.replaceAll("\n  childrenStarted","");
        foo = foo.replaceAll("\n  componentAdapters","");
        foo = foo.replaceAll("\n  orderedComponentAdapters","");
        foo = foo.replaceAll("\n  startedfalsestarted","");
        foo = foo.replaceAll("\n  disposedfalsedisposed","");
        foo = foo.replaceAll("\n  handler","");
        foo = foo.replaceAll("\n  children","");
        foo = foo.replaceAll("\n  delegate\n","\n");
        foo = foo.replaceAll("\n    delegate\n","\n");
        foo = foo.replaceAll("\n    outer-class reference=\"/PICO\"","");
        foo = foo.replaceAll("\n  componentCharacteristic class=\"org.picocontainer.DefaultPicoContainer$1\"","");
        foo = foo.replaceAll("\n  componentCharacteristics","");
        foo = foo.replaceAll("\n  componentKeyToAdapterCache","");
        foo = foo.replaceAll("\n    startedComponentAdapters","");
        foo = foo.replaceAll("\n    props","");
        foo = foo.replaceAll("\"class=","\"\nclass=");
        foo = foo.replaceAll("\n  componentFactory\n","\n");
        foo = foo.replaceAll("\n  componentMonitor\n","\n");
        foo = foo.replaceAll("\n  lifecycleManager","");
        foo = foo.replaceAll("class=\"org.picocontainer.DefaultPicoContainer_1\"","");
        foo = foo.replaceAll("class=\"org.picocontainer.DefaultPicoContainer_OrderedComponentAdapterLifecycleManager\"","");
        foo = foo.replaceAll("class=","=");
        foo = foo.replaceAll("\"","");
        foo = foo.replaceAll(" \n","\n");
        foo = foo.replaceAll(" =","=");
        foo = foo.replaceAll("\n\n\n","\n");
        foo = foo.replaceAll("\n\n","\n");

        return foo;
    }




}
