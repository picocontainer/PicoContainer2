/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved. 
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file. 
 ******************************************************************************/
package org.picocontainer.script;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;

import java.net.URL;

/**
 * A ScriptedPicoContainer is used primarily by the various
 * {@link org.picocontainer.script.ScriptedContainerBuilder ScriptedContainerBuilder}
 * implementations in the org.picocontainer.script.[scripting engine] packages.
 * 
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 */
public interface ScriptedPicoContainer extends MutablePicoContainer {

    /**
     * Adds a new URL that will be used in classloading
     * 
     * @param url url of the jar to find components in.
     * @return ClassPathElement to add permissions to (subject to security
     *         policy)
     */
    ClassPathElement addClassLoaderURL(URL url);

    /**
     * Returns class loader that is the aggregate of the URLs added.
     * 
     * @return A ClassLoader
     */
    ClassLoader getComponentClassLoader();

    /**
     * Make a child container with a given name
     * 
     * @param name the container name
     * @return The ScriptedPicoContainer
     */
    ScriptedPicoContainer makeChildContainer(String name);

    /**
     * Addes a child container with a given name
     * 
     * @param name the container name
     * @param child the child PicoContainer
     */
    void addChildContainer(String name, PicoContainer child);

}
