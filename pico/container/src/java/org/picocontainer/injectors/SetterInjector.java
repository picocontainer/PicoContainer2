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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
 * <a href="http://picocontainer.org/setter-injection.html">Setter Injection</a>.
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
public class SetterInjector extends PostInstantiationInjector {

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


    protected boolean isInjectorMethod(Method method) {
        String methodName = method.getName();
        return methodName.length() >= getInjectorPrefix().length() + 1 && methodName.startsWith(getInjectorPrefix()) && Character.isUpperCase(methodName.charAt(getInjectorPrefix().length()));
    }

    protected String getInjectorPrefix() {
        return "set";
    }

}
