package org.picocontainer.converters;


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
