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
import java.io.Serializable;

/** @author Paul Hammant */
public class Intercepted<T> extends HiddenImplementation {
    private final Map<Class, Object> pres;
    private final Map<Class, Object> posts;

    private InterceptorWrapper interceptor = new InterceptorWrapper(new InterceptorThreadLocal());

    public Intercepted(ComponentAdapter delegate, Map<Class, Object> pres,  Map<Class, Object> posts) {
        super(delegate);
        this.pres = pres;
        this.posts = posts;
        this.interceptor = interceptor;
    }

    protected Object invokeMethod(Method method, Object[] args, PicoContainer container) throws Throwable {
        Object componentInstance = getDelegate().getComponentInstance(container);
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

    public InterceptorWrapper getInterceptor() {
        return interceptor;
    }

    public static class InterceptorThreadLocal extends ThreadLocal implements Serializable {
        protected Object initialValue() {
            return new InterceptorImpl();
        }
    }

    public interface Interceptor {
        void veto();

        void clear();

        boolean isVetoed();

        void setOriginalRetVal(Object retVal);

        boolean isOverridden();

        void instance(Object instance);

        Object getOriginalRetVal();

        void override();
    }

    public static class InterceptorImpl implements Interceptor {
        private boolean vetoed;
        private Object retVal;
        private boolean overridden;
        private Object instance;

        public void veto() {
            vetoed = true;
        }

        public void clear() {
            vetoed = false;
            overridden = false;
            retVal = null;
            instance = null;
        }

        public boolean isVetoed() {
            return vetoed;
        }
        public void setOriginalRetVal(Object retVal) {
            this.retVal = retVal;
        }

        public Object getOriginalRetVal() {
            return retVal;
        }

        public boolean isOverridden() {
            return overridden;
        }

        public void instance(Object instance) {
            this.instance = instance;
        }

        public void override() {
            overridden = true;
        }
    }

    public class InterceptorWrapper implements Interceptor {
        private final ThreadLocal threadLocal;

        public InterceptorWrapper(ThreadLocal threadLocal) {
            this.threadLocal = threadLocal;
        }

        public void veto() {
            ((Interceptor) threadLocal.get()).veto();
        }

        public void clear() {
            ((Interceptor) threadLocal.get()).clear();
        }

        public boolean isVetoed() {
            return ((Interceptor) threadLocal.get()).isVetoed();
        }

        public void setOriginalRetVal(Object retVal) {
            ((Interceptor) threadLocal.get()).setOriginalRetVal(retVal);
        }

        public Object getOriginalRetVal() {
            return ((Interceptor) threadLocal.get()).getOriginalRetVal();
        }

        public boolean isOverridden() {
            return ((Interceptor) threadLocal.get()).isOverridden();
        }

        public void instance(Object instance) {
            ((Interceptor) threadLocal.get()).instance(instance);

        }

        public void override() {
            ((Interceptor) threadLocal.get()).override();
        }
    }



    protected String getName() {
        return "Intercepted:";
    }
}
