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

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;


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
        name = lookupParameterName(member, index);
        if (name.isBlank()) {
            if (member instanceof Constructor) {
                name = ((Constructor) member).getParameters()[index].getName();
            } else {
                name = ((Method) member).getParameters()[index].getName();
            }
        }
        return name;
    }

    public String lookupParameterName(AccessibleObject methodOrCtor, int ix) {
        Executable executable = (Executable) methodOrCtor;

        Annotation[][] anns = executable.getParameterAnnotations();

        for (int j = 0; j < anns[ix].length; j++) {
            Annotation ann = anns[ix][j];
            if (isNamed(ann)) {
                return getNamedValue(ann);
            }
        }

        return "";
    }

    private String getNamedValue(Annotation ann) {
        if ("javax.inject.Named".equals(ann.annotationType().getName())) {
            return ((Named) ann).value();
        } else {
            return null;
        }
    }

    private boolean isNamed(Annotation ann) {
        if ("javax.inject.Named".equals(ann.annotationType().getName())) {
            return ann instanceof Named;
        } else {
            return false;
        }
    }

}

