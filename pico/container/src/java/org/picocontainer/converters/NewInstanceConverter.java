package org.picocontainer.converters;

import org.picocontainer.converters.Converter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class NewInstanceConverter implements Converter {
    private Constructor c;

    public NewInstanceConverter(Class clazz) {
        try {
            c = clazz.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
        }
    }
    public Object convert(String paramValue) {
        try {
            return c.newInstance(paramValue);
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        } catch (InstantiationException e) {
        }
        return null;
    }
}
