/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer;

import java.lang.reflect.Type;

/**
 * A facade for a collection of converters that provides string-to-type conversions. 
 * @author Paul Hammant, Michael Rimov
 */
public interface Converters {
    
    /**
     * Returns true if the set of converters can convert between strings and the target
     * type.
     * @param type
     * @return true if the target type can convert.
     */
    boolean canConvert(Type type);
    
    /**
     * Converts a particular string value into the target type.
     * @param paramValue
     * @param type
     * @return the target object.
     */
    Object convert(String paramValue, Type type);
}
