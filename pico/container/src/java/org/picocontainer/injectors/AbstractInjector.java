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
import org.picocontainer.PicoVisitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ObjectReference;
import org.picocontainer.adapters.AbstractAdapter;
import org.picocontainer.monitors.DelegatingComponentMonitor;
import org.picocontainer.parameters.ComponentParameter;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Set;

/**
 * This ComponentAdapter will instantiate a new object for each call to
 * {@link org.picocontainer.ComponentAdapter#getComponentInstance(PicoContainer)}.
 * That means that when used with a PicoContainer, getComponent will
 * return a new object each time.
 *
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @version $Revision$
 * @since 1.0
 */
public abstract class AbstractInjector extends AbstractAdapter implements LifecycleStrategy {
    /** The cycle guard for the verification. */ 
    protected transient ThreadLocalCyclicDependencyGuard verifyingGuard;
    /** The parameters to use for initialization. */ 
    protected transient Parameter[] parameters;
 
    /** The strategy used to control the lifecycle */
    protected LifecycleStrategy lifecycleStrategy;
    
    /**
     * Constructs a new ComponentAdapter for the given key and implementation. 
     * @param componentKey the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters the parameters to use for the initialization
     * @param monitor the component monitor used by this ComponentAdapter
     * @param lifecycleStrategy the lifecycle strategy used by this ComponentAdapter
     * @throws org.picocontainer.injectors.AbstractInjector.NotConcreteRegistrationException if the implementation is not a concrete class
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    protected AbstractInjector(Object componentKey, Class componentImplementation, Parameter[] parameters,
                                            ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy) {
        super(componentKey, componentImplementation, monitor);
        checkConcrete();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                if(parameters[i] == null) {
                    throw new NullPointerException("Parameter " + i + " is null");
                }
            }
        }
        this.parameters = parameters;
        this.lifecycleStrategy = lifecycleStrategy;
    }

    /**
     * Constructs a new ComponentAdapter for the given key and implementation. 
     * @param componentKey the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters the parameters to use for the initialization
     * @param monitor the component monitor used by this ComponentAdapter
     * @throws org.picocontainer.injectors.AbstractInjector.NotConcreteRegistrationException if the implementation is not a concrete class
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    protected AbstractInjector(Object componentKey, Class componentImplementation,
                                            Parameter[] parameters,
                                            ComponentMonitor monitor) {
        this(componentKey, componentImplementation, parameters, monitor, new StartableLifecycleStrategy(monitor));
    }

    /**
     * Constructs a new ComponentAdapter for the given key and implementation. 
     * @param componentKey the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters the parameters to use for the initialization
     * @throws org.picocontainer.injectors.AbstractInjector.NotConcreteRegistrationException if the implementation is not a concrete class.
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    protected AbstractInjector(Object componentKey, Class componentImplementation, Parameter[] parameters) {
        this(componentKey, componentImplementation, parameters, new DelegatingComponentMonitor());
    }
    
    private void checkConcrete() throws NotConcreteRegistrationException {
        // Assert that the component class is concrete.
        boolean isAbstract = (getComponentImplementation().getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT;
        if (getComponentImplementation().isInterface() || isAbstract) {
            throw new NotConcreteRegistrationException(getComponentImplementation());
        }
    }

    /**
     * Create default parameters for the given types.
     *
     * @param parameters the parameter types
     * @return the array with the default parameters.
     */
    protected Parameter[] createDefaultParameters(Class[] parameters) {
        Parameter[] componentParameters = new Parameter[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            componentParameters[i] = ComponentParameter.DEFAULT;
        }
        return componentParameters;
    }

    public abstract void verify(PicoContainer container) throws PicoCompositionException;

