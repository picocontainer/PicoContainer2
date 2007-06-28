/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.picometer;

import junit.framework.TestCase;

import java.net.URL;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class AbstractPicoMeterTestCase extends TestCase {
    protected URL source;

    public static class Dummy {
    }

    public static class InstantiatesOne {
        Exception e = new Exception();
    }

    public static class InstantiatesThree {
        Dummy dummy1 =
                new Dummy();

        public InstantiatesThree() {
            Dummy dummy2 = new Dummy();
        }

        private void doIt() {
            Dummy dummy = new Dummy();
        }
    }

    public static class OneInjection {
        Dummy dummy;

        public OneInjection(Dummy dummy) {
            this.dummy = dummy;
        }
    }

    // putting this at the end makes test less brittle -- jon

    protected void setUp() throws Exception {
        source = findResource("org/nanocontainer/picometer/AbstractPicoMeterTestCase.java");
    }

    private URL findResource(String resourcePath) {
        URL resource = getClass().getResource("/" + resourcePath);
        assertNotNull("add " + resourcePath + " to the class-path", resource);
        return resource;
    }
}
