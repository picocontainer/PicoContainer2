package org.picocontainer.converters;

import org.picocontainer.Converting;

import java.lang.reflect.Type;

class CharacterConverter implements Converting.Converter {
    public boolean canConvert(Type type) {
        return Character.class == type;
    }

    public Object convert(String paramValue, Type type) {
        return paramValue.charAt(0);
    }
}
