package org.picocontainer.converters;

import org.picocontainer.converters.Converter;

import java.io.File;

/**
 * Converts strings to files.
 * @author Paul Hammant, Michael Rimov
 */
class FileConverter implements Converter<File> {

    /** {@inheritDoc} **/
    public File convert(String paramValue) {
        return new File(paramValue);
    }
}
