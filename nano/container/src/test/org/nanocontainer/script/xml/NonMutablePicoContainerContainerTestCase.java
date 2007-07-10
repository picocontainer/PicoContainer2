/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package org.nanocontainer.script.xml;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.ParameterName;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

import org.nanocontainer.integrationkit.PicoCompositionException;
import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.nanocontainer.testmodel.DefaultWebServerConfig;
import org.xml.sax.SAXException;

/**
 * @author Maarten Grootendorst
 * @version $Revision$
 */

// TODO to rename?
public class NonMutablePicoContainerContainerTestCase extends AbstractScriptedContainerBuilderTestCase {

    private class TestPicoContainer implements PicoContainer {
        public Object getComponent(Object componentKey) {
            return null;
        }

        public <T> T getComponent(Class<T> componentType) {
            return null;
        }

        public List getComponents() {
            return null;
        }

        public PicoContainer getParent() {
            return null;
        }

        public ComponentAdapter<?> getComponentAdapter(Object componentKey) {
            return null;
        }

        public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType, ParameterName componentParameterName) {
            return null;  
        }

        public Collection<ComponentAdapter<?>> getComponentAdapters() {
            return null;
        }

        public void addOrderedComponentAdapter(ComponentAdapter componentAdapter) {
        }

        public <T> List<T> getComponents(Class<T> type) throws PicoException {
            return null;
        }

        public void accept(PicoVisitor containerVisitor) {
        }

        public <T> List<ComponentAdapter<T>> getComponentAdapters(Class<T> componentType) {
            return null;
        }

        public void start() {
        }

        public void stop() {
        }

        public void dispose() {
        }

    }

    public void testCreateSimpleContainerWithPicoContainer()
        throws ParserConfigurationException, SAXException, IOException, PicoCompositionException
    {
        Reader script = new StringReader("" +
                                         "<container>" +
                                         "  <component-implementation class='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
                                         "  <component-implementation key='org.nanocontainer.testmodel.WebServer' class='org.nanocontainer.testmodel.WebServerImpl'/>" +
                                         "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()),
                                            new TestPicoContainer(),
                                            "SOME_SCOPE");
        assertEquals(2, pico.getComponents().size());
        assertNotNull(pico.getComponent(DefaultWebServerConfig.class));
    }

    public void testCreateSimpleContainerWithMutablePicoContainer()
        throws ParserConfigurationException, SAXException, IOException, PicoCompositionException
    {
        Reader script = new StringReader("" +
                                         "<container>" +
                                         "  <component-implementation class='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
                                         "  <component-implementation key='org.nanocontainer.testmodel.WebServer' class='org.nanocontainer.testmodel.WebServerImpl'/>" +
                                         "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()),
                                            new DefaultPicoContainer(),
                                            "SOME_SCOPE");
        assertEquals(2, pico.getComponents().size());
        assertNotNull(pico.getComponent(DefaultWebServerConfig.class));
        assertNotNull(pico.getParent());

    }
}
