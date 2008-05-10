/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan                                           *
 *****************************************************************************/
package org.nanocontainer.script.groovy;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.util.BuilderSupport;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.ClassName;
import org.picocontainer.PicoClassNotFoundException;
import org.nanocontainer.script.NanoContainerMarkupException;
import org.nanocontainer.script.NodeBuilderDecorationDelegate;
import org.nanocontainer.script.NullNodeBuilderDecorationDelegate;
import org.nanocontainer.script.groovy.buildernodes.*;
import org.picocontainer.MutablePicoContainer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds node trees of PicoContainers and Pico components using GroovyMarkup.
 * <p>Simple example usage in your groovy script:
 * <code><pre>
 * builder = new org.nanocontainer.script.groovy.GroovyNodeBuilder()
 * pico = builder.container(parent:parent) {
 * &nbsp;&nbsp;component(class:org.nanocontainer.testmodel.DefaultWebServerConfig)
 * &nbsp;&nbsp;component(class:org.nanocontainer.testmodel.WebServerImpl)
 * }
 * </pre></code>
 * </p>
 * <h4>Extending/Enhancing GroovyNodeBuilder</h4>
 * <p>Often-times people need there own assembly commands that are needed
 * for extending/enhancing the node builder tree.  The perfect example of this
 * is <tt>DynaopGroovyNodeBuilder</tt> which provides a new vocabulary for
 * the groovy node builder with terms such as 'aspect', 'pointcut', etc.</p>
 * <p>GroovyNodeBuilder provides two primary ways of enhancing the nodes supported
 * by the groovy builder: {@link org.nanocontainer.script.NodeBuilderDecorationDelegate}
 * and special node handlers {@link BuilderNode}.
 * Using NodeBuilderDecorationDelegate is often a preferred method because it is
 * ultimately script independent.  However, replacing an existing GroovyNodeBuilder's
 * behavior is currently the only way to replace the behavior of an existing
 * groovy node handler.
 * </p>
 *
 * @author James Strachan
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Michael Rimov
 * @author Mauro Talevi
 */
public class GroovyNodeBuilder extends BuilderSupport {

    private static final String CLASS = "class";

    private static final String PARENT = "parent";


    /**
     * Flag indicating that the attribute validation should be performed.
     */
    public static final boolean PERFORM_ATTRIBUTE_VALIDATION = true;


    /**
     * Flag indicating that attribute validation should be skipped.
     */
    public static final boolean SKIP_ATTRIBUTE_VALIDATION = false;


    /**
     * Decoration delegate. The traditional method of adding functionality to
     * the Groovy builder.
     */
    private final NodeBuilderDecorationDelegate decorationDelegate;

    /**
     * Map of node handlers.
     */
    private final Map nodeBuilderHandlers = new HashMap();
    private final Map nodeBuilders = new HashMap();

    private final boolean performAttributeValidation;


    /**
     * Allows the composition of a <tt>{@link NodeBuilderDecorationDelegate}</tt> -- an
     * object that extends the capabilities of the <tt>GroovyNodeBuilder</tt>
     * with new tags, new capabilities, etc.
     *
     * @param decorationDelegate         NodeBuilderDecorationDelegate
     * @param performAttributeValidation should be set to PERFORM_ATTRIBUTE_VALIDATION
     *                                   or SKIP_ATTRIBUTE_VALIDATION
     * @see org.nanocontainer.aop.defaults.AopNodeBuilderDecorationDelegate
     */
    public GroovyNodeBuilder(NodeBuilderDecorationDelegate decorationDelegate, boolean performAttributeValidation) {
        this.decorationDelegate = decorationDelegate;
        this.performAttributeValidation = performAttributeValidation;

        //Build and register node handlers.
        this.setNode(new ComponentNode(decorationDelegate))
                .setNode(new ChildContainerNode(decorationDelegate))
                .setNode(new BeanNode())
                .setNode(new ConfigNode())
                .setNode(new ClasspathNode())
                .setNode(new DoCallNode())
                .setNode(new NewBuilderNode())
                .setNode(new ClassLoaderNode())
                .setNode(new GrantNode())
                .setNode(new AppendContainerNode());
        DefaultNanoContainer factory = new DefaultNanoContainer();
        try {
            factory.addComponent("wc",  new ClassName("org.nanocontainer.webcontainer.groovy.WebContainerBuilder"));
            setNode((BuilderNode) factory.getComponent("wc"));
        } catch (PicoClassNotFoundException e) {
            //do nothing.
        }

    }

    public GroovyNodeBuilder(NodeBuilderDecorationDelegate decorationDelegate) {
        this(decorationDelegate, SKIP_ATTRIBUTE_VALIDATION);
    }

    /**
     * Default constructor.
     */
    public GroovyNodeBuilder() {
        this(new NullNodeBuilderDecorationDelegate(), SKIP_ATTRIBUTE_VALIDATION);
    }


    protected void setParent(Object parent, Object child) {
    }

