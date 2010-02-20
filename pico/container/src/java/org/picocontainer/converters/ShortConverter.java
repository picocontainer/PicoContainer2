package org.picocontainer.converters;

import org.picocontainer.converters.Converter;


/**
 * Converts strings to 'short' data type objects.
 * @author Paul Hammant, Michael Rimov
 */
class ShortConverter implements Converter<Short> {

    /**
     * {@inheritDoc}
     */
    public Short convert(String paramValue) {
        return Short.valueOf(paramValue);
    }
}
