package org.picocontainer.converters;

import org.picocontainer.Converting;

import java.lang.reflect.Type;

class DoubleConverter implements Converting.Converter {
    public boolean canConvert(Type type) {
        return Double.class == type;
    }

    public Object convert(String paramValue, Type type) {
        return Double.valueOf(paramValue);
    }
}
