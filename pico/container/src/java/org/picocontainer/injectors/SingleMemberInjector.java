/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.annotations.Bind;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;

import com.thoughtworks.paranamer.CachingParanamer;

/**
 * Injection will happen in a single member function on the component.
 *
 * @author Paul Hammant 
 * 
 */
public abstract class SingleMemberInjector<T> extends AbstractInjector<T> {

    private transient CachingParanamer paranamer = new CachingParanamer();

    public SingleMemberInjector(Object componentKey,
                                Class componentImplementation,
                                Parameter[] parameters,
                                ComponentMonitor monitor,
                                LifecycleStrategy lifecycleStrategy, boolean useNames) {
        super(componentKey, componentImplementation, parameters, monitor, lifecycleStrategy, useNames);
    }

    protected CachingParanamer getParanamer() {
        return paranamer;
    }

    @SuppressWarnings("unchecked")
    protected Object[] getMemberArguments(PicoContainer container, final AccessibleObject member, final Type[] parameterTypes, final Annotation[] bindings) {
        boxParameters(parameterTypes);
        Object[] result = new Object[parameterTypes.length];
        Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(parameterTypes);

        for (int i = 0; i < currentParameters.length; i++) {
            result[i] = getParameter(container, member, i, parameterTypes[i], bindings[i], currentParameters[i]);
        }

        return result;
    }

    private void boxParameters(Type[] parameterTypes) {
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypes[i] = box(parameterTypes[i]);
        }
    }

    private Object getParameter(PicoContainer container, AccessibleObject member, int i, Type parameterType, Annotation binding, Parameter currentParameter) {
        ParameterNameBinding expectedNameBinding = new ParameterNameBinding(paranamer, getComponentImplementation(), member, i);
        Object result = currentParameter.resolveInstance(container, this, parameterType, expectedNameBinding, useNames(), binding);
        if (result == null && useNames()) {
            result = container.getComponent(expectedNameBinding.getName());
        }
        if (result == null && !isNullParamAllowed(member, i)) {
            throw new ParameterCannotBeNullException(i, member, expectedNameBinding.getName());
        }
        return result;
    }

    protected boolean isNullParamAllowed(AccessibleObject member, int i) {
        return false;
    }

    protected Annotation[] getBindings(Annotation[][] annotationss) {
        Annotation[] retVal = new Annotation[annotationss.length];
        for (int i = 0; i < annotationss.length; i++) {
            Annotation[] annotations = annotationss[i];
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().getAnnotation(Bind.class) != null) {
                    retVal[i] = annotation;
                    break;
                }
            }
        }
        return retVal;
    }

    public static class ParameterCannotBeNullException extends PicoCompositionException {
        private final String name;
        private ParameterCannotBeNullException(int ix, AccessibleObject member, String name) {
            super("Parameter " + ix + " of '" + member + "' named '" + name + "' cannot be null");
            this.name = name;
        }
        public String getParameterName() {
            return name;
        }
    }

}
