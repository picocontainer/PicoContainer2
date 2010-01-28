package org.picocontainer.converters;

import org.picocontainer.Converting;

import java.lang.reflect.Type;

class ShortConverter implements Converting.Converter {
    public boolean canConvert(Type type) {
        return Short.class == type;
    }

    public Object convert(String paramValue, Type type) {
        return Short.valueOf(paramValue);
    }
}
