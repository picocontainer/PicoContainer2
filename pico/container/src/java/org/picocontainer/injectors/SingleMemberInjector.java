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
import org.picocontainer.ParameterName;
import org.picocontainer.PicoContainer;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;

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



    /**
     * TODO: shall it box everything?  a bit too few for me (konstantin)
     */
    protected Class box(Class parameterType) {
        if (parameterType.isPrimitive()) {
            String parameterTypeName = parameterType.getName();
            if (parameterTypeName == "int") {
                return Integer.class;
            } else if (parameterTypeName == "boolean") {
                return Boolean.class;
            } else if (parameterTypeName == "long") {
                return Long.class;
            } else if (parameterTypeName == "float") {
                return Float.class;
            } else if (parameterTypeName == "double") {
                return Double.class;
            } else if (parameterTypeName == "char") {
                return Character.class;
            } else if (parameterTypeName == "byte") {
                return Byte.class;
            } else if (parameterTypeName == "short") {
                return Short.class;
            }
        }
        return parameterType;
    }


    @SuppressWarnings("unchecked")
	protected Object[] getMemberArguments(PicoContainer container, final AccessibleObject member, final Class[] parameterTypes) {
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypes[i] = box(parameterTypes[i]);

        }
        Object[] result = new Object[parameterTypes.length];
        Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(parameterTypes);

        for (int i = 0; i < currentParameters.length; i++) {
            result[i] = currentParameters[i].resolveInstance(container, this, parameterTypes[i],
                                                             new SingleMemberInjectorParameterName(member, i),
                                                             useNames());
        }
        return result;
    }



    protected class SingleMemberInjectorParameterName implements ParameterName {
        private final AccessibleObject member;
        private final int index;
        private String name;

        public SingleMemberInjectorParameterName(AccessibleObject member, int index) {
            this.member = member;
            this.index = index;
        }

        public String getName() {
            if (name != null) {
                return name;
            }
            String[] strings = null;
            if(paranamer.areParameterNamesAvailable(getComponentImplementation(),"<init>") != Paranamer.PARAMETER_NAMES_FOUND) {
                paranamer.switchtoAsm();
            }
            if (member instanceof Constructor) {
                strings = paranamer.lookupParameterNames((Constructor)member);
            } else {
                strings = paranamer.lookupParameterNames((Method)member);
            }
            name = strings.length == 0 ? "" : strings[index];
            return name;
        }
    }
}
