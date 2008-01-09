/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

/**
 * TestCase for ContainerComposerMocker
 *
 * @author Konstantin Pribluda ( konstantin.pribluda[at]infodesire.com )
 * @version $Revision$
 */
public class ContainerComposerMockerTestCase implements KeyConstants {

    // FIXME @Test 
	// Hmmm, a stop() is being called on DPC, when it already disposed.
    public void testThatItMocksProperly() {

        ContainerComposerMocker mocker = new ContainerComposerMocker(XStreamContainerComposer.class);
        assertNull(mocker.getApplicationContainer());
        assertNull(mocker.getSessionContainer());
        assertNull(mocker.getRequestContainer());

        mocker.startApplication();
        assertNotNull(mocker.getApplicationContainer());
        mocker.startSession();
        assertNotNull(mocker.getSessionContainer());
        mocker.startRequest();
        assertNotNull(mocker.getRequestContainer());

        assertNotNull(mocker.getApplicationContainer().getComponent("applicationScopedInstance"));
        assertNotNull(mocker.getSessionContainer().getComponent("applicationScopedInstance"));
        assertNotNull(mocker.getRequestContainer().getComponent("applicationScopedInstance"));

        assertNotNull(mocker.getRequestContainer().getComponent("requestScopedInstance"));

        mocker.stopRequest();

        assertNull(mocker.getRequestContainer());

        mocker.startRequest();
        assertNotNull(mocker.getRequestContainer());

        assertSame(mocker.getApplicationContainer().getComponent("applicationScopedInstance"),
                mocker.getRequestContainer().getComponent("applicationScopedInstance"));

        mocker.stopApplication();

        assertNull(mocker.getApplicationContainer());
        assertNull(mocker.getSessionContainer());
        assertNull(mocker.getRequestContainer());
    }

    @Test public void testFoo() {
        // boo
    }

}
