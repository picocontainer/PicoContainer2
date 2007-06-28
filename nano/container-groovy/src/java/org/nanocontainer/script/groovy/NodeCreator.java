package org.nanocontainer.script.groovy;

import java.util.Map;

public interface NodeCreator {

    Object createNode(Object name, Map attributes);
}