    public void accept(PicoVisitor visitor) {
        super.accept(visitor);
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                parameter.accept(visitor);
            }
        }
    }
  
    public void start(Object component) {
        lifecycleStrategy.start(component);
    }

    public void stop(Object component) {
        lifecycleStrategy.stop(component);
    }

    public void dispose(Object component) {
        lifecycleStrategy.dispose(component);
    }

    public boolean hasLifecycle(Class type) {
        return lifecycleStrategy.hasLifecycle(type);
    }

    /**
     * Instantiate an object with given parameters and respect the accessible flag.
     * 
     * @param constructor the constructor to use
     * @param parameters the parameters for the constructor 
     * @return the new object.
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    protected Object newInstance(Constructor constructor, Object[] parameters) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        return constructor.newInstance(parameters);
    }

    /**
     * Find and return the greediest satisfiable constructor.
     * 
     * @param container the PicoContainer to resolve dependencies.
     * @return the found constructor.
     * @throws PicoCompositionException
     * @throws UnsatisfiableDependenciesException
     * @throws NotConcreteRegistrationException
     */
    protected abstract Constructor getGreediestSatisfiableConstructor(PicoContainer container) throws
                                                                                               PicoCompositionException;


    /**
     * Abstract utility class to detect recursion cycles.
     * Derive from this class and implement {@link ThreadLocalCyclicDependencyGuard#run}.
     * The method will be called by  {@link ThreadLocalCyclicDependencyGuard#observe}. Select
     * an appropriate guard for your scope. Any {@link ObjectReference} can be
     * used as long as it is initialized with  <code>Boolean.FALSE</code>.
     *
     * @author J&ouml;rg Schaible
     * @since 1.1
     */
    static abstract class ThreadLocalCyclicDependencyGuard extends ThreadLocal {

        protected PicoContainer guardedContainer;

        protected Object initialValue() {
            return Boolean.FALSE;
        }

        /**
         * Derive from this class and implement this function with the functionality
         * to observe for a dependency cycle.
         *
         * @return a value, if the functionality result in an expression,
         *      otherwise just return <code>null</code>
         */
        public abstract Object run();

        /**
         * Call the observing function. The provided guard will hold the {@link Boolean} value.
         * If the guard is already <code>Boolean.TRUE</code> a {@link CyclicDependencyException}
         * will be  thrown.
         *
         * @param stackFrame the current stack frame
         * @return the result of the <code>run</code> method
         */
        public final Object observe(Class stackFrame) {
            if (Boolean.TRUE.equals(get())) {
                throw new CyclicDependencyException(stackFrame);
            }
            Object result = null;
            try {
                set(Boolean.TRUE);
                result = run();
            } catch (final CyclicDependencyException e) {
                e.push(stackFrame);
                throw e;
            } finally {
                set(Boolean.FALSE);
            }
            return result;
        }

        public void setGuardedContainer(PicoContainer container) {
            this.guardedContainer = container;
        }

    }

    public static class CyclicDependencyException extends PicoCompositionException {
        private final List<Class> stack;

        /**
         * @since 1.1
         * @param element
         */
        public CyclicDependencyException(Class element) {
            super((Throwable)null);
            this.stack = new LinkedList<Class>();
            push(element);
        }

        /**
         * @since 1.1
         * @param element
         */
        public void push(Class element) {
            stack.add(element);
        }

        public Class[] getDependencies() {
            return stack.toArray(new Class[stack.size()]);
        }

        public String getMessage() {
            return "Cyclic dependency: " + stack.toString();
        }
    }

    /**
     * Exception that is thrown as part of the introspection. Raised if a PicoContainer cannot resolve a
     * type dependency because the registered {@link org.picocontainer.ComponentAdapter}s are not
     * distinct.
     *
     * @author Paul Hammant
     * @author Aslak Helles&oslash;y
     * @author Jon Tirs&eacute;n
     * @since 1.0
     */
    public static final class AmbiguousComponentResolutionException extends PicoCompositionException {
        private Class component;
        private final Class ambiguousDependency;
        private final Object[] ambiguousComponentKeys;


        /**
         * Construct a new exception with the ambigous class type and the ambiguous component keys.
         *
         * @param ambiguousDependency the unresolved dependency type
         * @param componentKeys the ambiguous keys.
         */
        public AmbiguousComponentResolutionException(Class ambiguousDependency, Object[] componentKeys) {
            super("");
            this.ambiguousDependency = ambiguousDependency;
            this.ambiguousComponentKeys = new Class[componentKeys.length];
            System.arraycopy(componentKeys, 0, ambiguousComponentKeys, 0, componentKeys.length);
        }

        /**
         * @return Returns a string containing the unresolved class type and the ambiguous keys.
         */
        public String getMessage() {
            StringBuffer msg = new StringBuffer();
            msg.append(component);
            msg.append(" has ambiguous dependency on ");
            msg.append(ambiguousDependency);
            msg.append(", ");
            msg.append("resolves to multiple classes: ");
            msg.append(Arrays.asList(getAmbiguousComponentKeys()));
            return msg.toString();
        }

        /**
         * @return Returns the ambiguous component keys as array.
         */
        public Object[] getAmbiguousComponentKeys() {
            return ambiguousComponentKeys;
        }

        public void setComponent(Class component) {
            this.component = component;
        }
    }

    /**
     * Exception thrown when some of the component's dependencies are not satisfiable.
     *
     * @author Aslak Helles&oslash;y
     * @author Mauro Talevi
     * @version $Revision$
     */
    public static class UnsatisfiableDependenciesException extends PicoCompositionException {

        private final ComponentAdapter instantiatingComponentAdapter;
        private final Set unsatisfiableDependencies;
        private final Class unsatisfiedDependencyType;
        private final PicoContainer leafContainer;

        public UnsatisfiableDependenciesException(ComponentAdapter instantiatingComponentAdapter,
                                                  Class unsatisfiedDependencyType, Set unsatisfiableDependencies,
                                                  PicoContainer leafContainer) {
            super(instantiatingComponentAdapter.getComponentImplementation().getName() + " has unsatisfied dependency: " + unsatisfiedDependencyType
                    +" among unsatisfiable dependencies: "+unsatisfiableDependencies + " where " + leafContainer
                    + " was the leaf container being asked for dependencies.");
            this.instantiatingComponentAdapter = instantiatingComponentAdapter;
            this.unsatisfiableDependencies = unsatisfiableDependencies;
            this.unsatisfiedDependencyType = unsatisfiedDependencyType;
            this.leafContainer = leafContainer;
        }

        public ComponentAdapter getUnsatisfiableComponentAdapter() {
            return instantiatingComponentAdapter;
        }

        public Set getUnsatisfiableDependencies() {
            return unsatisfiableDependencies;
        }

        public Class getUnsatisfiedDependencyType() {
            return unsatisfiedDependencyType;
        }

        public PicoContainer getLeafContainer() {
            return leafContainer;
        }

    }

    /**
     * @author Aslak Hellesoy
     * @version $Revision$
     */
    public static class NotConcreteRegistrationException extends PicoCompositionException {
        private final Class componentImplementation;

        public NotConcreteRegistrationException(Class componentImplementation) {
            super("Bad Access: '" + componentImplementation.getName() + "' is not instantiable");
            this.componentImplementation = componentImplementation;
        }

        public Class getComponentImplementation() {
            return componentImplementation;
        }
    }





}
