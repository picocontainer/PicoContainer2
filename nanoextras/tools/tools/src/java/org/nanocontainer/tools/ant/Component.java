/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.tools.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicConfigurator;

import java.util.HashMap;
import java.util.Map;

/**
 * Instantiated by ant when the PicoContainer task element has a &lt;addComponent&gt;
 * element. Holds class name of the component and additional properties.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public final class Component implements DynamicConfigurator {
    private String className;
    private final Map properties = new HashMap();
    private String key;

    public void setClassname(String className) {
        this.className = className;
    }

    public String getClassname() {
        return className;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key != null ? key : getClassname();
    }

    public void setDynamicAttribute(String name, String value) throws BuildException {
        properties.put(name, value);
    }

    public Object createDynamicElement(String string) throws BuildException {
        throw new BuildException("No sub elements of " + string + " is allowed");
    }

    public Map getProperties() {
        return properties;
    }
}
