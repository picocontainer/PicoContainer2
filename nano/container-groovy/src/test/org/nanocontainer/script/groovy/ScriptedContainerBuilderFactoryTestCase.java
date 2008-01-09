package org.nanocontainer.script.groovy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import org.junit.Test;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.nanocontainer.script.ScriptedContainerBuilderFactory;

public class ScriptedContainerBuilderFactoryTestCase {

    private static final String TEST_SCRIPT_PATH = "/org/nanocontainer/script/groovy/nanocontainer.groovy";

    @Test public void testScriptedContainerBuilderFactoryWithUrl() throws ClassNotFoundException {
        URL resource = getClass().getResource(TEST_SCRIPT_PATH);
        assertNotNull("Could not find script resource '+ TEST_SCRIPT_PATH + '.", resource);

        ScriptedContainerBuilderFactory result = new ScriptedContainerBuilderFactory(resource);
        ScriptedContainerBuilder builder = result.getContainerBuilder();
        assertNotNull(builder);
        assertEquals(GroovyContainerBuilder.class.getName(), builder.getClass().getName());
    }

    @Test public void testBuildWithReader() throws ClassNotFoundException {
        Reader script = new StringReader("" +
            "import org.nanocontainer.testmodel.*\n" +
            "X.reset()\n" +
            "builder = new org.nanocontainer.script.groovy.GroovyNodeBuilder()\n" +
            "nano = builder.container {\n" +
            "    component(A)\n" +
            "}");

        ScriptedContainerBuilderFactory result = new ScriptedContainerBuilderFactory(script,
            GroovyContainerBuilder.class.getName());
        ScriptedContainerBuilder builder = result.getContainerBuilder();
        assertNotNull(builder);
        assertEquals(GroovyContainerBuilder.class.getName(), builder.getClass().getName());
    }

    // must use xml script
    public void FIXMEtestBuildWithFile() throws IOException {
        File resource = new File("src/test/org/nanocontainer/script/xml/nanocontainer.xml");
        assertNotNull("Could not find script resource '+ TEST_SCRIPT_PATH + '.", resource);

        ScriptedContainerBuilderFactory result = new ScriptedContainerBuilderFactory(resource);
        ScriptedContainerBuilder builder = result.getContainerBuilder();
        assertNotNull(builder);
        assertEquals(GroovyContainerBuilder.class.getName(), builder.getClass().getName());

    }


}
