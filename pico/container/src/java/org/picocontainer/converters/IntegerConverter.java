package org.picocontainer.converters;

import org.picocontainer.converters.Converter;


/**
 * Converts strings to integer types.
 * @author Paul Hammant, Michael Rimov
 */
class IntegerConverter implements Converter<Integer> {

    /** {@inheritDoc} **/
    public Integer convert(String paramValue) {
        return Integer.valueOf(paramValue);
    }
}
