/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.injectors;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.LifecycleStrategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Injection will happen through a constructor for the component.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @author Zohar Melamed
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 */
@SuppressWarnings("serial")
public class ConstructorInjector<T> extends SingleMemberInjector<T> {
    private transient List<Constructor<T>> sortedMatchingConstructors;
    private transient ThreadLocalCyclicDependencyGuard<T> instantiationGuard;

    /**
     * Creates a ConstructorInjector
     *
     * @param componentKey            the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters              the parameters to use for the initialization
     * @param monitor                 the component monitor used by this addAdapter
     * @param lifecycleStrategy       the component lifecycle strategy used by this addAdapter
     * @param useNames                use argument names when looking up dependencies
     * @throws org.picocontainer.injectors.AbstractInjector.NotConcreteRegistrationException
     *                              if the implementation is not a concrete class.
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    public ConstructorInjector(final Object componentKey, final Class componentImplementation, Parameter[] parameters, ComponentMonitor monitor,
                               LifecycleStrategy lifecycleStrategy, boolean useNames) throws  NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters, monitor, lifecycleStrategy, useNames);
    }

    protected Constructor<T> getGreediestSatisfiableConstructor(PicoContainer container) throws PicoCompositionException {
        final Set<Constructor> conflicts = new HashSet<Constructor>();
        final Set<List<Class>> unsatisfiableDependencyTypes = new HashSet<List<Class>>();
        if (sortedMatchingConstructors == null) {
            sortedMatchingConstructors = getSortedMatchingConstructors();
        }
        Constructor<T> greediestConstructor = null;
        int lastSatisfiableConstructorSize = -1;
        Class unsatisfiedDependencyType = null;
        for (final Constructor<T> sortedMatchingConstructor : sortedMatchingConstructors) {
            boolean failedDependency = false;
            Class[] parameterTypes = sortedMatchingConstructor.getParameterTypes();
            Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(parameterTypes);

            // remember: all constructors with less arguments than the given parameters are filtered out already
            for (int j = 0; j < currentParameters.length; j++) {
                // check whether this constructor is statisfiable
                Class boxed = box(parameterTypes[j]);
                boolean un = useNames();
                if (currentParameters[j].isResolvable(container, this, boxed,
                    new SingleMemberInjectorParameterName(sortedMatchingConstructor, j), un)) {
                    continue;
                }
                unsatisfiableDependencyTypes.add(Arrays.asList(parameterTypes));
                unsatisfiedDependencyType = box(parameterTypes[j]);
                failedDependency = true;
                break;
            }

            if (greediestConstructor != null && parameterTypes.length != lastSatisfiableConstructorSize) {
                if (conflicts.isEmpty()) {
                    // we found our match [aka. greedy and satisfied]
                    return greediestConstructor;
                } else {
                    // fits although not greedy
                    conflicts.add(sortedMatchingConstructor);
                }
            } else if (!failedDependency && lastSatisfiableConstructorSize == parameterTypes.length) {
                // satisfied and same size as previous one?
                conflicts.add(sortedMatchingConstructor);
                conflicts.add(greediestConstructor);
            } else if (!failedDependency) {
                greediestConstructor = sortedMatchingConstructor;
                lastSatisfiableConstructorSize = parameterTypes.length;
            }
        }
        if (!conflicts.isEmpty()) {
            throw new PicoCompositionException(conflicts.size() + " satisfiable constructors is too many for '"+getComponentImplementation()+"'. Constructor List:" + conflicts.toString().replace(getComponentImplementation().getName(),"<init>").replace("public <i","<i"));
        } else if (greediestConstructor == null && !unsatisfiableDependencyTypes.isEmpty()) {
            throw new UnsatisfiableDependenciesException(this, unsatisfiedDependencyType, unsatisfiableDependencyTypes, container);
        } else if (greediestConstructor == null) {
            // be nice to the user, show all constructors that were filtered out
            final Set<Constructor> nonMatching = new HashSet<Constructor>();
            for (Constructor constructor : getConstructors()) {
                nonMatching.add(constructor);
            }
            throw new PicoCompositionException("Either the specified parameters do not match any of the following constructors: " + nonMatching.toString() + "; OR the constructors were not accessible for '" + getComponentImplementation().getName() + "'");
        }
        return greediestConstructor;
    }

    public T getComponentInstance(final PicoContainer container) throws PicoCompositionException {
        if (instantiationGuard == null) {
            instantiationGuard = new ThreadLocalCyclicDependencyGuard<T>() {
                public T run() {
                    Constructor<T> constructor;
                    try {
                        constructor = getGreediestSatisfiableConstructor(guardedContainer);
                    } catch (AmbiguousComponentResolutionException e) {
                        e.setComponent(getComponentImplementation());
                        throw e;
                    }
                    ComponentMonitor<T> componentMonitor = currentMonitor();
                    try {
                        Object[] parameters = getMemberArguments(guardedContainer, constructor);
                        constructor = componentMonitor.instantiating(container, ConstructorInjector.this, constructor);
                        long startTime = System.currentTimeMillis();
                        T inst = newInstance(constructor, parameters);
                        componentMonitor.instantiated(container,
                                                      ConstructorInjector.this,
                                                      constructor, inst, parameters, System.currentTimeMillis() - startTime);
                        return inst;
                    } catch (InvocationTargetException e) {
                        componentMonitor.instantiationFailed(container, ConstructorInjector.this, constructor, e);
                        if (e.getTargetException() instanceof RuntimeException) {
                            throw (RuntimeException) e.getTargetException();
                        } else if (e.getTargetException() instanceof Error) {
                            throw (Error) e.getTargetException();
                        }
                        throw new PicoCompositionException(e.getTargetException());
                    } catch (InstantiationException e) {
                        return caughtInstantiationException(componentMonitor, constructor, e, container);
                    } catch (IllegalAccessException e) {
                        return caughtIllegalAccessException(componentMonitor, constructor, e, container);

                    }
                }
            };
        }
        instantiationGuard.setGuardedContainer(container);
        return instantiationGuard.observe(getComponentImplementation());
    }

    protected Object[] getMemberArguments(PicoContainer container, final Constructor ctor) {
        return super.getMemberArguments(container, ctor, ctor.getParameterTypes());
    }

    private List<Constructor<T>> getSortedMatchingConstructors() {
        List<Constructor<T>> matchingConstructors = new ArrayList<Constructor<T>>();
        Constructor<T>[] allConstructors = getConstructors();
        // filter out all constructors that will definately not match
        for (Constructor<T> constructor : allConstructors) {
            if ((parameters == null || constructor.getParameterTypes().length == parameters.length) && (constructor.getModifiers() & Modifier.PUBLIC) != 0) {
                matchingConstructors.add(constructor);
            }
        }
        // optimize list of constructors moving the longest at the beginning
        if (parameters == null) {        	
            Collections.sort(matchingConstructors, new Comparator<Constructor>() {
                public int compare(Constructor arg0, Constructor arg1) {
                    return arg1.getParameterTypes().length - arg0.getParameterTypes().length;
                }
            });
        }
        return matchingConstructors;
    }

    private Constructor<T>[] getConstructors() {
        return AccessController.doPrivileged(new PrivilegedAction<Constructor<T>[]>() {
            public Constructor<T>[] run() {
                return (Constructor<T>[]) getComponentImplementation().getDeclaredConstructors();
            }
        });
    }

    public void verify(final PicoContainer container) throws PicoCompositionException {
        if (verifyingGuard == null) {
            verifyingGuard = new ThreadLocalCyclicDependencyGuard() {
                public Object run() {
                    final Constructor constructor = getGreediestSatisfiableConstructor(guardedContainer);
                    final Class[] parameterTypes = constructor.getParameterTypes();
                    final Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(parameterTypes);
                    for (int i = 0; i < currentParameters.length; i++) {
                        currentParameters[i].verify(container, ConstructorInjector.this, box(parameterTypes[i]),
                                                    new SingleMemberInjectorParameterName(constructor, i), useNames());
                    }
                    return null;
                }
            };
        }
        verifyingGuard.setGuardedContainer(container);
        verifyingGuard.observe(getComponentImplementation());
    }

    public String getDescriptor() {
        return "ConstructorInjector-";
    }


}
