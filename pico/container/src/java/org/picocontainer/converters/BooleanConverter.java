package org.picocontainer.converters;

import org.picocontainer.converters.Converter;


/**
 * Converts strings to boolean types.
 * @author Paul Hammant, Michael Rimov
 */
class BooleanConverter implements Converter<Boolean> {


    /** {@inheritDoc} **/
    public Boolean convert(String paramValue) {
        return Boolean.valueOf(paramValue);
    }
}
