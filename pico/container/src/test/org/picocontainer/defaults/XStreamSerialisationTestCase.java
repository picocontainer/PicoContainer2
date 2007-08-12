/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.xml.XppDriver;
import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;

/**
 * @author Aslak Helles&oslash;y
 */
public final class XStreamSerialisationTestCase extends TestCase {
    private final XStream xStream = new XStream(new XppDriver());

    public void testShouldBeAbleToSerialiseEmptyPico() {
        if (JVM.is14()) {
            MutablePicoContainer pico = new DefaultPicoContainer();
            String picoXml = xStream.toXML(pico);
            PicoContainer serializedPico = (PicoContainer) xStream.fromXML(picoXml);

            assertEquals(0, serializedPico.getComponents().size());
        }
    }

    public void testShouldBeAbleToSerialisePicoWithUninstantiatedComponents() {
        if (JVM.is14()) {
            MutablePicoContainer pico = new DefaultPicoContainer();
            pico.addComponent(SimpleTouchable.class);
            pico.addComponent(DependsOnTouchable.class);
            String picoXml = xStream.toXML(pico);
            PicoContainer serializedPico = (PicoContainer) xStream.fromXML(picoXml);

            assertEquals(2, serializedPico.getComponents().size());
        }
    }

    public void testShouldBeAbleToSerialisePicoWithInstantiatedComponents() {
        if (JVM.is14()) {
            MutablePicoContainer pico = new DefaultPicoContainer();
            pico.addComponent(SimpleTouchable.class);
            pico.addComponent(DependsOnTouchable.class);
            pico.getComponents();
            String picoXml = xStream.toXML(pico);
            PicoContainer serializedPico = (PicoContainer) xStream.fromXML(picoXml);

            assertEquals(2, serializedPico.getComponents().size());
        }
    }
}