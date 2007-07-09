/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer;

import java.util.Properties;

public final class Characteristics {

    private static final String _INJECTION = "injection";
    private static final String _CONSTRUCTOR = "constructor";
    private static final String _SETTER = "setter";
    private static final String _CACHE = "cache";
    private static final String _NOJMX = "no-jmx";
    private static final String _THREAD_SAFE = "thread-safe";
    private static final String _HIDE = "hide-implementations";
    private static final String FALSE = "FALSE";
    private static final String TRUE = "TRUE";

    public static final Properties CDI = makeProps(_INJECTION, _CONSTRUCTOR);

    public static final Properties SDI = makeProps(_INJECTION, _SETTER);

    public static final Properties NOCACHE = makeProps(_CACHE, FALSE);

    public static final Properties CACHE = makeProps(_CACHE, TRUE);

    public static final Properties NOJMX = makeProps(_NOJMX, TRUE);

    public static final Properties THREAD_SAFE = makeProps(_THREAD_SAFE, TRUE);
    
    public static final Properties SINGLE = CACHE;
    
    public static final Properties HIDE = makeProps(_HIDE, TRUE);

    private static Properties makeProps(String name, String value) {
        return new ImmutableProperties(name, value);
    }

    public static class ImmutableProperties extends Properties {

        public ImmutableProperties(String name, String value) {
            super.setProperty(name, value);
        }

        public Object remove(Object o) {
            throw new UnsupportedOperationException();
        }

        public synchronized Object setProperty(String string, String string1) {
            throw new UnsupportedOperationException();
        }
    }

}
