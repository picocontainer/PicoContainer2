package org.nanocontainer.script;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import junit.framework.TestCase;

/**
 * Name/Builder Resolution Test Cases.
 * @author Michael Rimov
 */
public class ScriptBuilderResolverTestCase extends TestCase {
    private ScriptBuilderResolver scriptBuilderResolver = null;

    protected void setUp() throws Exception {
        super.setUp();
        scriptBuilderResolver = new ScriptBuilderResolver();
    }

    protected void tearDown() throws Exception {
        scriptBuilderResolver = null;
        super.tearDown();
    }


    public void testGetAllSupportedExtensions() {
        Set allExtensions = new TreeSet();

        allExtensions.add(ScriptBuilderResolver.XML);

        String[] actualReturn = scriptBuilderResolver.getAllSupportedExtensions();
        assertNotNull(actualReturn);

        List returnAsList = Arrays.asList(actualReturn);
        boolean someMerged = allExtensions.removeAll(returnAsList);
        assertTrue(someMerged);
        assertTrue(allExtensions.size() == 0);
    }

    public void testGetBuilderClassNameForFile() {
        File compositionFile = new File("test.xml");
        String expected = ScriptBuilderResolver.DEFAULT_XML_BUILDER;
        String actual = scriptBuilderResolver.getBuilderClassName(compositionFile);
        assertEquals("return value", expected, actual);
    }

    public void testGetBuilderClassNameForResource() {
        final String resourceName = "/org/nanocontainer/nanocontainer.xml";
        URL compositionURL = this.getClass().getResource(resourceName);
        if (compositionURL == null) {
            fail("This test depended on resource '"+ resourceName + "' which appears to have been moved");
        }
        String expected = ScriptBuilderResolver.DEFAULT_XML_BUILDER;
        String actual = scriptBuilderResolver.getBuilderClassName(compositionURL);
        assertEquals("return value", expected, actual);
    }

    public void testGetBuilderClassNameForExtension() throws UnsupportedScriptTypeException {
        String expectedReturn = ScriptBuilderResolver.DEFAULT_XML_BUILDER;
        String actualReturn = scriptBuilderResolver.getBuilderClassName(".xml");
        assertEquals("return value", expectedReturn, actualReturn);
    }

    public void testGetBuilderForExtensionThrowsExceptionForUnknownBuilderType() {
        try {
            scriptBuilderResolver.getBuilderClassName(".foo");
            fail("Retrieving extension of type .foo should have thrown exception");
        } catch (UnsupportedScriptTypeException ex) {
            assertEquals(".foo",ex.getRequestedExtension());
        }
    }

    public void testRegisterBuilder() {
        scriptBuilderResolver.registerBuilder(".foo","org.example.FooBar");
        assertEquals("org.example.FooBar", scriptBuilderResolver.getBuilderClassName(".foo"));
    }

    public void testResetBuilders() {
        scriptBuilderResolver.registerBuilder(".foo","org.example.FooBar");
        scriptBuilderResolver.resetBuilders();
        try {
            scriptBuilderResolver.getBuilderClassName(".foo");
            fail("Retrieving extension of type .foo should have thrown exception");
        } catch (UnsupportedScriptTypeException ex) {
            assertEquals(".foo",ex.getRequestedExtension());
        }
    }

}
