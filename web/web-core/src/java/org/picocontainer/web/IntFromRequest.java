/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web;

import javax.servlet.http.HttpServletRequest;

/**
 * Use this to make a request level component that pulls an integer from a named parameter (GET or POST)
 * of the request.  If a parameter of the supplied name is not available for the current
 * request path, then an exception will be thrown. An exception will also be thrown, if the number format is bad.
 */
public class IntFromRequest extends StringFromRequest {

    public IntFromRequest(String paramName) {
        super(paramName);
    }

    @Override
    public Class getComponentImplementation() {
        return Integer.class;
    }

    @Override
    public Object provide(HttpServletRequest req) {
        String num = (String) super.provide(req);
        try {
            return Integer.parseInt(num);
        } catch (NumberFormatException e) {
            throw new RuntimeException("'" + num + "' cannot be converted to an integer");
        }
    }
}
