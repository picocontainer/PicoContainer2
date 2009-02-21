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

import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;

public class ParameterNameBinding implements NameBinding {
    private final Class impl;
    private final AccessibleObject member;
    private final int index;
    private final Paranamer paranamer;

    private String name;

    public ParameterNameBinding(Paranamer paranamer, Class impl, AccessibleObject member, int index) {
        this.impl = impl;
        this.member = member;
        this.paranamer = paranamer;
        this.index = index;
    }

    public String getName() {
        if (name != null) {
            return name;
        }
        String[] strings = null;
        if (member instanceof Constructor) {
            strings = paranamer.lookupParameterNames((Constructor)member);
        } else {
            strings = paranamer.lookupParameterNames((Method)member);
        }
        name = strings.length == 0 ? "" : strings[index];
        return name;
    }
}

