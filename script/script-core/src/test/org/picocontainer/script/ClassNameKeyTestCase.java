package org.picocontainer.script;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.picocontainer.script.ClassName;

/**
 * 
 * @author Mauro Talevi
 */
public class ClassNameKeyTestCase {

    @Test public void testGetClassName(){
        String className = ClassName.class.getName();
        ClassName key = new ClassName(className);
        assertEquals(className, key.getClassName());
    }
}