    protected Object doInvokeMethod(String s, Object name, Object args) {
        //TODO use setDelegate() from Groovy JSR
        Object answer = super.doInvokeMethod(s, name, args);
        List list = InvokerHelper.asList(args);
        if (!list.isEmpty()) {
            Object o = list.get(list.size() - 1);
            if (o instanceof Closure) {
                Closure closure = (Closure) o;
                closure.setDelegate(answer);
            }
        }
        return answer;
    }

    protected Object createNode(Object name) {
        return createNode(name, Collections.EMPTY_MAP);
    }

    protected Object createNode(Object name, Object value) {
        Map attributes = new HashMap();
        attributes.put(CLASS, value);
        return createNode(name, attributes);
    }

    /**
     * Override of create node.  Called by BuilderSupport.  It examines the
     * current state of the builder and the given parameters and dispatches the
     * code to one of the create private functions in this object.
     *
     * @param name       The name of the groovy node we're building.  Examples are
     *                   'container', and 'grant',
     * @param attributes Map  attributes of the current invocation.
     * @param value      A closure passed into the node.  Currently unused.
     * @return Object the created object.
     */
    protected Object createNode(Object name, Map attributes, Object value) {
        Object current = getCurrent();
        if (current != null && current instanceof GroovyObject) {
            GroovyObject groovyObject = (GroovyObject) current;
            return groovyObject.invokeMethod(name.toString(), attributes);
        } else if (current == null) {
            current = extractOrCreateValidRootNanoContainer(attributes);
        } else {
            if (attributes.containsKey(PARENT)) {
                throw new NanoContainerMarkupException("You can't explicitly specify a parent in a child element.");
            }
        }
        if (name.equals("registerBuilder")) {
            return registerBuilder(attributes);

        } else {
            return handleNode(name, attributes, current);
        }

    }

    private Object registerBuilder(Map attributes) {
        String builderName = (String) attributes.remove("name");
        Object clazz = attributes.remove("class");
        try {
            if (clazz instanceof String) {
                clazz = this.getClass().getClassLoader().loadClass((String) clazz);
            }
        } catch (ClassNotFoundException e) {
            throw new NanoContainerMarkupException("ClassNotFoundException " + clazz);
        }
        nodeBuilders.put(builderName, clazz);
        return clazz;
    }

    private Object handleNode(Object name, Map attributes, Object current) {

        attributes = new HashMap(attributes);

        BuilderNode nodeHandler = this.getNode(name.toString());

        if (nodeHandler == null) {
            Class builderClass = (Class) nodeBuilders.get(name);
            if (builderClass != null) {
                nodeHandler = this.getNode("newBuilder");
                attributes.put("class",builderClass);
            }
        }

        if (nodeHandler == null) {
            // we don't know how to handle it - delegate to the decorator.
            return getDecorationDelegate().createNode(name, attributes, current);

        } else {
            //We found a handler.

            if (performAttributeValidation) {
                //Validate
                nodeHandler.validateScriptedAttributes(attributes);
            }

            return nodeHandler.createNewNode(current, attributes);
        }
    }

    /**
     * Pulls the nanocontainer from the 'current' method or possibly creates
     * a new blank one if needed.
     *
     * @param attributes Map the attributes of the current node.
     * @return NanoContainer, never null.
     * @throws NanoContainerMarkupException
     */
    private NanoContainer extractOrCreateValidRootNanoContainer(final Map attributes) throws NanoContainerMarkupException {
        Object parentAttribute = attributes.get(PARENT);
        //
        //NanoPicoContainer implements MutablePicoCotainer AND NanoContainer
        //So we want to check for NanoContainer first.
        //
        if (parentAttribute instanceof NanoContainer) {
            // we're not in an enclosing scope - look at parent attribute instead
            return (NanoContainer) parentAttribute;
        }
        if (parentAttribute instanceof MutablePicoContainer) {
            // we're not in an enclosing scope - look at parent attribute instead
            return new DefaultNanoContainer((MutablePicoContainer) parentAttribute);
        }
        return null;
    }


    /**
     * Retrieve the current decoration delegate.
     *
     * @return NodeBuilderDecorationDelegate, should never be null.
     */
    public NodeBuilderDecorationDelegate getDecorationDelegate() {
        return this.decorationDelegate;
    }


    /**
     * Returns an appropriate node handler for a given node and
     *
     * @param tagName String
     * @return CustomGroovyNode the appropriate node builder for the given
     *         tag name, or null if no handler exists. (In which case, the Delegate
     *         receives the createChildContainer() call)
     */
    public synchronized BuilderNode getNode(final String tagName) {
        Object o = nodeBuilderHandlers.get(tagName);
        return (BuilderNode) o;
    }

    /**
     * Add's a groovy node handler to the table of possible handlers. If a node
     * handler with the same node name already exists in the map of handlers, then
     * the <tt>GroovyNode</tt> replaces the existing node handler.
     *
     * @param newGroovyNode CustomGroovyNode
     * @return GroovyNodeBuilder to allow for method chaining.
     */
    public synchronized GroovyNodeBuilder setNode(final BuilderNode newGroovyNode) {
        nodeBuilderHandlers.put(newGroovyNode.getNodeName(), newGroovyNode);
        return this;
    }

    protected Object createNode(Object name, Map attributes) {
        return createNode(name, attributes, null);
    }


}
