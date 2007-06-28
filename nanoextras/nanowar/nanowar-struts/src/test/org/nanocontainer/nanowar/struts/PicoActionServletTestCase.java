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
public class PicoActionServletTestCase extends AbstractActionTestCase {

    public void testProcessActionCreate() throws IOException {
        PicoActionServlet servlet = new PicoActionServlet();
        StrutsTestAction action = (StrutsTestAction) servlet.processActionCreate(mapping, request);
        assertNotNull(action);
        assertSame(service, action.getService());
        assertSame(servlet, action.getServlet());
    }

}
