package org.picocontainer.converters;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.picocontainer.ConverterSet;

@SuppressWarnings("serial")
public class BuiltInConverter implements ConverterSet, Serializable {

    private final Map<Class, Converter> stringConverters = new HashMap<Class, Converter>();

    public BuiltInConverter() {
        addBuiltInConverters();
    }

    protected void addBuiltInConverters() {
        IntegerConverter intConverter = new IntegerConverter();
        addConverter(Integer.class, intConverter);
        addConverter(Integer.TYPE, intConverter);
        
        DoubleConverter doubleConverter = new DoubleConverter();
        addConverter(Double.class, doubleConverter);
        addConverter(Double.TYPE, doubleConverter);
        
        BooleanConverter booleanConverter = new BooleanConverter();        
        addConverter(Boolean.class, booleanConverter);
        addConverter(Boolean.TYPE, booleanConverter);
        
        LongConverter longConverter = new LongConverter();
        addConverter(Long.class, longConverter);
        addConverter(Long.TYPE, longConverter);

        FloatConverter floatConverter = new FloatConverter();        
        addConverter(Float.class, floatConverter);
        addConverter(Float.TYPE, floatConverter);

        CharacterConverter charConverter = new CharacterConverter();
        addConverter(Character.class, charConverter);
        addConverter(Character.TYPE, charConverter);
        
        ByteConverter byteConverter = new ByteConverter();
        addConverter(Byte.class, byteConverter);
        addConverter(Byte.TYPE, byteConverter);
        
        ShortConverter shortConverter = new ShortConverter();
        addConverter(Short.class, shortConverter);
        addConverter(Short.TYPE, shortConverter);
        
        addConverter(File.class, new FileConverter());
    }

    protected void addConverter(Class<?> key, Converter converter) {
        stringConverters.put(key, converter);
    }

    public boolean canConvert(Type type) {
        return stringConverters.containsKey(type);
    }

    public Object convert(String paramValue, Type type) {
        Converter converter = stringConverters.get(type);
        if (converter== null) {
            return null;
        }
        return converter.convert(paramValue);
    }

}
