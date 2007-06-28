package org.nanocontainer.script.groovy;

import java.util.Map;
import java.util.Set;
import org.nanocontainer.script.NanoContainerMarkupException;

/**
 * In a groovy node builder environment, there is often one class per
 * node that is possible in a builder.  This interface provides the necessary
 * validation and interaction methods for the mediator (The GroovyNodeBuilder
 * object) to figure out who should handle what.
 * @author Michael Rimov
 * @version 1.0
 */
public interface BuilderNode {

    /**
     * Retrieve the name of the node.  Examples could be 'container' or 'component'.
     * @return String
     */
    String getNodeName();

    /**
     * Retrieve a map of supported attribute names.
     * <p><strong>note:</strong>Supported attributes are currently unverified by the
     * GroovyNodeBuilder as this would result in a change of behavior.</p>
     * @return Set of Strings.
     */
    Set getSupportedAttributes();


    /**
     * Validates a given map of attributes as supplied by the GroovyNodeBuilder
     * against the node's supported attributes.
     * @param specifiedAttributes Map
     * @throws NanoContainerMarkupException
     */
    void validateScriptedAttributes(Map specifiedAttributes) throws NanoContainerMarkupException;

    /**
     * Execute the handler for the given node builder.
     * @param current the current object.  May be null
     * for no parent container.
     * @param attributes Map attributes specified in the groovy script
     * for the builder node.
     * in for consistency with the Groovy Builder API.  Normally set to null.
     * @return Object
     * @throws NanoContainerMarkupException upon Nanocontainer error.
     */
    Object createNewNode(Object current, Map attributes) throws NanoContainerMarkupException;
}
