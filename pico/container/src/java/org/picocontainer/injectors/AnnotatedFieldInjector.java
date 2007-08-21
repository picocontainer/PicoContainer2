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
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Parameter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

public class AnnotatedFieldInjector extends IterativeInjector {

    private final Class injectionAnnotation;

    public AnnotatedFieldInjector(Object key,
                                  Class impl,
                                  Parameter[] parameters,
                                  ComponentMonitor componentMonitor,
                                  LifecycleStrategy lifecycleStrategy, Class injectionAnnotation) {

        super(key, impl, parameters, componentMonitor, lifecycleStrategy);
        this.injectionAnnotation = injectionAnnotation;
    }

    protected void initializeInjectionMembersAndTypeLists() {
        injectionMembers = new ArrayList<Member>();
        final List<Class> typeList = new ArrayList<Class>();
        final Field[] fields = getFields();
        for (final Field field : fields) {
            if (isAnnotatedForInjection(field)) {
                injectionMembers.add(field);
                typeList.add(field.getType());
            }
        }
        injectionTypes = typeList.toArray(new Class[0]);
    }

    protected boolean isAnnotatedForInjection(Field field) {
        return field.getAnnotation(injectionAnnotation) != null;
    }

    private Field[] getFields() {
        return (Field[]) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return getComponentImplementation().getDeclaredFields();
            }
        });
    }


    protected void injectIntoMember(Member member, Object componentInstance, Object toInject)
        throws IllegalAccessException, InvocationTargetException {
        Field field = (Field) member;
        field.setAccessible(true);
        field.set(componentInstance, toInject);
    }

    public String toString() {
        return "FieldInjector-" + super.toString();
    }

}
