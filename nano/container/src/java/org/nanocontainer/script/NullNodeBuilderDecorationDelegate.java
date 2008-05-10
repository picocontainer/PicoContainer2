package org.nanocontainer.script;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ComponentFactory;

import java.util.Map;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 */
public class NullNodeBuilderDecorationDelegate implements NodeBuilderDecorationDelegate {
    public ComponentFactory decorate(ComponentFactory componentFactory, Map attributes) {
        return componentFactory;
    }

    public MutablePicoContainer decorate(MutablePicoContainer picoContainer) {
        return picoContainer;
    }

    public Object createNode(Object name, Map attributes, Object parentElement) {
        throw new NanoContainerMarkupException("Don't know how to create a '" + name + "' child of a '" + ((parentElement == null) ? "null" : parentElement.toString()) + "' element");
    }

    public void rememberComponentKey(Map attributes) {
    }
}
