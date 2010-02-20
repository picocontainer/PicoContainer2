package org.picocontainer.converters;

import org.picocontainer.converters.Converter;

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
