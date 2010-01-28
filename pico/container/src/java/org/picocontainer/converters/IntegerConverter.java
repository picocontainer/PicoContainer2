package org.picocontainer.converters;

import org.picocontainer.Converting;

import java.lang.reflect.Type;

class IntegerConverter implements Converting.Converter {
    public boolean canConvert(Type type) {
        return Integer.class == type;
    }

    public Object convert(String paramValue, Type type) {
        return Integer.valueOf(paramValue);
    }
}
