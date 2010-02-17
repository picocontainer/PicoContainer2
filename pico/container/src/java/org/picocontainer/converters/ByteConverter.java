package org.picocontainer.converters;

import org.picocontainer.Converting;

import java.lang.reflect.Type;

/**
 * Converts
 *
 */
class ByteConverter implements Converter {
    
    /** {@inheritDoc} **/
    public Object convert(String paramValue) {
        return Byte.valueOf(paramValue);
    }
}
