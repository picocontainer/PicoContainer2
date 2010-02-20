package org.picocontainer.converters;

import org.picocontainer.converters.Converter;


/**
 * Converts strings to long-integer types.
 * @author Paul Hammant, Michael Rimov
 */
class LongConverter implements Converter<Long> {

    /** {@inheritDoc} **/
    public Long convert(String paramValue) {
        return Long.valueOf(paramValue);
    }
}
