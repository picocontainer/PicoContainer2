package org.picocontainer.converters;

import org.picocontainer.converters.Converter;


/**
 * Converts strings to boolean types.
 * @author Paul Hammant, Michael Rimov
 */
class FloatConverter implements Converter<Float> {

    public Float convert(String paramValue) {
        return Float.valueOf(paramValue);
    }
}
