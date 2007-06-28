/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.script.xml;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.integrationkit.ContainerPopulator;
import org.nanocontainer.script.NanoContainerMarkupException;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentFactory;
import org.picocontainer.parameters.ComponentParameter;
import org.picocontainer.parameters.ConstantParameter;
import org.picocontainer.behaviors.CachingBehaviorFactory;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.injectors.ConstructorInjectionFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.DomReader;

/**
 * This class builds up a hierarchy of PicoContainers from an XML configuration file.
 *
 * @author Konstantin Pribluda
 * @version $Revision$
 */
public class XStreamContainerBuilder extends ScriptedContainerBuilder implements ContainerPopulator {
    private final Element rootElement;

    private final static String IMPLEMENTATION = "implementation";
    private final static String INSTANCE = "instance";
    private final static String ADAPTER = "adapter";
    private final static String CLASS = "class";
    private final static String KEY = "key";
    private final static String CONSTANT = "constant";
    private final static String DEPENDENCY = "dependency";
    private final static String CONSTRUCTOR = "constructor";

    private final HierarchicalStreamDriver xsdriver;

    /**
    * construct with just reader, use context classloader
     * @param script
     */
    public XStreamContainerBuilder(Reader script) {
        this(script,Thread.currentThread().getContextClassLoader());
    }
    
    /**
     * construct with given script and specified classloader
     * @param classLoader
     * @param script
     */
    public XStreamContainerBuilder(Reader script, ClassLoader classLoader) {
        this(script, classLoader, new DomDriver());
    }

