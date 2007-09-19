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

/**
 * Collection of immutable properties, holding behaviour characteristics.
 * 
 * @author Paul Hammant
 */
public final class Characteristics {

    private static final String _INJECTION = "injection";
    private static final String _NONE = "none";
    private static final String _CONSTRUCTOR = "constructor";
    private static final String _METHOD = "method";
    private static final String _SETTER = "setter";
    private static final String _CACHE = "cache";
    private static final String _JMX = "jmx";
    private static final String _SYNCHRONIZING = "synchronizing";
    private static final String _LOCKING = "locking";
    private static final String _HIDE_IMPL = "hide-impl";
    private static final String _PROPERTY_APPLYING = "property-applying";
    private static final String _AUTOMATIC = "automatic";
    private static final String _USE_NAMES = "use-parameter-names";    

    private static final String FALSE = "false";
    private static final String TRUE = "true";

    public static final Properties CDI = immutable(_INJECTION, _CONSTRUCTOR);

    public static final Properties SDI = immutable(_INJECTION, _SETTER);

    public static final Properties METHOD_INJECTION = immutable(_INJECTION, _METHOD);

    public static final Properties NO_CACHE = immutable(_CACHE, FALSE);

    public static final Properties CACHE = immutable(_CACHE, TRUE);

    public static final Properties NO_JMX = immutable(_JMX, FALSE);

    public static final Properties SYNCHRONIZE = immutable(_SYNCHRONIZING, TRUE);

    public static final Properties LOCK = immutable(_LOCKING, TRUE);

    public static final Properties SINGLE = CACHE;
    
    public static final Properties HIDE_IMPL = immutable(_HIDE_IMPL, TRUE);

    public static final Properties NO_HIDE_IMPL = immutable(_HIDE_IMPL, FALSE);
    
    public static final Properties NONE = immutable(_NONE, "");

    public static final Properties PROPERTY_APPLYING = immutable(_PROPERTY_APPLYING, TRUE);

    public static final Properties AUTOMATIC = immutable(_AUTOMATIC, TRUE);

    public static final Properties USE_NAMES = immutable(_USE_NAMES, TRUE);

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
