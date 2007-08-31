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
import java.util.HashMap;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.Serializable;

/** @author Paul Hammant */
public class Intercepted<T> extends HiddenImplementation {

    private final Map<Class, Object> pres = new HashMap<Class, Object>();
    private final Map<Class, Object> posts = new HashMap<Class, Object>();
    private Controller controller = new ControllerWrapper(new InterceptorThreadLocal());

    public Intercepted(ComponentAdapter delegate) {
        super(delegate);
    }

    public void addPreInvocation(Class type, Object interceptor) {
        pres.put(type, interceptor);
    }

    public void addPostInvocation(Class type, Object interceptor) {
        posts.put(type, interceptor);
    }

    protected Object invokeMethod(Method method, Object[] args, PicoContainer container) throws Throwable {
        Object componentInstance = getDelegate().getComponentInstance(container);
        try {
            controller.clear();
            controller.instance(componentInstance);
            Object pre = pres.get(method.getDeclaringClass());
            if (pre != null) {
                Object rv =  method.invoke(pre, args);
                if (controller.isVetoed()) {
                    return rv;
                }
            }
            Object result = method.invoke(componentInstance, args);
            controller.setOriginalRetVal(result);
            Object post = posts.get(method.getDeclaringClass());
            if (post != null) {
                Object rv = method.invoke(post, args);
                if (controller.isOverridden()) {
                    return rv;
                }
            }
            return result;
        } catch (final InvocationTargetException ite) {
            throw ite.getTargetException();
        }
    }

    public Controller getController() {
        return controller;
    }

    public static class InterceptorThreadLocal extends ThreadLocal implements Serializable {
        protected Object initialValue() {
            return new ControllerImpl();
        }
    }

    public interface Controller {
        void veto();

        void clear();

        boolean isVetoed();

        void setOriginalRetVal(Object retVal);

        boolean isOverridden();

        void instance(Object instance);

        Object getOriginalRetVal();

        void override();
    }

    public static class ControllerImpl implements Controller {
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

    public class ControllerWrapper implements Controller {
        private final ThreadLocal threadLocal;

        public ControllerWrapper(ThreadLocal threadLocal) {
            this.threadLocal = threadLocal;
        }

        public void veto() {
            ((Controller) threadLocal.get()).veto();
        }

        public void clear() {
            ((Controller) threadLocal.get()).clear();
        }

        public boolean isVetoed() {
            return ((Controller) threadLocal.get()).isVetoed();
        }

        public void setOriginalRetVal(Object retVal) {
            ((Controller) threadLocal.get()).setOriginalRetVal(retVal);
        }

        public Object getOriginalRetVal() {
            return ((Controller) threadLocal.get()).getOriginalRetVal();
        }

        public boolean isOverridden() {
            return ((Controller) threadLocal.get()).isOverridden();
        }

        public void instance(Object instance) {
            ((Controller) threadLocal.get()).instance(instance);

        }

        public void override() {
            ((Controller) threadLocal.get()).override();
        }
    }

    public String getDescriptor() {
        return "Intercepted";
    }
}
