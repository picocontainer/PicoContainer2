package org.nanocontainer.script.xml.issues;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.ArrayList;

import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.nanocontainer.script.xml.XMLContainerBuilder;
import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.gems.behaviors.HotSwappingBehavior;

//http://jira.codehaus.org/browse/NANO-170
public class Issue0170TestCase extends AbstractScriptedContainerBuilderTestCase {


    public void testSomething() {

    }
    
    public void BROKEN_testHotSwappingCAF() {
        Reader script = new StringReader("" +
                "<container>" +
                "  <component-adapter-factory key='factory' class='org.picocontainer.gems.behaviors.HotSwappingBehaviorFactory'>"+
                "    <component-adapter-factory class='org.picocontainer.behaviors.CachingBehaviorFactory'>"+
                "      <component-adapter-factory class='org.picocontainer.injectors.ConstructorInjectionFactory'/>"+
                "    </component-adapter-factory>"+
                "  </component-adapter-factory>"+
                "  <component-adapter class-name-key='java.util.List' class='java.util.ArrayList' factory='factory'/>"+
                "</container>");

        PicoContainer pico = buildContainer(script);
        assertNotNull(pico);
        List list = pico.getComponent(List.class);
        assertNotNull(list);

        ComponentAdapter listCA = pico.getComponentAdapter(List.class);

        assertTrue(listCA instanceof HotSwappingBehavior);
        HotSwappingBehavior hsca = (HotSwappingBehavior) listCA;
        ArrayList newList = new ArrayList();
        List oldList = (List) hsca.swapRealInstance(newList);

        List list2 = pico.getComponent(List.class);

        assertEquals(list, list2); // still the same 'end point'

        list2.add("foo");

        assertFalse(oldList.contains("foo"));
        assertTrue(newList.contains("foo"));


    }

    private PicoContainer buildContainer(Reader script) {
        return buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
    }

}

   