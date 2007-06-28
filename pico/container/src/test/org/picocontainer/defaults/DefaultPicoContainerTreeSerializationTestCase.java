/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.tck.AbstractPicoContainerTestCase;

/**
 * @author Thomas Heller
 * @author Paul Hammant
 */
public class DefaultPicoContainerTreeSerializationTestCase extends AbstractPicoContainerTestCase {
    protected MutablePicoContainer createPicoContainer(PicoContainer parent) {
        return new DefaultPicoContainer(parent);
    }

    public void testContainerIsDeserializableWithParent() throws PicoException,
                                                                 IOException, ClassNotFoundException {

        PicoContainer parent = createPicoContainer(null);
        MutablePicoContainer child = createPicoContainer(parent);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(child);

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        child = (MutablePicoContainer) ois.readObject();
        assertNotNull(child.getParent());
    }
}
