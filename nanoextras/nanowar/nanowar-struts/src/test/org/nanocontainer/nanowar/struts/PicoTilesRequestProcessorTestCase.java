/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.struts;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.IOException;

import org.junit.Test;

/**
 * @author Stephen Molitor
 */
public class PicoTilesRequestProcessorTestCase extends AbstractActionTestCase {

    @Test public void testProcessActionCreate() throws IOException {
        PicoTilesRequestProcessor requestProcessor = new PicoTilesRequestProcessor();
        TestAction action = (TestAction) requestProcessor.processActionCreate(request, response, mapping);
        assertNotNull(action);
        assertSame(service, action.getService());
    }

}
