/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.picocontainer.Parameter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ParameterName;
import org.picocontainer.PicoContainer;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Member;

/** @author Paul Hammant */
public abstract class SingleMemberInjector extends AbstractInjector {

    private transient ParanamerProxy paranamer;

    private static final String[] EMPTY_NAMES = new String[]{};
    private static final String COMMA = ",";
    private static final String SPACE = " ";

    public SingleMemberInjector(Object componentKey,
                                Class componentImplementation,
                                Parameter[] parameters,
                                ComponentMonitor monitor,
                                LifecycleStrategy lifecycleStrategy) {
        super(componentKey, componentImplementation, parameters, monitor, lifecycleStrategy);
    }


    private void createIfNeededParanamerProxy() {
        if (paranamer == null) {
            try {
                paranamer = new ParanamerProxy();
            } catch (NoClassDefFoundError e) {
            }
        }
    }


    // copied from DefaultParanamer
    protected String[] lookupParameterNames(AccessibleObject methodOrCtor) {
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

    protected Class box(Class parameterType) {
        if (parameterType.isPrimitive()) {
            if (parameterType == Integer.TYPE) {
                return Integer.class;
            } else if (parameterType == Boolean.TYPE) {
                return Boolean.class;
            }
        }
        return parameterType;
    }


    protected Object[] getMemberArguments(PicoContainer container, final AccessibleObject member, final Class[] parameterTypes) {
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypes[i] = box(parameterTypes[i]);

        }
        Object[] result = new Object[parameterTypes.length];
        Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(parameterTypes);

        for (int i = 0; i < currentParameters.length; i++) {
            result[i] = currentParameters[i].resolveInstance(container, this, parameterTypes[i],
                                                             new MemberInjectorParameterName(member, i, useNames));
        }
        return result;
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

    protected class MemberInjectorParameterName implements ParameterName {
        private final AccessibleObject member;
        private final int index;
        private final boolean useNames;
        private String name;

        public MemberInjectorParameterName(AccessibleObject member, int index, boolean useNames) {
            this.member = member;
            this.index = index;
            this.useNames = useNames;
        }

        public String getName() {
            if (name != null) {
                return name;
            }
            createIfNeededParanamerProxy();
            if (paranamer != null) {
                String[] strings = lookupParameterNames(member);
                name = strings.length == 0 ? "" : strings[index];
            }
            return name;
        }

        public boolean useNames() {
            return useNames;
        }
    }




}
