package org.picocontainer.converters;

import org.picocontainer.Converting;

import java.io.File;
import java.lang.reflect.Type;

class FileConverter implements Converting.Converter {
    public boolean canConvert(Type type) {
        return File.class == type;
    }

    public Object convert(String paramValue, Type type) {
        return new File(paramValue);
    }
}
