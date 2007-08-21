/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

import java.util.Map;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/** @author Paul Hammant */
public class Intercepted<T> extends HiddenImplementation {
    private final Map<Class, Object> pres;
    private final Map<Class, Object> posts;
    private final Interception.Interceptor interceptor;


    public Intercepted(ComponentAdapter delegate, Map<Class, Object> pres,  Map<Class, Object> posts,  Interception.Interceptor interceptor) {
        super(delegate);
        this.pres = pres;
        this.posts = posts;
        this.interceptor = interceptor;
    }

    protected Object invokeMethod(Method method, Object[] args, PicoContainer container) throws Throwable {
        Object componentInstance = getDelegate(ComponentAdapter.class).getComponentInstance(container);
        try {
            interceptor.clear();
            interceptor.instance(componentInstance);
            Object pre = pres.get(method.getDeclaringClass());
            if (pre != null) {
                Object rv =  method.invoke(pre, args);
                if (interceptor.isVetoed()) {
                    return rv;
                }
            }
            Object result = method.invoke(componentInstance, args);
            interceptor.setOriginalRetVal(result);
            Object post = posts.get(method.getDeclaringClass());
            if (post != null) {
                Object rv = method.invoke(post, args);
                if (interceptor.isOverridden()) {
                    return rv;
                }
            }
            return result;
        } catch (final InvocationTargetException ite) {
            throw ite.getTargetException();
        }
    }

    protected String getName() {
        return "Intercepted:";
    }
}
