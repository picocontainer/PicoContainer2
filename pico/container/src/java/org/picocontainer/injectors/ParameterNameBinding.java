/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.picocontainer.NameBinding;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Member;


public class ParameterNameBinding implements NameBinding {
    private final AccessibleObject member;
    private final int index;

    private String name;

    public ParameterNameBinding(AccessibleObject member, int index) {
        this.member = member;
        this.index = index;
    }

    public String getName() {
        if (name != null) {
            return name;
        }
        if (member instanceof Constructor) {
            name = ((Constructor) member).getParameters()[index].getName();
        } else {
            name = ((Method) member).getParameters()[index].getName();
        }
        return name;
    }
}

