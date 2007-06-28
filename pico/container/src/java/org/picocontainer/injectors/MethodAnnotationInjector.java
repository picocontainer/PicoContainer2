package org.picocontainer.injectors;

import org.picocontainer.Parameter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.annotations.Inject;
import org.picocontainer.LifecycleStrategy;

import java.lang.reflect.Method;

public class MethodAnnotationInjector extends SetterInjector {

    public MethodAnnotationInjector(Object key, Class impl, Parameter[] parameters, ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy) {
        super(key, impl, parameters, monitor, lifecycleStrategy);
    }

    protected final boolean isInjectorMethod(Method method) {
        return method.getAnnotation(Inject.class) != null;
    }
}
