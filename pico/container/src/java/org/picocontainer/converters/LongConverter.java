package org.picocontainer.converters;

import org.picocontainer.Converting;

import java.lang.reflect.Type;

class LongConverter implements Converting.Converter {
    public boolean canConvert(Type type) {
        return Long.class == type;
    }

    public Object convert(String paramValue, Type type) {
        return Long.valueOf(paramValue);
    }
}
