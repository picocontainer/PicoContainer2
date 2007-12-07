/*****************************************************************************
 * Copyright (C) PicoContainer Committers. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaibe                                            *
 *****************************************************************************/
package org.picocontainer;

import java.lang.annotation.Annotation;

/** @author Paul Hammant */
public class BindKey {
    private final Class type;
    private final Class<? extends Annotation> annotation;

    public BindKey(Class type, Class<? extends Annotation> annotation) {
        this.type = type;
        this.annotation = annotation;
    }

    public Class getType() {
        return type;
    }

    public Class<? extends Annotation> getAnnotation() {
        return annotation;
    }

    public String toString() {
        return type.getName() + ":" + annotation.getName();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BindKey bindKey = (BindKey)o;

        if (!annotation.equals(bindKey.annotation)) return false;
        if (!type.equals(bindKey.type)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = type.hashCode();
        result = 31 * result + annotation.hashCode();
        return result;
    }

    public static BindKey bindKey(Class type, Class<? extends Annotation> annotation) {
        return new BindKey(type, annotation);
    }

}
