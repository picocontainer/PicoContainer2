/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.nanocontainer.remoting.ejb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.rmi.NoSuchObjectException;
import java.util.Hashtable;

import javax.ejb.EJBHome;
import javax.ejb.EJBObject;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.behaviors.CachingBehavior;
import org.picocontainer.adapters.AbstractAdapter;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.factory.StandardProxyFactory;


/**
 * {@link ComponentAdapter}, that is able to lookup and instantiate an EJB as client.
 * <p>
 * The default mode for this adapter is late binding i.e. the adapter returns a proxy object for the requested type as
 * instance. The lookup for the EJB is started with the first call of the proxy and the stub is created. Any further
 * call will do the same, if the last call was aborted by a remote exception. With early binding the stub wiill be
 * created in the constructor and the initialization of the adapter may fail.
 * </p>
 * <p>
 * The adapter is using internally an own proxy for the stub object. This enables a failover in case of a temporary
 * unavailability of the application server providing the EJB. With every call to a methof of the EJB, the adapter is
 * able to reestablish the connection, if the last call had been failed. In this case you might try a call at some
 * minutes later again, but you should give up after some trials.
 * </p>
 * <p>
 * If you want to cache the EJB with a {@link CachingBehavior}, you have to use a
 * {@link org.picocontainer.gems.adapters.ThreadLocalReference}, since you may not use an instance of the EJB in
 * different threads. Use an {@link EJBClientComponentAdapterFactory} for such a completely transparent
 * {@link ThreadLocal} support.
 * </p>
 * @author J&ouml;rg Schaible
 */
public class EJBClientAdapter extends AbstractAdapter {

    private final Object proxy;

    /**
     * Construct a {@link ComponentAdapter} for an EJB. This constructor implies the home interface follows normal
     * naming conventions. The adapter will use the default {@link InitialContext} and will also do late binding.
     * @param name the EJB's JNDI name
     * @param type the implemented interface of the EJB
     * @throws ClassNotFoundException if the home interface could not be found
     */
    public EJBClientAdapter(final String name, final Class type) throws ClassNotFoundException {
        this(name, type, null, false);
    }

    /**
     * Construct a {@link ComponentAdapter} for an EJB. This constructor implies the home interface follows normal
     * naming conventions. The implementation will use the default {@link InitialContext}.
     * @param name the EJB's JNDI name
     * @param type the implemented interface of the EJB
     * @param earlyBinding <code>true</code> if the EJB should be instantiated in the constructor
     * @throws ClassNotFoundException if the home interface could not be found
     */
    public EJBClientAdapter(final String name, final Class type, final boolean earlyBinding)
            throws ClassNotFoundException {
        this(name, type, null, earlyBinding);
    }

    /**
     * Construct a {@link ComponentAdapter} for an EJB. This constructor implies the home interface follows normal
     * naming conventions.
     * @param name the EJB's JNDI name
     * @param type the implemented interface of the EJB
     * @param environment the environment {@link InitialContext} to use
     * @param earlyBinding <code>true</code> if the EJB should be instantiated in the constructor.
     * @throws ClassNotFoundException if the home interface could not be found
     */
    public EJBClientAdapter(
            final String name, final Class type, final Hashtable environment, final boolean earlyBinding)
            throws ClassNotFoundException {
        this(name, type, type.getClassLoader().loadClass(type.getName() + "Home"), environment, earlyBinding);
    }

    /**
     * Construct a {@link ComponentAdapter} for an EJB.
     * @param name the EJB's JNDI name
     * @param type the implemented interface of the EJB
     * @param homeInterface the home interface of the EJB
     * @param environment the environment {@link InitialContext} to use
     * @param earlyBinding <code>true</code> if the EJB should be instantiated in the constructor
     * @throws PicoCompositionException if lookup of home interface fails
     */
    public EJBClientAdapter(
            final String name, final Class type, final Class homeInterface, final Hashtable environment,
            final boolean earlyBinding) {
        super(name, type);
        if (!EJBHome.class.isAssignableFrom(homeInterface)) {
            throw new ClassCastException(homeInterface.getName() + " is not a " + EJBHome.class.getName());
        }
        if (!EJBObject.class.isAssignableFrom(type)) {
            throw new ClassCastException(type.getName() + " is not a " + EJBObject.class.getName());
        }
        if (!type.isInterface()) {
            throw new PicoCompositionException(type.getName() + " must be an interface");
        }
        final Invoker invoker = new EJBClientInvoker(name, type, homeInterface, environment);
        proxy = new StandardProxyFactory().createProxy(new Class[]{type}, invoker);
        if (earlyBinding) {
            proxy.hashCode();
        }
    }

    /**
     * Retrieve the proxy for the EJB instance.
     * @see org.picocontainer.ComponentAdapter#getComponentInstance(PicoContainer)
     */
    public Object getComponentInstance(final PicoContainer pico) {
        return proxy;
    }

    /**
     * This implementation has nothing to verify.
     * @see org.picocontainer.ComponentAdapter#verify(PicoContainer)
     */
    public void verify(final PicoContainer pico) {
        // cannot do anything here
    }

    private final static class EJBClientInvoker implements Invoker {
        private final String name;
        private final Class type;
        private final Class home;
        private final Hashtable environment;
        private transient Object stub;

        private EJBClientInvoker(final String name, final Class type, final Class home, final Hashtable environment) {
            this.name = name;
            this.type = type;
            this.home = home;
            this.environment = environment;
        }

        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            try {
                if (stub == null) {
                    stub = bind();
                }
                return method.invoke(stub, args);
            } catch (final InvocationTargetException e) {
                stub = null;
                throw e.getTargetException();
            } catch (Throwable t) {
                stub = null;
                throw t;
            }
        }

        private Object bind() throws InvocationTargetException {
            try {
                final InitialContext context = new InitialContext(environment);
                final Object ref = context.lookup(name);
                final Object proxy = PortableRemoteObject.narrow(ref, home);
                final Class homeClass = proxy.getClass();
                final Method create = homeClass.getMethod("create", (Class[])null);
                if (type.isAssignableFrom(create.getReturnType())) {
                    return create.invoke(proxy, (Object[])null);
                }
                throw new PicoCompositionException(
                        "Wrong return type of EJBHome implementation", new ClassCastException(create.getReturnType()
                                .getName()));
            } catch (final SecurityException e) {
                throw new PicoCompositionException(
                        "Security Exception occured accessing create method of home interface of " + name, e);
            } catch (final NoSuchMethodException e) {
                throw new PicoCompositionException("Home interface of " + name + " has no create method", e);
            } catch (final NameNotFoundException e) {
                // Server startup, application not bound yet
                throw new InvocationTargetException(e);
            } catch (final NamingException e) {
                final Throwable rootCause = e.getRootCause();
                if (rootCause != null
                        && (rootCause instanceof SocketTimeoutException || rootCause instanceof NoSuchObjectException)) {
                    // Server down, did not have a connection or JNDI not stuffed yet
                    throw new InvocationTargetException(e);
                } else {
                    throw new PicoCompositionException("InitialContext has no EJB named " + name, e);
                }
            } catch (final IllegalAccessException e) {
                throw new PicoCompositionException("Cannot access default constructor for " + name, e);
            }
        }
    }
}
