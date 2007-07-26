package org.nanocontainer.script.groovy.buildernodes;

import org.picocontainer.MutablePicoContainer;

import java.util.Map;
import java.util.Iterator;

import org.nanocontainer.script.NanoContainerMarkupException;

public class ConfigNode extends AbstractBuilderNode {

    public static final String NODE_NAME = "config";

    public ConfigNode() {
        super(NODE_NAME);
    }

    public Object createNewNode(Object current, Map attributes) {

        Iterator it = attributes.entrySet().iterator();
        Object key = ((Map.Entry)it.next()).getValue();
        Object value = ((Map.Entry)it.next()).getValue();
        if (it.hasNext()) {
            throw new NanoContainerMarkupException("config has two parameters - key and value");
        }

        ((MutablePicoContainer) current).addConfig((String) key, value);
        return null;
    }

    public void validateScriptedAttributes(Map specifiedAttributes) throws NanoContainerMarkupException {
        if (specifiedAttributes.size() != 2) {
            throw new NanoContainerMarkupException("config has two parameters - key and value");
        }

    }
}
