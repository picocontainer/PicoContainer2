package org.nanocontainer.remoting.rmi;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.nanocontainer.remoting.rmi.testmodel.Thang;
import org.nanocontainer.remoting.rmi.testmodel.Thing;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.CglibProxyFactory;

import junit.framework.TestCase;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class RemoteInterceptorImplTestCase extends TestCase {
    public void testInvocationsArePassedThrough() throws RemoteException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        MutablePicoContainer pico = new DefaultPicoContainer();
        ByRefKey thangKey = new ByRefKey("thang");
        pico.addComponent(thangKey, Thang.class);
        pico.addComponent(ArrayList.class);
        List serverList = pico.getComponent(ArrayList.class);

        ProxyFactory proxyFactory = new CglibProxyFactory();
        RemotingInterceptor remoteInterceptor = new RemoteInterceptorImpl(RegistryHelper.getRegistry(), pico, thangKey, proxyFactory);
        Collection collection = (Collection) remoteInterceptor.invoke(new Invocation("getList", null, null));
        assertSame(serverList, collection);
    }

    public void testByRefComponentsShouldBeProxied() throws RemoteException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        MutablePicoContainer pico = new DefaultPicoContainer();
        ProxyFactory proxyFactory = new CglibProxyFactory();
        ByRefKey thingKey = new ByRefKey("thing");
        ByRefKey thangKey = new ByRefKey("thang");
        ComponentAdapter thingAdapter = pico.addComponent(thingKey, Thing.class).getComponentAdapter(thingKey);
        pico.addComponent(thangKey, Thang.class);
        pico.addComponent(ArrayList.class);

        RemoteInterceptorImpl remoteInterceptor = new RemoteInterceptorImpl(RegistryHelper.getRegistry(), pico, thingKey, proxyFactory);
        remoteInterceptor.bind(thingAdapter);
        Thang thang = (Thang) remoteInterceptor.invoke(new Invocation("getThang", null, null));
        assertTrue(proxyFactory.isProxyClass(thang.getClass()));
        Thang serverThang = (Thang) pico.getComponent(thangKey);

        assertNotSame(serverThang, thang);
    }
}