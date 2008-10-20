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
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypes[i] = box(parameterTypes[i]);

        }
        Object[] result = new Object[parameterTypes.length];
        Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(parameterTypes);

        for (int i = 0; i < currentParameters.length; i++) {
            result[i] = currentParameters[i].resolveInstance(container, this, parameterTypes[i],
                new ParameterNameBinding(paranamer, getComponentImplementation(), member, i), useNames(), bindings[i]);
            if (result[i] == null) {
                throw new PicoCompositionException("Parameter " + i + " of '" + member + "' cannot be null");
            }
        }

        return result;
    }

    protected Annotation[] getBindings(Annotation[][] annotationss) {
        Annotation[] retVal = new Annotation[annotationss.length];
        for (int i = 0; i < annotationss.length; i++) {
            Annotation[] annotations = annotationss[i];
            for (int j = 0; j < annotations.length; j++) {
                Annotation annotation = annotations[j];
                if (annotation.annotationType().getAnnotation(Bind.class) != null) {
                    retVal[i] = annotation;
                    break;
                }
            }
        }
        return retVal;
    }


}
