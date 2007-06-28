package org.nanocontainer;

import junit.framework.TestCase;

/**
 * 
 * @author Mauro Talevi
 */
public class ClassNameKeyTestCase extends TestCase {

    public void testGetClassName(){
        String className = ClassName.class.getName();
        ClassName key = new ClassName(className);
        assertEquals(className, key.getClassName());
    }
}
