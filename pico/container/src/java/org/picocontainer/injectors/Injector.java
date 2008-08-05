package org.picocontainer.injectors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Parameter;

/**
 * convenience class providing static methods to conveniently create injectors
 * ( like org.junit.Assert )
 *
 * @author Konstantin Pribluda
 */
public class Injector {
    /**
     * Constructor injector that uses no monitor and no lifecycle adapter.  This is a more
     * convenient constructor for use when instantiating a constructor injector directly.
     *
     * @param componentKey            the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters              the parameters used for initialization
     */

    public static ComponentAdapter constructor(final Object componentKey, final Class<?> componentImplementation, Parameter... parameters) {
        return new ConstructorInjector(componentKey, componentImplementation, parameters);
    }

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
    public static ComponentAdapter constructor(final Object componentKey, final Class componentImplementation, Parameter[] parameters, ComponentMonitor monitor,
                                               LifecycleStrategy lifecycleStrategy, boolean useNames) throws AbstractInjector.NotConcreteRegistrationException {
        return new ConstructorInjector(componentKey, componentImplementation, parameters, monitor, lifecycleStrategy, useNames);
    }

    /**
     * Creates a ConstructorInjector
     *
     * @param componentKey            the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters              the parameters to use for the initialization
     * @param monitor                 the component monitor used by this addAdapter
     * @param lifecycleStrategy       the component lifecycle strategy used by this addAdapter
     * @param useNames                use argument names when looking up dependencies
     * @param rememberChosenCtor      remember the chosen constructor (to speed up second/subsequent calls)
     * @throws org.picocontainer.injectors.AbstractInjector.NotConcreteRegistrationException
     *                              if the implementation is not a concrete class.
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    public static ComponentAdapter constructor(final Object componentKey, final Class componentImplementation, Parameter[] parameters, ComponentMonitor monitor,
                                               LifecycleStrategy lifecycleStrategy, boolean useNames, boolean rememberChosenCtor) throws AbstractInjector.NotConcreteRegistrationException {
        return new ConstructorInjector(componentKey, componentImplementation, parameters, monitor,
                lifecycleStrategy, useNames, rememberChosenCtor);
    }


}
