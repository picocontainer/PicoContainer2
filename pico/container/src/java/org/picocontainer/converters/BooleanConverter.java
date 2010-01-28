package org.picocontainer.converters;

import org.picocontainer.Converting;

import java.lang.reflect.Type;

class BooleanConverter implements Converting.Converter {
    public boolean canConvert(Type type) {
        return Boolean.class == type;
    }

    public Object convert(String paramValue, Type type) {
        return Boolean.valueOf(paramValue);
    }
}
