/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan                                           *
 *****************************************************************************/


package org.nanocontainer.script.groovy.buildernodes;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.nanocontainer.NanoContainer;
import org.nanocontainer.script.NodeBuilderDecorationDelegate;
import org.nanocontainer.script.ComponentElementHelper;
import org.picocontainer.Parameter;
import org.picocontainer.parameters.ConstantParameter;

/**
 *
 * @author James Strachan
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Michael Rimov
 * @author Mauro Talevi
 */
public class ComponentNode extends AbstractBuilderNode {

    public static final String NODE_NAME =  "component";

    /**
     * Attributes 'key'
     */
    public static final String KEY = "key";

    /**
     * Class attribute.
     */
    private static final String CLASS = "class";

    /**
     * Class Name Key Attribute.
     */
    private static final String CLASS_NAME_KEY = "classNameKey";

    /**
     * Instance attribute name.
     */
    private static final String INSTANCE = "instance";

    /**
     * Parameters attribute name.
     */
    private static final String PARAMETERS = "parameters";

    private static final String PROPERTIES = "properties";

    private final NodeBuilderDecorationDelegate delegate;

    public ComponentNode(NodeBuilderDecorationDelegate builderDelegate) {
        super(NODE_NAME);

        this.delegate = builderDelegate;


        //Supported attributes.
        this.addAttribute(KEY)
            .addAttribute(CLASS)
            .addAttribute(CLASS_NAME_KEY)
            .addAttribute(INSTANCE)
            .addAttribute(PARAMETERS)
            .addAttribute(PROPERTIES);
    }

    /**
     * Execute the handler for the given node builder.
     * @param current The current node.
     * @param attributes Map attributes specified in the groovy script for
     *   the builder node.
     * @return Object
     */
    public Object createNewNode(final Object current, final Map attributes) {
        delegate.rememberComponentKey(attributes);
        Object key = attributes.remove(KEY);
        Object cnkey = attributes.remove(CLASS_NAME_KEY);
        Object classValue = attributes.remove(CLASS);
        Object instance = attributes.remove(INSTANCE);
        Object parameters =  attributes.remove(PARAMETERS);
        List properties = (List) attributes.remove(PROPERTIES);

        return ComponentElementHelper.makeComponent(cnkey, key, getParameters(parameters), classValue, (NanoContainer) current, instance, getProperties(properties));
    }

    private static Parameter[] getParameters(Object params) {
        if (params == null) {
            return null;
        }
        
        if (params instanceof Parameter[]){
        	return (Parameter[])params;
        } 
        
        if (! (params instanceof List)) {
        	throw new IllegalArgumentException("Parameters may only be of type List or Parameter Array");
        }
        
        List paramsList = (List)params;
        
        int n = paramsList.size();
        Parameter[] parameters = new Parameter[n];
        for (int i = 0; i < n; ++i) {
            parameters[i] = toParameter(paramsList.get(i));
        }
        return parameters;
    }

    private static Properties[] getProperties(List propsList) {
        if (propsList == null) {
            return new Properties[0];
        }
        int n = propsList.size();
        Properties[] properties = new Properties[n];
        for (int i = 0; i < n; ++i) {
            properties[i] = (Properties) propsList.get(i);
        }
        return properties;
    }



    private static Parameter toParameter(Object obj) {
        return obj instanceof Parameter ? (Parameter) obj : new ConstantParameter(obj);
    }


}
