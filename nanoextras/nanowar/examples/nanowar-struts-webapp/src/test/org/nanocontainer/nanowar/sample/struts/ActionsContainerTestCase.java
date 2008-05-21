package org.nanocontainer.nanowar.sample.struts;

import org.nanocontainer.nanowar.sample.dao.CheeseDao;
import org.nanocontainer.nanowar.sample.service.CheeseService;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.nanocontainer.script.xml.XStreamContainerBuilder;
import org.picocontainer.PicoContainer;
import org.picocontainer.ObjectReference;
import org.picocontainer.references.SimpleReference;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * @author Mauro Talevi
 */
public final class ActionsContainerTestCase {

    private final ObjectReference containerRef = new SimpleReference();

    private final ObjectReference parentContainerRef = new SimpleReference();

    protected PicoContainer buildContainer(Reader script) {
        ScriptedContainerBuilder builder = new XStreamContainerBuilder(script,
                getClass().getClassLoader());
        parentContainerRef.set(null);
        builder.buildContainer(containerRef, parentContainerRef, "SOME_SCOPE", true);
        return (PicoContainer) containerRef.get();
    }

    @Test public void testContainerBuildingWithXmlConfig() {

        Reader script = new StringReader("<container>"
                + "	 <implementation type='org.nanocontainer.nanowar.sample.dao.CheeseDao'"
                + "					class='org.nanocontainer.nanowar.sample.dao.simple.MemoryCheeseDao'> "
                + "  </implementation>"
                + "	 <implementation type='org.nanocontainer.nanowar.sample.service.CheeseService'"
                + " 				class='org.nanocontainer.nanowar.sample.service.defaults.DefaultCheeseService'>"
                + "  </implementation>"
                + " </container>");

        PicoContainer pico = buildContainer(script);
        assertNotNull(pico.getComponent(CheeseDao.class));
        assertNotNull(pico.getComponent(CheeseService.class));
    }

}