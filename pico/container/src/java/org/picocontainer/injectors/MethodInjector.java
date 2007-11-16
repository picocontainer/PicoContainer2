/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.injectors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;

/**
 * Injection will happen through a single method for the component.
 *
 * Most likely it is a method called 'inject', though that can be overridden.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @author Zohar Melamed
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 */
public class MethodInjector extends SingleMemberInjector {
    private transient ThreadLocalCyclicDependencyGuard instantiationGuard;
    private final String methodName;


    /**
     * Creates a MethodInjector
     *
     * @param componentKey            the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters              the parameters to use for the initialization
     * @param monitor                 the component monitor used by this addAdapter
     * @param lifecycleStrategy       the component lifecycle strategy used by this addAdapter
     * @param methodName              the method name
     * @param useNames                use argument names when looking up dependencies
     * @throws AbstractInjector.NotConcreteRegistrationException
     *                              if the implementation is not a concrete class.
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    public MethodInjector(final Object componentKey, final Class componentImplementation, Parameter[] parameters, ComponentMonitor monitor,
                          LifecycleStrategy lifecycleStrategy, String methodName, boolean useNames) throws AbstractInjector.NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters, monitor, lifecycleStrategy, useNames);
        this.methodName = methodName;
    }

    protected Method getInjectorMethod() {
        Method[] methods = super.getComponentImplementation().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    public Object getComponentInstance(final PicoContainer container) throws PicoCompositionException {
        if (instantiationGuard == null) {
            instantiationGuard = new ThreadLocalCyclicDependencyGuard() {
                public Object run() {
                    Method method;
                    Object inst = null;
                    try {
                        method = getInjectorMethod();
                    } catch (AmbiguousComponentResolutionException e) {
                        e.setComponent(getComponentImplementation());
                        throw e;
                    }
                    ComponentMonitor componentMonitor = currentMonitor();
                    try {
                        componentMonitor.instantiating(container, MethodInjector.this, null);
                        long startTime = System.currentTimeMillis();
                        Object[] parameters = null;
                        inst = getComponentImplementation().newInstance();
                        if (method != null) {
                            parameters = getMemberArguments(guardedContainer, method);
                            method.invoke(inst, parameters);
                        }
                        componentMonitor.instantiated(container,
                                                      MethodInjector.this,
                                                      null, inst, parameters, System.currentTimeMillis() - startTime);
                        return inst;
                    } catch (InvocationTargetException e) {
                        componentMonitor.instantiationFailed(container, MethodInjector.this, null, e);
                        if (e.getTargetException() instanceof RuntimeException) {
                            throw (RuntimeException) e.getTargetException();
                        } else if (e.getTargetException() instanceof Error) {
                            throw (Error) e.getTargetException();
                        }
                        throw new PicoCompositionException(e.getTargetException());
                    } catch (InstantiationException e) {
                        return caughtInstantiationException(componentMonitor, null, e, container);
                    } catch (IllegalAccessException e) {
                        return caughtIllegalAccessException(componentMonitor, method, inst, e);

                    }
                }
            };
        }
        instantiationGuard.setGuardedContainer(container);
        return instantiationGuard.observe(getComponentImplementation());
    }

    protected Object[] getMemberArguments(PicoContainer container, final Method method) {
        return super.getMemberArguments(container, method, method.getParameterTypes());
    }


    public void verify(final PicoContainer container) throws PicoCompositionException {
        if (verifyingGuard == null) {
            verifyingGuard = new ThreadLocalCyclicDependencyGuard() {
                public Object run() {
                    final Method method = getInjectorMethod();
                    final Class[] parameterTypes = method.getParameterTypes();
                    final Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(parameterTypes);
                    for (int i = 0; i < currentParameters.length; i++) {
                        currentParameters[i].verify(container, MethodInjector.this, parameterTypes[i],
                                                    new SingleMemberInjectorParameterName(method, i), useNames());
                    }
                    return null;
                }
            };
        }
        verifyingGuard.setGuardedContainer(container);
        verifyingGuard.observe(getComponentImplementation());
    }

    public String getDescriptor() {
        return "MethodInjector-";
    }

}