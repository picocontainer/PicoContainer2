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
import org.picocontainer.ParameterName;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.behaviors.CachingBehavior;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.thoughtworks.paranamer.ParameterNamesNotFoundException;

/**
 * Instantiates components using Constructor Injection.
 * <em>
 * Note that this class doesn't cache instances. If you want caching,
 * use a {@link CachingBehavior} around this one.
 * </em>
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @author Zohar Melamed
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @version $Revision$
 */
public class ConstructorInjector extends AbstractInjector {
    private transient List<Constructor> sortedMatchingConstructors;
    private transient ThreadLocalCyclicDependencyGuard instantiationGuard;
    private transient ParanamerProxy paranamer;


    /**
     * Creates a ConstructorInjectionComponentAdapter
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
    public ConstructorInjector(final Object componentKey, final Class componentImplementation, Parameter[] parameters, ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy) throws  NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters, monitor, lifecycleStrategy);
    }

    protected Constructor getGreediestSatisfiableConstructor(PicoContainer container) throws PicoCompositionException {
        final Set<Constructor> conflicts = new HashSet<Constructor>();
        final Set<List<Class>> unsatisfiableDependencyTypes = new HashSet<List<Class>>();
        if (sortedMatchingConstructors == null) {
            sortedMatchingConstructors = getSortedMatchingConstructors();
        }
        Constructor greediestConstructor = null;
        int lastSatisfiableConstructorSize = -1;
        Class unsatisfiedDependencyType = null;
        for (final Constructor sortedMatchingConstructor : sortedMatchingConstructors) {
            boolean failedDependency = false;
            Class[] parameterTypes = sortedMatchingConstructor.getParameterTypes();
            Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(parameterTypes);

            // remember: all constructors with less arguments than the given parameters are filtered out already
            for (int j = 0; j < currentParameters.length; j++) {
                // check wether this constructor is statisfiable
                final int j1 = j;
                if (currentParameters[j].isResolvable(container, this, parameterTypes[j],
                                                      makeParameterName(sortedMatchingConstructor, j1))) {
                    continue;
                }
                unsatisfiableDependencyTypes.add(Arrays.asList(parameterTypes));
                unsatisfiedDependencyType = parameterTypes[j];
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

    private ParameterName makeParameterName(final Constructor sortedMatchingConstructor, final int j1) {
        return new ParameterName() {
            public String getParameterName() {
                String[] names = lookupParameterNames(sortedMatchingConstructor);
                if (names.length != 0) {
                    return names[j1];
                }
                return null;
            }
        };
    }

    private static final String[] EMPTY_NAMES = new String[]{};
    private static final String COMMA = ",";
    private static final String SPACE = " ";

    // copied from DefaultParanamer
    private String[] lookupParameterNames(AccessibleObject methodOrCtor) {
        Class[] types = null;
        Class declaringClass = null;
        String name = null;
        if (methodOrCtor instanceof Method) {
            Method method = (Method) methodOrCtor;
            types = method.getParameterTypes();
            name = method.getName();
            declaringClass = method.getDeclaringClass();
        } else {
            Constructor constructor = (Constructor) methodOrCtor;
            types = constructor.getParameterTypes();
            declaringClass = constructor.getDeclaringClass();
            name = "<init>";
        }

        if (types.length == 0) {
            // faster ?
            return EMPTY_NAMES;
        }
        final String parameterTypeNames = getParameterTypeNamesCSV(types);
        final String[] names = getParameterNames(declaringClass, parameterTypeNames, name + SPACE);

        if (names != null) {
            return names;
        }
        createIfNeededParanamerProxy();
        if (paranamer != null) {
            return paranamer.lookupParameterNames((Constructor)methodOrCtor);
        }
        return new String[0];
    }
    // copied from DefaultParanamer
    private static String getParameterTypeNamesCSV(Class[] parameterTypes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < parameterTypes.length; i++) {
            sb.append(parameterTypes[i].getName());
            if (i < parameterTypes.length - 1) {
                sb.append(COMMA);
            }
        }
        return sb.toString();
    }
    // copied from DefaultParanamer
    private static String[] getParameterNames(Class declaringClass, String parameterTypes, String prefix) {
        String data = getParameterListResource(declaringClass);
        String line = findFirstMatchingLine(data, prefix + parameterTypes);
        String[] parts = line.split(SPACE);
        // assumes line structure: constructorName parameterTypes parameterNames
        if (parts.length == 3 && parts[1].equals(parameterTypes)) {
            String parameterNames = parts[2];
            return parameterNames.split(COMMA);
        }
        return null;
    }
    // copied from DefaultParanamer
    private static String getParameterListResource(Class declaringClass) {
        try {
            Field field = declaringClass.getDeclaredField("__PARANAMER_DATA");
            if(!Modifier.isStatic(field.getModifiers()) || !field.getType().equals(String.class)) {
                return null;
            }
            return (String) field.get(null);
        } catch (NoSuchFieldException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }
    // copied from DefaultParanamer
    private static String findFirstMatchingLine(String data, String prefix) {
        if (data == null) {
            return "";
        }
        int ix = data.indexOf(prefix);
        if (ix > 0) {
            int iy = data.indexOf("\n", ix);
            if(iy >0) {
                return data.substring(ix,iy);
            }
        }
        return "";
    }





    private void createIfNeededParanamerProxy() {
        if (paranamer == null) {
            try {
                paranamer = new ParanamerProxy();
            } catch (NoClassDefFoundError e) {
            }
        }
    }


    public Object getComponentInstance(final PicoContainer container) throws PicoCompositionException {
        if (instantiationGuard == null) {
            instantiationGuard = new ThreadLocalCyclicDependencyGuard() {
                public Object run() {
                    Constructor constructor;
                    try {
                        constructor = getGreediestSatisfiableConstructor(guardedContainer);
                    } catch (AmbiguousComponentResolutionException e) {
                        e.setComponent(getComponentImplementation());
                        throw e;
                    }
                    ComponentMonitor componentMonitor = currentMonitor();
                    try {
                        Object[] parameters = getConstructorArguments(guardedContainer, constructor);
                        constructor = componentMonitor.instantiating(container, ConstructorInjector.this, constructor);
                        long startTime = System.currentTimeMillis();
                        Object inst = newInstance(constructor, parameters);
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

    protected Object[] getConstructorArguments(PicoContainer container, final Constructor ctor) {
        Class[] parameterTypes = ctor.getParameterTypes();
        Object[] result = new Object[parameterTypes.length];
        Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(parameterTypes);

        for (int i = 0; i < currentParameters.length; i++) {
            final int i1 = i;
            result[i] = currentParameters[i].resolveInstance(container, this, parameterTypes[i], new ParameterName() {
                public String getParameterName() {
                    createIfNeededParanamerProxy();
                    if (paranamer != null) {
                        String[] strings = lookupParameterNames(ctor);
                        return strings.length == 0 ? "" : strings[i1];
                    }
                    return null;
                }
            });
        }
        return result;
    }

    private List<Constructor> getSortedMatchingConstructors() {
        List<Constructor> matchingConstructors = new ArrayList<Constructor>();
        Constructor[] allConstructors = getConstructors();
        // filter out all constructors that will definately not match
        for (Constructor constructor : allConstructors) {
            if ((parameters == null || constructor.getParameterTypes().length == parameters.length) && (constructor.getModifiers() & Modifier.PUBLIC) != 0) {
                matchingConstructors.add(constructor);
            }
        }
        // optimize list of constructors moving the longest at the beginning
        if (parameters == null) {
            Collections.sort(matchingConstructors, new Comparator() {
                public int compare(Object arg0, Object arg1) {
                    return ((Constructor) arg1).getParameterTypes().length - ((Constructor) arg0).getParameterTypes().length;
                }
            });
        }
        return matchingConstructors;
    }

    private Constructor[] getConstructors() {
        return (Constructor[]) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return getComponentImplementation().getDeclaredConstructors();
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
                        final int i1 = i;
                        currentParameters[i].verify(container, ConstructorInjector.this, parameterTypes[i], new ParameterName() {
                    public String getParameterName() {

                        String[] names = lookupParameterNames(constructor);
                        if (names.length != 0) {
                            return names[i1];
                        }
                        return null;
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

}
