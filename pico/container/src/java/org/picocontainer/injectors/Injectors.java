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

import org.picocontainer.injectors.AdaptingInjection;
import org.picocontainer.injectors.SetterInjection;
import org.picocontainer.injectors.ConstructorInjection;
import org.picocontainer.injectors.AnnotatedMethodInjection;
import org.picocontainer.InjectionFactory;

public class Injectors {

    public static InjectionFactory adaptiveDI() {
        return new AdaptingInjection();
    }

    public static InjectionFactory SDI() {
        return new SetterInjection();
    }

    public static InjectionFactory CDI() {
        return new ConstructorInjection();
    }

    public static InjectionFactory annotatedMethodDI() {
        return new AnnotatedMethodInjection();
    }

    public static InjectionFactory annotatedFieldDI() {
        return new AnnotatedFieldInjection();
    }

}
