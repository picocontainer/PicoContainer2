/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.aop;

import org.aopalliance.intercept.MethodInterceptor;

/**
 * Represents the collection of aspects (pointuts + advice) to be applied to a
 * Pico container. Provides methods for registering mixin and interceptor
 * advice. Advice can be applied to all components in the container that match a
 * pointcut, or advice can be applied to just one component. Advice objects may
 * themselves be components in the container, with dependencies on other
 * components.
 *
 * @author Stephen Molitor
 * @version $Revision$
 */
public interface AspectsContainer {

    /**
     * Registers container scoped interceptor advice. The advice will be applied
     * to all components in the container whose class satisfies the
     * <code>classPointcut</code>. The interceptor will only intercept
     * methods that match the <code>methodPointcut</code>.
     *
     * @param classPointcut  classes to apply the interceptor to.
     * @param methodPointcut methods to apply the interceptor to.
     * @param interceptor    the interceptor advice object.
     */
    void registerInterceptor(ClassPointcut classPointcut, MethodPointcut methodPointcut, MethodInterceptor interceptor);

    /**
     * Registers component scoped interceptor advice. The advice will be applied
     * to all components in the container whose key satisfies
     * <code>componentPointcut</code>. The interceptor will only intercept
     * methods that match the <code>methodPointcut</code>.
     *
     * @param componentPointcut components to apply the interceptor to.
     * @param methodPointcut    methods to apply the interceptor to.
     * @param interceptor       the interceptor advice object.
     */
    void registerInterceptor(ComponentPointcut componentPointcut, MethodPointcut methodPointcut,
                             MethodInterceptor interceptor);

    /**
     * Registers container supplied container scoped interceptor advice. The
     * interceptor advice object itself is a component in the container,
     * specified by <code>interceptorComponentKey</code>. The advice will be
     * applied to all components in the container whose class satisfies the
     * <code>classPointcut</code>. The interceptor will only intercept
     * methods that match the <code>methodPointcut</code>.
     *
     * @param classPointcut           classes to apply the interceptor to.
     * @param methodPointcut          methods to apply the interceptor to.
     * @param interceptorComponentKey the interceptor component key.
     */
    void registerInterceptor(ClassPointcut classPointcut, MethodPointcut methodPointcut, Object interceptorComponentKey);

    /**
     * Registers component scoped interceptor advice. The interceptor advice
     * object itself is a component in the container, specified by the
     * <code>interceptorComponentKey</code>. The advice will be applied to
     * all components in the container whose key satisfies
     * <code>componentPointcut</code>. The interceptor will only intercept
     * methods that match the <code>methodPointcut</code>.
     *
     * @param componentPointcut       components to apply the interceptor to.
     * @param methodPointcut          methods to apply the interceptor to.
     * @param interceptorComponentKey the interceptor component key.
     */
    void registerInterceptor(ComponentPointcut componentPointcut, MethodPointcut methodPointcut,
                             Object interceptorComponentKey);

    /**
     * Registers container scoped mixin advice. The mixin will be added to all
     * components in the container whose class satisfies the
     * <code>classPointcut</code>.
     * <p/>
     * If a component of type <code>mixinClass</code> has been registered in
     * the container, that component will be used as the mixin. Otherwise a new
     * object of type <code>mixinClass</code> will be instantiated each time
     * the mixin is applied to a component. Any dependencies the mixin has will
     * be supplied from components in the container, or, if there are no
     * dependencies, the default constructor will be invoked to instantiate the
     * mixin.
     *
     * @param classPointcut classes to add mixin to.
     * @param interfaces    interfaces the mixin implements.
     * @param mixinClass    the mixin implementation.
     */
    void registerMixin(ClassPointcut classPointcut, Class[] interfaces, Class mixinClass);

    /**
     * Registers component scoped mixin advice. The mixin will be added to all
     * components in the container whose key satisfies the
     * <code>componentPointcut</code>.
     *
     * @param componentPointcut classes to add mixin to.
     * @param interfaces        interfaces the mixin implements.
     * @param mixinClass        the mixin implementation.
     * @see AspectsContainer#registerMixin(ClassPointcut, Class[], Class) for
     *      details on how <code>mixinClass</code> gets instantiated.
     */
    void registerMixin(ComponentPointcut componentPointcut, Class[] interfaces, Class mixinClass);

    /**
     * Registers container scoped mixin advice. The mixin will be added to all
     * components in the container whose class satisfies the
     * <code>classPointcut</code>. Convenience method that uses all
     * interfaces implemented by the mixin class.
     *
     * @param classPointcut classes to add mixin to.
     * @param mixinClass    the mixin implementation.
     * @see AspectsContainer#registerMixin(ClassPointcut, Class[], Class) for
     *      details on how <code>mixinClass</code> gets instantiated.
     */
    void registerMixin(ClassPointcut classPointcut, Class mixinClass);

    /**
     * Registers component scoped mixin advice. The mixin will be added to all
     * components in the container whose key satisfies the
     * <code>componentPointcut</code>. Convenience method that uses all
     * interfaces implemented by the mixin class.
     *
     * @param componentPointcut classes to add mixin to.
     * @param mixinClass        the mixin implementation.
     * @see AspectsContainer#registerMixin(ClassPointcut, Class[], Class) for
     *      details on how <code>mixinClass</code> gets instantiated.
     */
    void registerMixin(ComponentPointcut componentPointcut, Class mixinClass);

    /**
     * Adds interfaces to classes picked by the class pointcut.
     * <p/>
     * This can be handy when you want to add an aggregate helper interface that
     * extends all the mixin interfaces added, to avoid the need for casting.
     * Note that the interfaces will <i>not </i> be added if no advice
     * (interceptor or mixin) has been applied to the component.
     *
     * @param classPointcut classes to add interfaces to.
     * @param interfaces    the interfaces to add.
     */
    void registerInterfaces(ClassPointcut classPointcut, Class[] interfaces);

    /**
     * Adds interfaces to components picked by the component pointcut.
     *
     * @param componentPointcut components to add interfaces to.
     * @param interfaces        the interfaces to add.
     * @see AspectsContainer#registerInterfaces(ClassPointcut, Class[]) for
     *      notes on using this method.
     */
    void registerInterfaces(ComponentPointcut componentPointcut, Class[] interfaces);

    /**
     * Produces a pointcuts factory that can be used to create pointcuts to be
     * used in aspects registered with this <code>AspectsContainer</code>.
     * Note that you are not limited to pointcuts produced by this factory; any
     * pointcut that implements the appropriate <code>ClassPointcut</code>,
     * <code>MethodPointcut</code> or <code>ComponentPointcut</code> will
     * work.
     *
     * @return a pointcuts factory.
     */
    PointcutsFactory getPointcutsFactory();

}