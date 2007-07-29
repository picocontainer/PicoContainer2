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

import org.picocontainer.injectors.AdaptiveInjection;
import org.picocontainer.injectors.SetterInjectionFactory;
import org.picocontainer.injectors.ConstructorInjectionFactory;
import org.picocontainer.injectors.AnnotatatedMethodInjection;
import org.picocontainer.InjectionFactory;

public class Injectors {

    public static InjectionFactory adaptiveDI() {
        return new AdaptiveInjection();
    }

    public static InjectionFactory SDI() {
        return new SetterInjectionFactory();
    }

    public static InjectionFactory CDI() {
        return new ConstructorInjectionFactory();
    }

    public static InjectionFactory annotatedMethodDI() {
        return new AnnotatatedMethodInjection();
    }

    public static InjectionFactory annotatedFieldDI() {
        return new AnnotatatedFieldInjection();
    }

}
