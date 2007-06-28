package org.nanocontainer;

import java.io.FilePermission;
import java.net.URL;

import junit.framework.TestCase;

/**
 * 
 * @author Mauro Talevi
 */
public class ClassPathElementTestCase extends TestCase {

    public void testGetUrl() throws Exception{
        URL url = new URL("file:///usr/lib");
        ClassPathElement element = new ClassPathElement(url);
        assertEquals(url, element.getUrl());
    }

    public void testGrantPermission() throws Exception{
        ClassPathElement element = new ClassPathElement(new URL("file:///usr/lib"));
        element.grantPermission(new FilePermission("/usr/lib", "read"));
        assertNotNull(element.getPermissionCollection());
    }
}
