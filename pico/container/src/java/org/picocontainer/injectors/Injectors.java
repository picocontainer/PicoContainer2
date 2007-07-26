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

import org.picocontainer.injectors.AdaptiveInjectionFactory;
import org.picocontainer.injectors.SetterInjectionFactory;
import org.picocontainer.injectors.ConstructorInjectionFactory;
import org.picocontainer.injectors.MethodAnnotationInjectionFactory;
import org.picocontainer.InjectionFactory;

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
