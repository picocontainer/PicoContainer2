package org.picocontainer;

import java.lang.reflect.Type;

public interface Converting {

    Converter getConverter();

    public static interface Converter {
        boolean canConvert(Type type);
        Object convert(String paramValue, Type type);
    }
    
}
