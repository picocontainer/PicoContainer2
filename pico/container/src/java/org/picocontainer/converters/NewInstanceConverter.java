package org.picocontainer.converters;

import org.picocontainer.Converting;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

public class NewInstanceConverter implements Converting.Converter {
    private Constructor c;

    public NewInstanceConverter(Class clazz) {
        try {
            c = clazz.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
        }
    }
    public boolean canConvert(Type type) {
        return false;
    }

    public Object convert(String paramValue, Type type) {
        try {
            return c.newInstance(paramValue);
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        } catch (InstantiationException e) {
        }
        return null;
    }
}
