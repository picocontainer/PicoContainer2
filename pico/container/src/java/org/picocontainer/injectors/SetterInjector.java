/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ParameterName;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.behaviors.PropertyApplyingBehavior;
import org.picocontainer.behaviors.CachingBehavior;
import org.picocontainer.injectors.AbstractInjector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.Serializable;

/**
 * Instantiates components using empty constructors and
 * <a href="http://docs.codehaus.org/display/PICO/Setter+Injection">Setter Injection</a>.
 * For easy setting of primitive properties, also see {@link PropertyApplyingBehavior}.
 * <p/>
 * <em>
 * Note that this class doesn't cache instances. If you want caching,
 * use a {@link CachingBehavior} around this one.
 * </em>
 * </p>
 *
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @author Paul Hammant
 * @version $Revision$
 */
public class SetterInjector extends AbstractInjector {
    private transient ThreadLocalCyclicDependencyGuard instantiationGuard;
    protected transient List<Member> injectionMembers;
    protected transient Class[] injectionTypes;

    /**
     * Constructs a SetterInjectionComponentAdapter
     *
     * @param componentKey            the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters              the parameters to use for the initialization
     * @param monitor                 the component monitor used by this addAdapter
     * @param lifecycleStrategy       the component lifecycle strategy used by this addAdapter
     * @throws org.picocontainer.injectors.AbstractInjector.NotConcreteRegistrationException
     *                              if the implementation is not a concrete class.
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    public SetterInjector(final Object componentKey, final Class componentImplementation, Parameter[] parameters, ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy) throws  NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters, monitor, lifecycleStrategy);
    }


    /**
     * Constructs a SetterInjectionComponentAdapter with a {@link org.picocontainer.monitors.DelegatingComponentMonitor} as default.
     *
     * @param componentKey            the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters              the parameters to use for the initialization
     * @throws NotConcreteRegistrationException
     *                              if the implementation is not a concrete class.
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    public SetterInjector(final Serializable componentKey, final Class componentImplementation, Parameter... parameters) throws NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters);
    }


    protected Constructor getGreediestSatisfiableConstructor(PicoContainer container) throws PicoCompositionException {
        final Constructor constructor = getConstructor();
        getMatchingParameterListForSetters(container);
        return constructor;
    }

    private Constructor getConstructor()  {
        Object retVal = AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                try {
                    return getComponentImplementation().getConstructor((Class[])null);
                } catch (NoSuchMethodException e) {
                    return new PicoCompositionException(e);
                } catch (SecurityException e) {
                    return new PicoCompositionException(e);
                }
            }
        });
        if (retVal instanceof Constructor) {
            return (Constructor) retVal;
        } else {
            throw (PicoCompositionException) retVal;
        }
    }

    private Parameter[] getMatchingParameterListForSetters(PicoContainer container) throws PicoCompositionException {
        if (injectionMembers == null) {
            initializeInjectionMembersAndTypeLists();
        }

        final List<Object> matchingParameterList = new ArrayList<Object>(Collections.nCopies(injectionMembers.size(), null));
        final Set<Integer> nonMatchingParameterPositions = new HashSet<Integer>();
        final Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(injectionTypes);
        for (int i = 0; i < currentParameters.length; i++) {
            final Parameter parameter = currentParameters[i];
            boolean failedDependency = true;
            for (int j = 0; j < injectionTypes.length; j++) {
                if (matchingParameterList.get(j) == null && parameter.isResolvable(container, this, injectionTypes[j], new ParameterName() {
                    public String getParameterName() {
                        return ""; // TODO 
                    }
                })) {
                    matchingParameterList.set(j, parameter);
                    failedDependency = false;
                    break;
                }
            }
            if (failedDependency) {
                nonMatchingParameterPositions.add(i);
            }
        }

        final Set<Class> unsatisfiableDependencyTypes = new HashSet<Class>();
        for (int i = 0; i < matchingParameterList.size(); i++) {
            if (matchingParameterList.get(i) == null) {
                unsatisfiableDependencyTypes.add(injectionTypes[i]);
            }
        }
        if (unsatisfiableDependencyTypes.size() > 0) {
            throw new UnsatisfiableDependenciesException(this, null, unsatisfiableDependencyTypes, container);
        } else if (nonMatchingParameterPositions.size() > 0) {
            throw new PicoCompositionException("Following parameters do not match any of the injectionMembers for " + getComponentImplementation() + ": " + nonMatchingParameterPositions.toString());
        }
        return matchingParameterList.toArray(new Parameter[matchingParameterList.size()]);
    }

    public Object getComponentInstance(final PicoContainer container) throws
                                                                      PicoCompositionException
    {
        final Constructor constructor = getConstructor();
        if (instantiationGuard == null) {
            instantiationGuard = new ThreadLocalCyclicDependencyGuard() {
                public Object run() {
                    final Parameter[] matchingParameters = getMatchingParameterListForSetters(guardedContainer);
                    ComponentMonitor componentMonitor = currentMonitor();
                    Object componentInstance;
                    long startTime = System.currentTimeMillis();
                    Constructor constructorToUse = componentMonitor.instantiating(container,
                                                                                  SetterInjector.this, constructor);
                    try {
                        componentInstance = newInstance(constructorToUse, null);
                    } catch (InvocationTargetException e) {
                        componentMonitor.instantiationFailed(container, SetterInjector.this, constructorToUse, e);
                        if (e.getTargetException() instanceof RuntimeException) {
                            throw (RuntimeException) e.getTargetException();
                        } else if (e.getTargetException() instanceof Error) {
                            throw (Error) e.getTargetException();
                        }
                        throw new PicoCompositionException(e.getTargetException());
                    } catch (InstantiationException e) {
                        // can't get here because checkConcrete() will catch it earlier, but see PICO-191
                        ///CLOVER:OFF
                        componentMonitor.instantiationFailed(container, SetterInjector.this, constructorToUse, e);
                        throw new PicoCompositionException("Should never get here");
                        ///CLOVER:ON
                    } catch (IllegalAccessException e) {
                        // can't get here because either filtered or access mode set
                        ///CLOVER:OFF
                        componentMonitor.instantiationFailed(container, SetterInjector.this, constructorToUse, e);
                        throw new PicoCompositionException(e);
                        ///CLOVER:ON
                    }
                    Member member = null;
                    Object injected[] = new Object[injectionMembers.size()];
                    try {
                        for (int i = 0; i < injectionMembers.size(); i++) {
                            member = injectionMembers.get(i);
                            componentMonitor.invoking(container, SetterInjector.this, member, componentInstance);
                            Object toInject = matchingParameters[i].resolveInstance(guardedContainer, SetterInjector.this, injectionTypes[i], new ParameterName() {
                                public String getParameterName() {
                                    return ""; // TODO
                                }
                            });
                            injectIntoMember(member, componentInstance, toInject);
                            injected[i] = toInject;
                        }
                        componentMonitor.instantiated(container,
                                                      SetterInjector.this,
                                                      constructorToUse, componentInstance, injected, System.currentTimeMillis() - startTime);
                        return componentInstance;
                    } catch (InvocationTargetException e) {
                        componentMonitor.invocationFailed(member, componentInstance, e);
                        if (e.getTargetException() instanceof RuntimeException) {
                            throw (RuntimeException) e.getTargetException();
                        } else if (e.getTargetException() instanceof Error) {
                            throw (Error) e.getTargetException();
                        }
                        throw new PicoCompositionException(e.getTargetException());
                    } catch (IllegalAccessException e) {
                        componentMonitor.invocationFailed(member, componentInstance, e);
                        throw new PicoCompositionException(e);
                    }

                }
            };
        }
        instantiationGuard.setGuardedContainer(container);
        return instantiationGuard.observe(getComponentImplementation());
    }

    protected void injectIntoMember(Member member, Object componentInstance, Object toInject)
        throws IllegalAccessException, InvocationTargetException
    {
        ((Method)member).invoke(componentInstance, toInject);
    }

    public void verify(final PicoContainer container) throws PicoCompositionException {
        if (verifyingGuard == null) {
            verifyingGuard = new ThreadLocalCyclicDependencyGuard() {
                public Object run() {
                    final Parameter[] currentParameters = getMatchingParameterListForSetters(guardedContainer);
                    for (int i = 0; i < currentParameters.length; i++) {
                        currentParameters[i].verify(container, SetterInjector.this, injectionTypes[i], new ParameterName() {
                            public String getParameterName() {
                                return ""; // TODO
                            }
                        });
                    }
                    return null;
                }
            };
        }
        verifyingGuard.setGuardedContainer(container);
        verifyingGuard.observe(getComponentImplementation());
    }

    protected void initializeInjectionMembersAndTypeLists() {
        injectionMembers = new ArrayList<Member>();
        final List<Class> typeList = new ArrayList<Class>();
        final Method[] methods = getMethods();
        for (final Method method : methods) {
            final Class[] parameterTypes = method.getParameterTypes();
            // We're only interested if there is only one parameter and the method name is bean-style.
            if (parameterTypes.length == 1) {
                boolean isInjector = isInjectorMethod(method);
                if (isInjector) {
                    injectionMembers.add(method);
                    typeList.add(parameterTypes[0]);
                }
            }
        }
        injectionTypes = typeList.toArray(new Class[0]);
    }

    protected boolean isInjectorMethod(Method method) {
        String methodName = method.getName();
        return methodName.length() >= getInjectorPrefix().length() + 1 && methodName.startsWith(getInjectorPrefix()) && Character.isUpperCase(methodName.charAt(getInjectorPrefix().length()));
    }

    protected String getInjectorPrefix() {
        return "set";
    }

    private Method[] getMethods() {
        return (Method[]) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return getComponentImplementation().getMethods();
            }
        });
    }
}
