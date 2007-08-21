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
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.io.Serializable;


/** @author Paul Hammant */
public class Interception extends AbstractBehaviorFactory {

    private final Map<Class, Object> pres = new HashMap<Class, Object>();
    private final Map<Class, Object> posts = new HashMap<Class, Object>();

    private InterceptorThreadLocal interceptorThreadLocal = new InterceptorThreadLocal();
    private InterceptorWrapper interceptorWrapper = new InterceptorWrapper();

    public void pre(Class type, Object interceptor) {
        pres.put(type, interceptor);
    }

    public void post(Class type, Object interceptor) {
        posts.put(type, interceptor);
    }

    public <T> ComponentAdapter<T> createComponentAdapter(ComponentMonitor componentMonitor,
                                                          LifecycleStrategy lifecycleStrategy,
                                                          Properties componentProperties,
                                                          Object componentKey,
                                                          Class<T> componentImplementation,
                                                          Parameter... parameters) throws PicoCompositionException {
        return new Intercepted(super.createComponentAdapter(componentMonitor,
				lifecycleStrategy, componentProperties, componentKey,
				componentImplementation, parameters), pres, posts, interceptorWrapper);
    }

    public static class InterceptorThreadLocal extends ThreadLocal implements Serializable {
        protected Object initialValue() {
            return new InterceptorImpl();
        }
    }

    public Interceptor interceptor() {
        return interceptorWrapper;
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
        public void veto() {
            ((Interceptor) interceptorThreadLocal.get()).veto();
        }

        public void clear() {
            ((Interceptor) interceptorThreadLocal.get()).clear();
        }

        public boolean isVetoed() {
            return ((Interceptor) interceptorThreadLocal.get()).isVetoed();
        }

        public void setOriginalRetVal(Object retVal) {
            ((Interceptor) interceptorThreadLocal.get()).setOriginalRetVal(retVal);
        }

        public Object getOriginalRetVal() {
            return ((Interceptor) interceptorThreadLocal.get()).getOriginalRetVal();
        }

        public boolean isOverridden() {
            return ((Interceptor) interceptorThreadLocal.get()).isOverridden();
        }

        public void instance(Object instance) {
            ((Interceptor) interceptorThreadLocal.get()).instance(instance);

        }

        public void override() {
            ((Interceptor) interceptorThreadLocal.get()).override();
        }
    }


}
