package org.nanocontainer.script.groovy;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

/**
 * This class can generate a Groovy script from a preconfigured container.
 * This script can be passed to {@link GroovyContainerBuilder} to recreate
 * a new container with the same configuration.
 * <p/>
 * This is practical in situations where a container configuration needs
 * to be saved.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class GroovyScriptGenerator {
    // This implementation is ugly and naive. But it's all I need for now.
    // When there are more requirements (in the form of tests), we can improve this.
    public String generateScript(MutablePicoContainer pico) {
        StringBuffer groovy = new StringBuffer();
        groovy.append("pico = new org.nanocontainer.DefaultNanoContainer()\n");

        Collection<ComponentAdapter<?>> componentAdapters = pico.getComponentAdapters();
        for (ComponentAdapter componentAdapter : componentAdapters) {
            Object componentKey = componentAdapter.getComponentKey();
            String groovyKey = null;
            if (componentKey instanceof Class) {
                groovyKey = ((Class) componentKey).getName();
            } else if (componentKey instanceof String) {
                groovyKey = "\"" + componentKey + "\"";
            }

            Object componentInstance = componentAdapter.getComponentInstance(pico);

            if (componentInstance instanceof String) {
                groovy.append("pico.addComponent(")
                    .append(groovyKey)
                    .append(", (Object) \"")
                    .append(componentInstance)
                    .append("\")\n");
            } else {
                groovy.append("pico.addComponent(")
                    .append(groovyKey)
                    .append(", ")
                    .append(componentInstance.getClass().getName())
                    .append(")\n");
            }
        }
        return groovy.toString();
    }
}