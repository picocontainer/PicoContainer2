/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web;

import javax.servlet.http.HttpServletRequest;

public class IntFromRequest extends StringFromRequest {

    public IntFromRequest(String paramName) {
        super(paramName);
    }

    public Class getComponentImplementation() {
        return Integer.class;
    }

    public Object provide(HttpServletRequest req) {
        String num = (String) super.provide(req);
        return Integer.parseInt(num);
    }
}
