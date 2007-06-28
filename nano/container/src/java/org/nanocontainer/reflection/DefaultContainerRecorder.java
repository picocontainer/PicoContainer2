/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.reflection;

import org.nanocontainer.integrationkit.ContainerRecorder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is serializable. The original container will not be serialized
 * (for performance reasons), but the invocations will, so they can be replayed at the
 * other end of the wire.
 *
 * @author Konstantin Pribluda ( konstantin.pribluda(at)infodesire.com )
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 */
public final class DefaultContainerRecorder implements Serializable, ContainerRecorder {

    private final List<Invocation> invocations = new ArrayList<Invocation>();
    private final transient MutablePicoContainer container;

    private final InvocationHandler invocationRecorder = new InvocationRecorder();

    public DefaultContainerRecorder(MutablePicoContainer container) {
        this.container = container;
    }

    public MutablePicoContainer getContainerProxy() {
        return (MutablePicoContainer) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{MutablePicoContainer.class}, invocationRecorder);
    }

    public void replay(MutablePicoContainer target) {
        for (Object invocation1 : invocations) {
            Invocation invocation = (Invocation) invocation1;
            try {
                invocation.invoke(target);
            } catch (IllegalAccessException e) {
                throw new PicoException(e) {
                };
            } catch (InvocationTargetException e) {
                throw new PicoException(e) {
                };
            }
        }
    }

    private final class Invocation implements Serializable {
        private transient Method method;
        private final Object[] args;

        Invocation(Method method, Object[] args) {
            this.method = method;
            this.args = args;
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
            out.writeUTF(method.getName());
            out.writeObject(method.getDeclaringClass());
            Class[] parameterTypes = method.getParameterTypes();
            out.writeInt(parameterTypes.length);
            for (Class parameterType : parameterTypes) {
                out.writeObject(parameterType);
            }
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            String methodName = in.readUTF();
            Class declaringClass = (Class) in.readObject();
            int n = in.readInt();
            Class[] parameterTypes = new Class[n];
            for (int i = 0; i < n; i++) {
                parameterTypes[i] = (Class) in.readObject();
            }
            try {
                method = declaringClass.getMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                throw new IOException("Couldn't load method " + methodName);
            }
        }

        public void invoke(MutablePicoContainer target) throws IllegalAccessException, InvocationTargetException {
            method.invoke(target, args);
        }
    }

    private class InvocationRecorder implements InvocationHandler, Serializable {
        /**
         * Record invocation and invoke on underlying container
         */
        public Object invoke(Object proxy, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
            invocations.add(new Invocation(method, args));
            return method.invoke(container, args);
        }
    }

}