    public XStreamContainerBuilder(Reader script, ClassLoader classLoader, HierarchicalStreamDriver driver) {
        super(script, classLoader);
        xsdriver = driver;
        InputSource inputSource = new InputSource(script);
        try {
            rootElement = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource).getDocumentElement();
        } catch (SAXException e) {
            throw new NanoContainerMarkupException(e);
        } catch (IOException e) {
            throw new NanoContainerMarkupException(e);
        } catch (ParserConfigurationException e) {
            throw new NanoContainerMarkupException(e);
        }
    }

    public XStreamContainerBuilder(URL script, ClassLoader classLoader, HierarchicalStreamDriver driver) {
        super(script, classLoader);
        xsdriver = driver;
        try {
            InputSource inputSource = new InputSource(getScriptReader());
            rootElement = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource).getDocumentElement();
        } catch (SAXException e) {
            throw new NanoContainerMarkupException(e);
        } catch (IOException e) {
            throw new NanoContainerMarkupException(e);
        } catch (ParserConfigurationException e) {
            throw new NanoContainerMarkupException(e);
        }
    }

    public void populateContainer(MutablePicoContainer container) {
        populateContainer(container, rootElement);
    }

    /**
     * just a convenience method, so we can work recursively with subcontainers
     * for whatever puproses we see cool.
     * @param container
     * @param rootElement
     */
    private void populateContainer(MutablePicoContainer container, Element rootElement) {
        NodeList children = rootElement.getChildNodes();
        Node child;
        String name;
        short type;
        for (int i = 0; i < children.getLength(); i++) {
            child = children.item(i);
            type = child.getNodeType();

            if (type == Document.ELEMENT_NODE) {
                name = child.getNodeName();
                if (IMPLEMENTATION.equals(name)) {
                    try {
                        insertImplementation(container, (Element) child);
                    } catch (ClassNotFoundException e) {
                        throw new NanoContainerMarkupException(e);
                    }
                } else if (INSTANCE.equals(name)) {
                    insertInstance(container, (Element) child);
                } else if (ADAPTER.equals(name)) {
                    insertAdapter(container, (Element) child);
                } else {
                    throw new NanoContainerMarkupException("Unsupported element:" + name);
                }
            }
        }

    }

    /**
     * process adapter node
     * @param container
     * @param rootElement
     */
    protected void insertAdapter(MutablePicoContainer container, Element rootElement) {
        String key = rootElement.getAttribute(KEY);
        String klass = rootElement.getAttribute(CLASS);
        try {
            DefaultPicoContainer nested = new DefaultPicoContainer();
            populateContainer(nested, rootElement);

            if (key != null) {
                container.addAdapter((ComponentAdapter) nested.getComponent(key));
            } else if (klass != null) {
                Class clazz = getClassLoader().loadClass(klass);
                container.addAdapter((ComponentAdapter) nested.getComponent(clazz));
            } else {
                container.addAdapter(nested.getComponent(ComponentAdapter.class));
            }
        } catch (ClassNotFoundException ex) {
            throw new NanoContainerMarkupException(ex);
        }

    }

    /**
     * process implementation node
     * @param container
     * @param rootElement
     * @throws ClassNotFoundException
     */
    protected void insertImplementation(MutablePicoContainer container, Element rootElement) throws ClassNotFoundException {
        String key = rootElement.getAttribute(KEY);
        String klass = rootElement.getAttribute(CLASS);
        String constructor = rootElement.getAttribute(CONSTRUCTOR);
        if (klass == null || "".equals(klass)) {
            throw new NanoContainerMarkupException("class specification is required for component implementation");
        }

        Class clazz = getClassLoader().loadClass(klass);

        List parameters = new ArrayList();

        NodeList children = rootElement.getChildNodes();
        Node child;
        String name;
        String dependencyKey;
        String dependencyClass;
        Object parseResult;

        for (int i = 0; i < children.getLength(); i++) {
            child = children.item(i);
            if (child.getNodeType() == Document.ELEMENT_NODE) {
                name = child.getNodeName();
                // constant parameter. it does not have any attributes.
                if (CONSTANT.equals(name)) {
                    // create constant with xstream
                    parseResult = parseElementChild((Element) child);
                    if (parseResult == null) {
                        throw new NanoContainerMarkupException("could not parse constant parameter");
                    }
                    parameters.add(new ConstantParameter(parseResult));
                } else if (DEPENDENCY.equals(name)) {
                    // either key or class must be present. not both
                    // key has prececence
                    dependencyKey = ((Element) child).getAttribute(KEY);
                    if (dependencyKey == null || "".equals(dependencyKey)) {
                        dependencyClass = ((Element) child).getAttribute(CLASS);
                        if (dependencyClass == null || "".equals(dependencyClass)) {
                            throw new NanoContainerMarkupException("either key or class must be present for dependency");
                        } else {
                            parameters.add(new ComponentParameter(getClassLoader().loadClass(dependencyClass)));
                        }
                    } else {
                        parameters.add(new ComponentParameter(dependencyKey));
                    }
                }
            }
        }

        // ok , we processed our children. insert implementation
        Parameter[] parameterArray = (Parameter[]) parameters.toArray(new Parameter[parameters.size()]);
        if (parameters.size() > 0 || "default".equals(constructor)) {
            if (parameterArray.length == 0) {
                parameterArray = Parameter.ZERO;
            }
            if (key == null || "".equals(key)) {
                // without  key. clazz is our key
                container.addComponent(clazz, clazz, parameterArray);
            } else {
                // with key
                container.addComponent(key, clazz, parameterArray);
            }
        } else {
            if (key == null || "".equals(key)) {
                // without  key. clazz is our key
                container.addComponent(clazz, clazz);
            } else {
                // with key
                container.addComponent(key, clazz);
            }

        }
    }

    /**
     * process instance node. we get key from atributes ( if any ) and leave content
     * to xstream. we allow only one child node inside. ( first  one wins )
     * @param container
     * @param rootElement
     */
    protected void insertInstance(MutablePicoContainer container, Element rootElement) {
        String key = rootElement.getAttribute(KEY);
        Object result = parseElementChild(rootElement);
        if (result == null) {
            throw new NanoContainerMarkupException("no content could be parsed in instance");
        }
        if (key != null && !"".equals(key)) {
            // insert with key
            container.addComponent(key, result);
        } else {
            // or without
            container.addComponent(result);
        }
    }

    /**
     * parse element child with xstream and provide object
     * @return
     * @param rootElement
     */
    protected Object parseElementChild(Element rootElement) {
        NodeList children = rootElement.getChildNodes();
        Node child;
        for (int i = 0; i < children.getLength(); i++) {
            child = children.item(i);
            if (child.getNodeType() == Document.ELEMENT_NODE) {
                return (new XStream(xsdriver)).unmarshal(new DomReader((Element) child));
            }
        }
        return null;
    }

    protected PicoContainer createContainerFromScript(PicoContainer parentContainer, Object assemblyScope) {
        try {
            ComponentFactory componentFactory;
            String componentFactoryName = rootElement.getAttribute("componentadapterfactory");
            if ("".equals(componentFactoryName) || componentFactoryName == null) {
                componentFactory = new CachingBehaviorFactory().forThis(new ConstructorInjectionFactory());
            } else {
                Class componentFactoryClass = getClassLoader().loadClass(componentFactoryName);
                componentFactory = (ComponentFactory) componentFactoryClass.newInstance();
            }
            MutablePicoContainer picoContainer = new DefaultPicoContainer(componentFactory);
            DefaultNanoContainer nano = new DefaultNanoContainer(getClassLoader(), picoContainer);
            populateContainer(nano);
            return nano;
        } catch (ClassNotFoundException e) {
            throw new NanoContainerMarkupException(e);
        } catch (InstantiationException e) {
            throw new NanoContainerMarkupException(e);
        } catch (IllegalAccessException e) {
            throw new NanoContainerMarkupException(e);
        }
    }
}
