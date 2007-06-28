package org.picocontainer.injectors;

import org.picocontainer.InjectionFactory;
import org.picocontainer.injectors.AdaptiveInjectionFactory;
import org.picocontainer.injectors.SetterInjectionFactory;
import org.picocontainer.injectors.ConstructorInjectionFactory;
import org.picocontainer.injectors.MethodAnnotationInjectionFactory;

public class Injectors {

    public static InjectionFactory adaptiveDI() {
        return new AdaptiveInjectionFactory();
    }

    public static InjectionFactory SDI() {
        return new SetterInjectionFactory();
    }

    public static InjectionFactory CDI() {
        return new ConstructorInjectionFactory();
    }

    public static InjectionFactory methodAnnotationDI() {
        return new MethodAnnotationInjectionFactory();
    }

    public static InjectionFactory fieldAnnotationDI() {
        return new FieldAnnotationInjectionFactory();
    }

}
