/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.nanocontainer.remoting.jmx;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import junit.framework.TestCase;


/**
 * @author J&ouml;rg Schaible
 */
public class PredefinedObjectNameFactoryTest extends TestCase {

    public void testSpecifiedDomain() throws MalformedObjectNameException {
        final ObjectName key = new ObjectName("JUnit:type=null");
        final ObjectNameFactory factory = new PredefinedObjectNameFactory();
        final ObjectName objectName = factory.create(key, null);
        assertSame(key, objectName);
    }

}
