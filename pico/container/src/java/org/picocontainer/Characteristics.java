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
    private static final String _NONE = "none";
    private static final String _CONSTRUCTOR = "constructor";
    private static final String _SETTER = "setter";
    private static final String _CACHE = "cache";
    private static final String _JMX = "jmx";
    private static final String _THREAD_SAFE = "thread-safe";
    private static final String _HIDE_IMPL = "hide-impl";
    private static final String FALSE = "false";
    private static final String TRUE = "true";

    public static final Properties CDI = immutable(_INJECTION, _CONSTRUCTOR);

    public static final Properties SDI = immutable(_INJECTION, _SETTER);

    public static final Properties NO_CACHE = immutable(_CACHE, FALSE);

    public static final Properties CACHE = immutable(_CACHE, TRUE);

    public static final Properties NO_JMX = immutable(_JMX, FALSE);

    public static final Properties THREAD_SAFE = immutable(_THREAD_SAFE, TRUE);
    
    public static final Properties SINGLE = CACHE;
    
    public static final Properties HIDE_IMPL = immutable(_HIDE_IMPL, TRUE);

    public static final Properties NO_HIDE_IMPL = immutable(_HIDE_IMPL, FALSE);
    
    public static final Properties NONE = immutable(_NONE, "");

    private static Properties immutable(String name, String value) {
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
