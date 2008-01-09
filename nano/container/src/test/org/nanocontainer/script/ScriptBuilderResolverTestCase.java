package org.nanocontainer.script;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Name/Builder Resolution Test Cases.
 * @author Michael Rimov
 */
public class ScriptBuilderResolverTestCase {
    private ScriptBuilderResolver scriptBuilderResolver = null;

    @Before public void setUp() throws Exception {
        scriptBuilderResolver = new ScriptBuilderResolver();
    }

    @After public void tearDown() throws Exception {
        scriptBuilderResolver = null;
    }


    @Test public void testGetAllSupportedExtensions() {
        Set allExtensions = new TreeSet();

        allExtensions.add(ScriptBuilderResolver.XML);

        String[] actualReturn = scriptBuilderResolver.getAllSupportedExtensions();
        assertNotNull(actualReturn);

        List returnAsList = Arrays.asList(actualReturn);
        boolean someMerged = allExtensions.removeAll(returnAsList);
        assertTrue(someMerged);
        assertTrue(allExtensions.size() == 0);
    }

    @Test public void testGetBuilderClassNameForFile() {
        File compositionFile = new File("test.xml");
        String expected = ScriptBuilderResolver.DEFAULT_XML_BUILDER;
        String actual = scriptBuilderResolver.getBuilderClassName(compositionFile);
        assertEquals("return value", expected, actual);
    }

    @Test public void testGetBuilderClassNameForResource() {
        final String resourceName = "/org/nanocontainer/nanocontainer.xml";
        URL compositionURL = this.getClass().getResource(resourceName);
        if (compositionURL == null) {
            fail("This test depended on resource '"+ resourceName + "' which appears to have been moved");
        }
        String expected = ScriptBuilderResolver.DEFAULT_XML_BUILDER;
        String actual = scriptBuilderResolver.getBuilderClassName(compositionURL);
        assertEquals("return value", expected, actual);
    }

    @Test public void testGetBuilderClassNameForExtension() throws UnsupportedScriptTypeException {
        String expectedReturn = ScriptBuilderResolver.DEFAULT_XML_BUILDER;
        String actualReturn = scriptBuilderResolver.getBuilderClassName(".xml");
        assertEquals("return value", expectedReturn, actualReturn);
    }

    @Test public void testGetBuilderForExtensionThrowsExceptionForUnknownBuilderType() {
        try {
            scriptBuilderResolver.getBuilderClassName(".foo");
            fail("Retrieving extension of type .foo should have thrown exception");
        } catch (UnsupportedScriptTypeException ex) {
            assertEquals(".foo",ex.getRequestedExtension());
        }
    }

    @Test public void testRegisterBuilder() {
        scriptBuilderResolver.registerBuilder(".foo","org.example.FooBar");
        assertEquals("org.example.FooBar", scriptBuilderResolver.getBuilderClassName(".foo"));
    }

    @Test public void testResetBuilders() {
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
