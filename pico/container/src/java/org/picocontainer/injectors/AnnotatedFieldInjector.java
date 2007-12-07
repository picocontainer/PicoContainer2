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
import org.picocontainer.ParameterName;
import org.picocontainer.annotations.Bind;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

/**
 * Injection happens after instantiation, and through fields marked as injection points via an Annotation.
 * The default annotation of org.picocontainer.annotations.@Inject can be overwridden.
 */
public class AnnotatedFieldInjector extends IterativeInjector {

    private final Class injectionAnnotation;

    public AnnotatedFieldInjector(Object key,
                                  Class impl,
                                  Parameter[] parameters,
                                  ComponentMonitor componentMonitor,
                                  LifecycleStrategy lifecycleStrategy, Class injectionAnnotation, boolean useNames) {

        super(key, impl, parameters, componentMonitor, lifecycleStrategy, useNames);
        this.injectionAnnotation = injectionAnnotation;
    }

    protected void initializeInjectionMembersAndTypeLists() {
        injectionMembers = new ArrayList<AccessibleObject>();
        List<Annotation> bindingIds = new ArrayList<Annotation>();
        final List<Class> typeList = new ArrayList<Class>();
        final Field[] fields = getFields();
        for (final Field field : fields) {
            if (isAnnotatedForInjection(field)) {
                injectionMembers.add(field);
                typeList.add(box(field.getType()));
                bindingIds.add(getBinding(field));
            }
        }
        injectionTypes = typeList.toArray(new Class[0]);
        bindings = bindingIds.toArray(new Annotation[0]);
    }

    private Annotation getBinding(Field field) {
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(Bind.class)) {
                return annotation;
            }
        }
        return null;
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


    protected void injectIntoMember(AccessibleObject member, Object componentInstance, Object toInject)
        throws IllegalAccessException, InvocationTargetException {
        Field field = (Field) member;
        field.setAccessible(true);
        field.set(componentInstance, toInject);
    }

    public String getDescriptor() {
        return "FieldInjector-";
    }

    protected ParameterName makeParameterNameImpl(final AccessibleObject member) {
        return new ParameterName() {
            public String getName() {
                return ((Field) member).getName();
            }
        };
    }
}
