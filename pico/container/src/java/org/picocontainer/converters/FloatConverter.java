package org.picocontainer.converters;


/**
 * Converts strings to boolean types.
 * @author Paul Hammant, Michael Rimov
 */
class FloatConverter implements Converter<Float> {

    public Float convert(String paramValue) {
        return Float.valueOf(paramValue);
    }
}
