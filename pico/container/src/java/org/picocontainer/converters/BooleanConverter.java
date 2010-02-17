package org.picocontainer.converters;


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
