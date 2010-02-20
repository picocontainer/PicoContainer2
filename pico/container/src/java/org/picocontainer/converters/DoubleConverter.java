package org.picocontainer.converters;

import org.picocontainer.converters.Converter;


/**
 * Converts strings to double-precision floating point values..
 * @author Paul Hammant, Michael Rimov
 */
class DoubleConverter implements Converter<Double> {

    /**
     * {@inheritDoc}
     */
    public Double convert(String paramValue) {
        return Double.valueOf(paramValue);
    }
}
