package org.picocontainer.injectors;

import org.picocontainer.InjectionFactory;
import org.picocontainer.injectors.AdaptiveInjectionFactory;
import org.picocontainer.injectors.SetterInjectionFactory;
import org.picocontainer.injectors.ConstructorInjectionFactory;
import org.picocontainer.injectors.MethodAnnotationInjectionFactory;

public class Injectors {

    public static InjectionFactory anyDI() {
        return new AdaptiveInjectionFactory();
    }

    public static InjectionFactory SDI() {
        return new SetterInjectionFactory();
    }

    public static InjectionFactory CDI() {
        return new ConstructorInjectionFactory();
    }

    public static InjectionFactory MADI() {
        return new MethodAnnotationInjectionFactory();
    }

    public static InjectionFactory FADI() {
        return new FieldAnnotationInjectionFactory();
    }

}
