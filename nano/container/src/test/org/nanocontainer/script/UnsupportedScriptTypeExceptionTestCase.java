package org.nanocontainer.script;

import junit.framework.*;

/**
 * Exception Tests.
 * @author Michael Rimov
 */
public class UnsupportedScriptTypeExceptionTestCase extends TestCase {
    private UnsupportedScriptTypeException unsupportedScriptTypeException = null;

    private final String[] supportedParams = new String[]{".groovy",".py",".xml"};

    protected void setUp() throws Exception {
        super.setUp();
        unsupportedScriptTypeException = new UnsupportedScriptTypeException("test.txt", supportedParams);
    }

    protected void tearDown() throws Exception {
        unsupportedScriptTypeException = null;
        super.tearDown();
    }

    public void testGetMessage() {
        String actualReturn = unsupportedScriptTypeException.getMessage();
        assertNotNull(actualReturn);
        assertTrue(actualReturn.indexOf(".groovy") > -1);
        assertTrue(actualReturn.indexOf(".py") > -1) ;
        assertTrue(actualReturn.indexOf(".xml") > -1);
        assertTrue(actualReturn.indexOf("test.txt") > -1);
    }

    public void testGetRequestedExtension() {
        String expectedReturn = "test.txt";
        String actualReturn = unsupportedScriptTypeException.getRequestedExtension();
        assertEquals("return value", expectedReturn, actualReturn);
    }

    public void testGetSystemSupportedExtensions() {
        String[] expectedReturn = supportedParams;
        String[] actualReturn = unsupportedScriptTypeException.getSystemSupportedExtensions();
        assertEquals("return value", expectedReturn, actualReturn);
    }


}
