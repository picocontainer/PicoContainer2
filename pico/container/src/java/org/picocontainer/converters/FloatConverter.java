package org.picocontainer.converters;

import org.picocontainer.Converting;

import java.lang.reflect.Type;

class FloatConverter implements Converting.Converter {
    public boolean canConvert(Type type) {
        return Float.class == type;
    }

    public Object convert(String paramValue, Type type) {
        return Float.valueOf(paramValue);
    }
}
