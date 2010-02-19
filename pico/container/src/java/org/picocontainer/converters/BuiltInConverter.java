package org.picocontainer.converters;

import org.picocontainer.ConverterSet;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class BuiltInConverter implements ConverterSet, Serializable {

    private final Map<Class, Converter> converters = new HashMap<Class, Converter>();

    public BuiltInConverter() {
        addBuiltInConverters();
    }

    protected void addBuiltInConverters() {
        addConverter(new IntegerConverter(), Integer.class, Integer.TYPE);
        addConverter(new DoubleConverter(), Double.class, Double.TYPE);
        addConverter(new BooleanConverter(), Boolean.class, Boolean.TYPE);
        addConverter(new LongConverter(), Long.class, Long.TYPE);
        addConverter(new FloatConverter(), Float.class, Float.TYPE);
        addConverter(new CharacterConverter(), Character.class, Character.TYPE);
        addConverter(new ByteConverter(), Byte.class, Byte.TYPE);
        addConverter(new ShortConverter(), Short.class, Short.TYPE);
        addConverter(new FileConverter(), File.class);
    }

    private void addConverter(Converter converter, Class<?> type, Class<?> type2) {
        addConverter(converter, type);
        addConverter(converter, type2);
    }

    protected void addConverter(Converter converter, Class<?> key) {
        converters.put(key, converter);
    }

    public boolean canConvert(Type type) {
        return converters.containsKey(type);
    }

    public Object convert(String paramValue, Type type) {
        Converter converter = converters.get(type);
        if (converter== null) {
            return null;
        }
        return converter.convert(paramValue);
    }

}
