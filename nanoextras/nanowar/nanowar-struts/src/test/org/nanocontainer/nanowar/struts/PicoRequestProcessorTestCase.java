/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.struts;

import java.io.IOException;


/**
 * @author Stephen Molitor
 */
public class PicoRequestProcessorTestCase extends AbstractActionTestCase {

    public void testProcessActionCreate() throws IOException {
        PicoRequestProcessor requestProcessor = new PicoRequestProcessor();
        TestAction action = (TestAction) requestProcessor.processActionCreate(request, response, mapping);
        assertNotNull(action);
        assertSame(service, action.getService());
    }

}
