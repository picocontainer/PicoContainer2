package org.nanocontainer.remoting.rmi;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Iterator;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

import com.thoughtworks.proxy.ProxyFactory;

/**
 * @author Neil Clayton
 * @author Aslak Helles&oslash;y
 * @author Obie Fernandez
 * @version $Revision$
 */
public class RemoteInterceptorImpl extends UnicastRemoteObject implements RemoteInterceptor {
    private final transient ByRefKey key;
    private final PicoContainer pico;
    private final ProxyFactory proxyFactory;
    private final Registry registry;

    public RemoteInterceptorImpl(Registry registry, PicoContainer pico, ByRefKey key, ProxyFactory proxyFactory) throws RemoteException {
        super();
        if (registry == null) throw new NullPointerException("registry");
        this.registry = registry;
        this.pico = pico;
        this.proxyFactory = proxyFactory;
        this.key = key;
    }

    public Object invoke(Invocation invocation) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, RemoteException {

        Object target = pico.getComponent(key);

        Object[] args = invocation.getArgs();
        invocation.setArgs(unwrap(args));

        Object resultOfInvocation = invocation.invoke(target);

        ComponentAdapter componentAdapter = getComponentAdapterByInstance(pico, resultOfInvocation);
        if (componentAdapter != null && componentAdapter.getComponentKey() instanceof ByRefKey) {
            NanoNamingImpl naming = new NanoNamingImpl(registry, pico, proxyFactory);
            ByRefKey key = (ByRefKey) componentAdapter.getComponentKey();
            return naming.lookup(key);
        } else {
            return resultOfInvocation;
        }
    }

    private Object[] unwrap(Object[] args) {
        if (args == null) {
            return null;
        }
        for (int i = 0; i < args.length; i++) {
            args[i] = unwrap(args[i]);
        }
        return args;
    }

    private Object unwrap(Object arg) {
        if (arg instanceof KeyHolder && proxyFactory.isProxyClass(arg.getClass())) {
            KeyHolder keyHolder = (KeyHolder) arg;
            ByRefKey key = keyHolder.getKey();
            arg = pico.getComponent(key);
        }
        return arg;
    }

    public void bind(ComponentAdapter componentAdapter) throws RemoteException {
        ByRefKey byRefKey = (ByRefKey) componentAdapter.getComponentKey();
        Serializable key = byRefKey.getValue();
        registry.rebind(key.toString(), this);
    }

    private ComponentAdapter getComponentAdapterByInstance(PicoContainer pico, Object componentInstance) {
        Collection componentAdapters = pico.getComponentAdapters();
        for (Object componentAdapter1 : componentAdapters) {
            ComponentAdapter componentAdapter = (ComponentAdapter)componentAdapter1;
            if (componentAdapter.getComponentInstance(pico).equals(componentInstance)) {
                return componentAdapter;
            }
        }
        return null;
    }
}