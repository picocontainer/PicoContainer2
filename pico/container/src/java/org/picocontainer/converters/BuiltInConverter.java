package org.picocontainer.converters;

import org.picocontainer.Converting;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class BuiltInConverter implements Converting.Converter, Serializable {

    private final Map<Class, Converting.Converter> stringConverters = new HashMap<Class, Converting.Converter>();

    public BuiltInConverter() {
        addBuiltInConverters();
    }

    protected void addBuiltInConverters() {
        addConverter(Integer.class, new IntegerConverter());
        addConverter(Double.class, new DoubleConverter());
        addConverter(Boolean.class, new BooleanConverter());
        addConverter(Long.class, new LongConverter());
        addConverter(Float.class, new FloatConverter());
        addConverter(Character.class, new CharacterConverter());
        addConverter(Byte.class, new ByteConverter());
        addConverter(Short.class, new ShortConverter());
        addConverter(File.class, new FileConverter());
    }

    protected void addConverter(Class<?> key, Converting.Converter converter) {
        stringConverters.put(key, converter);
    }

    public boolean canConvert(Type type) {
        return stringConverters.containsKey(type);
    }

    public Object convert(String paramValue, Type type) {
        Converting.Converter converter = stringConverters.get(type);
        if (converter== null) {
            return null;
        }
        return converter.convert(paramValue, type);
    }

}
