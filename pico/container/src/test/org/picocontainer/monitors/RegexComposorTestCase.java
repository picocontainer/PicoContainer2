package org.picocontainer.monitors;

import junit.framework.TestCase;
import org.junit.Test;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Characteristics;
import static org.picocontainer.Characteristics.USE_NAMES;

import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RegexComposorTestCase extends TestCase {

    @Test
    public void testReturningList() {
        MutablePicoContainer pico = new DefaultPicoContainer(new RegexComposor());
        pico.addComponent("apple1", "Braeburn");
        pico.addComponent("apple2", "Granny Smith");
        pico.addComponent("plum", "Victoria");

        List apples = (List) pico.getComponent("apple[1-9]");
        assertEquals(2, apples.size());
        assertEquals("Braeburn", apples.get(0));
        assertEquals("Granny Smith", apples.get(1));
    }

    @Test
    public void testReturningList2() {
        MutablePicoContainer pico = new DefaultPicoContainer(new RegexComposor("apple[1-9]", "apples"));
        pico.addComponent("apple1", "Braeburn");
        pico.addComponent("apple2", "Granny Smith");
        pico.addComponent("plum", "Victoria");
        pico.as(USE_NAMES).addComponent(NeedsApples.class);

        NeedsApples needsApples = pico.getComponent(NeedsApples.class);
        assertEquals(2, needsApples.apples.size());
        assertEquals("Braeburn", needsApples.apples.get(0));
        assertEquals("Granny Smith", needsApples.apples.get(1));
    }

    public static class RegexComposor extends NullComponentMonitor {

        private final Pattern pattern;
        private final String forNamedComponent;

        public RegexComposor(String pattern, String forNamedComponent) {
            this.pattern = Pattern.compile(pattern);
            this.forNamedComponent = forNamedComponent;
        }

        public RegexComposor() {
            pattern = null;
            forNamedComponent = null;
        }

        @Override
        public Object noComponentFound(MutablePicoContainer container, Object componentKey) {
            if (componentKey instanceof String
                    && (forNamedComponent == null || forNamedComponent.equals(componentKey))) {
                Pattern pat = null;
                if (pattern == null) {
                    pat = Pattern.compile((String) componentKey);
                } else {
                    pat = pattern;
                }
                Collection<ComponentAdapter<?>> cas = container.getComponentAdapters();
                List retVal = new ArrayList();
                for (ComponentAdapter<?> componentAdapter : cas) {
                    Object key = componentAdapter.getComponentKey();
                    if (key instanceof String) {
                        Matcher matcher = pat.matcher((String) key);
                        if (matcher != null && matcher.find()) {
                            retVal.add(componentAdapter.getComponentInstance(container, ComponentAdapter.NOTHING.class));
                        }
                    }
                }
                return retVal;
            }
            return super.noComponentFound(container, componentKey);
        }
    }

    public static class NeedsApples {
        List<String> apples;

        public NeedsApples(List<String> apples) {
            this.apples = apples;
        }
    }


}
