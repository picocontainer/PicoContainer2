/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults.issues;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;

public class Issue0196TestCase extends TestCase {
    public static class Descriptor {
        public static class DescriptorData {
        }
    }
    public static class DescriptorDep {
        public DescriptorDep(Descriptor.DescriptorData[] datas) {
            Assert.assertEquals(3, datas.length);
            Assert.assertNull(datas[0]);
            Assert.assertNull(datas[1]);
            Assert.assertNull(datas[2]);
        }
    }

    public void testShouldAllowRegistrationOfArrayAsInstance() {
        MutablePicoContainer pico = new DefaultPicoContainer();

        Descriptor.DescriptorData[] datas = new Descriptor.DescriptorData[3];

        pico.addComponent(datas);
        pico.addComponent(DescriptorDep.class);

        DescriptorDep descriptorDep = pico.getComponent(DescriptorDep.class);

        assertNotNull(descriptorDep);
    }
}
