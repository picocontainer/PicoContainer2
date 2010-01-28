package org.picocontainer.converters;

import org.picocontainer.Converting;

import java.lang.reflect.Type;

class ByteConverter implements Converting.Converter {
    public boolean canConvert(Type type) {
        return Byte.class == type;
    }

    public Object convert(String paramValue, Type type) {
        return Byte.valueOf(paramValue);
    }
}
