package org.picocontainer.injectors;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.thoughtworks.paranamer.Paranamer;
import com.thoughtworks.paranamer.asm.AsmParanamer;

public class ParanamerProxy {

    private final transient Paranamer paranamer = new AsmParanamer();

    public String[] lookupParameterNames(Constructor constructor) {
        return paranamer.lookupParameterNames(constructor);
    }
    public String[] lookupParameterNames(Method method) {
        return paranamer.lookupParameterNames(method);
    }
}
