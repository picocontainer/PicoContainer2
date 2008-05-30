/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.picocontainer.aop.dynaop;

import dynaop.Aspects;
import dynaop.Interceptor;
import dynaop.InterceptorFactory;
import dynaop.ProxyFactory;
import dynaop.util.Classes;
import org.aopalliance.intercept.MethodInterceptor;
import org.picocontainer.PicoContainer;
import org.picocontainer.aop.AspectsManager;
import org.picocontainer.aop.ClassPointcut;
import org.picocontainer.aop.ComponentPointcut;
import org.picocontainer.aop.MethodPointcut;
import org.picocontainer.aop.PointcutsFactory;

import java.lang.reflect.Method;

/**
 * An <code>AspectsManager</code> implemented using dynaop.
 *
 * @author Stephen Molitor
 */
public class DynaopAspectsManager implements AspectsManager {

    private final Aspects containerAspects;
    private final PointcutsFactory pointcutsFactory;
    private final ContainerLoader containerLoader = new ContainerLoader();
    private final PicoContainer container = PicoContainerProxy.create(containerLoader);
    private final ComponentAspectsCollection componentAspects = new ComponentAspectsCollection();

    /**
     * Creates a new <code>DynaopAspectsManager</code> that will used the
     * given <code>dynaop.Aspects</code> object and pointcuts factory. This
     * constructor might be useful if the <code>containerAspects</code> object
     * contains aspects already configured via dynaop's API, perhaps using
     * dynaop's BeanShell configuration mechanism.
     *
     * @param containerAspects the <code>dyanop.Aspects</code> object used to
     *                         contain the aspects.
     * @param pointcutsFactory the pointcuts factory.
     */
    public DynaopAspectsManager(Aspects containerAspects, PointcutsFactory pointcutsFactory) {
        this.containerAspects = containerAspects;
        this.pointcutsFactory = pointcutsFactory;
    }

    /**
     * Creates a new <code>DynaopAspectsManager</code> that will used the
     * given <code>dynaop.Aspects</code> object. This constructor might be
     * useful if the <code>containerAspects</code> object contains aspects
     * already configured via dynaop's API, perhaps using dynaop's BeanShell
     * configuration mechanism.
     *
     * @param containerAspects the <code>dyanop.Aspects</code> object used to
     *                         contain the aspects.
     */
    public DynaopAspectsManager(Aspects containerAspects) {
        this(containerAspects, new DynaopPointcutsFactory());
    }

    /**
     * Creates a new <code>DynaopAspectsManager</code> that will use the given
     * pointcuts factory.
     *
     * @param pointcutsFactory the pointcuts factory.
     */
    public DynaopAspectsManager(PointcutsFactory pointcutsFactory) {
        this(new Aspects(), pointcutsFactory);
    }

    /**
     * Creates a new <code>DynaopAspectsManager</code>.
     */
    public DynaopAspectsManager() {
        this(new Aspects());
    }

    public void registerInterceptor(ClassPointcut classPointcut, MethodPointcut methodPointcut,
                                    Object interceptorComponentKey) {
        containerAspects.interceptor(getClassPointcut(classPointcut), getMethodPointcut(methodPointcut),
                createInterceptorFactory(interceptorComponentKey));
    }

    public void registerInterceptor(ClassPointcut classPointcut, MethodPointcut methodPointcut,
                                    MethodInterceptor interceptor) {
        containerAspects.interceptor(getClassPointcut(classPointcut), getMethodPointcut(methodPointcut),
                createInterceptor(interceptor));
    }

    public void registerInterceptor(ComponentPointcut componentPointcut, MethodPointcut methodPointcut,
                                    Object interceptorComponentKey) {
        componentAspects.add(new InterceptorComponentAspect(componentPointcut, getMethodPointcut(methodPointcut),
                createInterceptorFactory(interceptorComponentKey)));
    }

    public void registerInterceptor(ComponentPointcut componentPointcut, MethodPointcut methodPointcut,
                                    MethodInterceptor interceptor) {
        componentAspects.add(new InterceptorComponentAspect(componentPointcut, getMethodPointcut(methodPointcut),
                createInterceptor(interceptor)));
    }

    public void registerMixin(ClassPointcut classPointcut, Class mixinClass) {
        registerMixin(classPointcut, Classes.getAllInterfaces(mixinClass), mixinClass);
    }

    public void registerMixin(ClassPointcut classPointcut, Class[] interfaces, Class mixinClass) {
        containerAspects.mixin(getClassPointcut(classPointcut), interfaces, new ContainerSuppliedMixinFactory(container, mixinClass));
    }

    public void registerMixin(ComponentPointcut componentPointcut, Class mixinClass) {
        registerMixin(componentPointcut, Classes.getAllInterfaces(mixinClass), mixinClass);
    }

    public void registerMixin(ComponentPointcut componentPointcut, Class[] interfaces, Class mixinClass) {
        componentAspects.add(new MixinComponentAspect(componentPointcut, interfaces, new ContainerSuppliedMixinFactory(container, mixinClass)));
    }

    public void registerInterfaces(ClassPointcut classPointcut, Class[] interfaces) {
        containerAspects.interfaces(getClassPointcut(classPointcut), interfaces);
    }

    public void registerInterfaces(ComponentPointcut componentPointcut, Class[] interfaces) {
        componentAspects.add(new InterfacesComponentAspect(componentPointcut, interfaces));
    }

    public PointcutsFactory getPointcutsFactory() {
        return pointcutsFactory;
    }

    public Object applyAspects(Object componentKey, Object component, PicoContainer container) {
        containerLoader.setContainer(container);
        Aspects aspects = componentAspects.registerAspects(componentKey, containerAspects);
        return new ProxyFactory(aspects).wrap(component);
    }

    private dynaop.ClassPointcut getClassPointcut(final ClassPointcut classPointcut) {
        if (classPointcut instanceof dynaop.ClassPointcut) {
            return (dynaop.ClassPointcut) classPointcut;
        }
        return new dynaop.ClassPointcut() {
            public boolean picks(Class clazz) {
                return classPointcut.picks(clazz);
            }
        };
    }

    private dynaop.MethodPointcut getMethodPointcut(final MethodPointcut methodPointcut) {
        if (methodPointcut instanceof dynaop.MethodPointcut) {
            return (dynaop.MethodPointcut) methodPointcut;
        }
        return new dynaop.MethodPointcut() {
            public boolean picks(Method method) {
                return methodPointcut.picks(method);
            }
        };
    }

    private Interceptor createInterceptor(MethodInterceptor methodInterceptor) {
        return new MethodInterceptorAdapter(methodInterceptor);
    }

    private InterceptorFactory createInterceptorFactory(Object interceptorComponent) {
        return new ContainerSuppliedInterceptorFactory(container, interceptorComponent);
    }

}