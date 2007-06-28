package org.nanocontainer.script.groovy;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.DefaultPicoContainer;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class GroovyScriptGeneratorTestCase extends TestCase {
    public void testShouldWriteAGroovyScriptThatAllowsToRecreateASimilarContainer() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(ArrayList.class);
        pico.addComponent("Hello", "World");

        GroovyScriptGenerator groovyScriptGenerator = new GroovyScriptGenerator();
        String script = groovyScriptGenerator.generateScript(pico);

        GroovyContainerBuilder groovyContainerBuilder = new GroovyContainerBuilder(new StringReader(script), getClass().getClassLoader());
        PicoContainer newPico = groovyContainerBuilder.createContainerFromScript(null, null);

        assertNotNull(newPico.getComponent(ArrayList.class));
        assertEquals("World", newPico.getComponent("Hello"));
    }
}