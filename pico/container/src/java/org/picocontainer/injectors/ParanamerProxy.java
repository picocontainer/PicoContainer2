package org.picocontainer.injectors;

import java.lang.reflect.Constructor;

import com.thoughtworks.paranamer.Paranamer;
import com.thoughtworks.paranamer.asm.AsmParanamer;

public class ParanamerProxy {

    private final transient Paranamer paranamer = new AsmParanamer();

    public String[] lookupParameterNames(Constructor sortedMatchingConstructor) {
        return paranamer.lookupParameterNames(sortedMatchingConstructor);  
    }
}
