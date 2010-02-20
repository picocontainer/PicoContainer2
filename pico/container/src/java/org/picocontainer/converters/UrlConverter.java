package org.picocontainer.converters;

import org.picocontainer.PicoCompositionException;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlConverter implements Converter<URL> {

    /**
     * {@inheritDoc} *
     */
    public URL convert(String paramValue) {
        try {
            return new URL(paramValue);
        } catch (MalformedURLException e) {
            throw new PicoCompositionException(e);
        }
    }
}
