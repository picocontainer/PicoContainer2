package org.nanocontainer.remoting.rmi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.nanocontainer.remoting.rmi.testmodel.Thang;
import org.nanocontainer.remoting.rmi.testmodel.Thing;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.CglibProxyFactory;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public final class NanoNamingTestCase {
    private final ProxyFactory proxyFactory;
    private MutablePicoContainer pico;

    private NanoNaming naming;
    private ByRefKey thingKey;
    private ByRefKey thangKey;

    public NanoNamingTestCase() {
        proxyFactory = new CglibProxyFactory();
    }

    @Before public void setUp() throws MalformedURLException, NotBoundException, RemoteException, AlreadyBoundException {
        // Configure server side components
        pico = new DefaultPicoContainer();
        thingKey = new ByRefKey("thing");
        thangKey = new ByRefKey("thang");
        pico.addComponent(thingKey, Thing.class);
        pico.addComponent(thangKey, Thang.class);
        pico.addComponent(ArrayList.class);

        // Configure nano naming lookup service


        NanoNamingImpl nanoNaming = new NanoNamingImpl(RegistryHelper.getRegistry(), pico, proxyFactory);
        nanoNaming.bind("nanonaming");
        naming = (NanoNaming) Naming.lookup("rmi://localhost:9877/nanonaming");
    }



    @Test public void testRemoteComponentCanBeLookedUp() throws MalformedURLException, NotBoundException, RemoteException, AlreadyBoundException {
        // The client looks up the thing (by ref)
        Thing thing = (Thing) naming.lookup(new ByRefKey("thing"));
        Thang thang = thing.getThang();
        thang = thing.getThang();
        assertTrue(proxyFactory.isProxyClass(thang.getClass()));

        // add something to the client side list (by value)
        thang.getList().add("onclientonly");

        // get the server side list
        List list = pico.getComponent(ArrayList.class);
        assertEquals(0, list.size());
    }

    @Test public void testByRefObjectsCanBePassedDownAndUnwrapped() throws Exception {
        Thing serverThing = (Thing) pico.getComponent(thingKey);
        Thang serverThang = (Thang) pico.getComponent(thangKey);

        assertNull(serverThang.getThing());

        Thing thing = (Thing) naming.lookup(new ByRefKey("thing"));
        Thang thang = thing.getThang();
        thang.setThing(thing);

        Thing serverThangsNewThing = serverThang.getThing();
        assertSame(serverThing, serverThangsNewThing);
    }

}
