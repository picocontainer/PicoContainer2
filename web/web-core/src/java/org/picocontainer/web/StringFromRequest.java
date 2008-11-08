/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/

package org.picocontainer.web;

import org.picocontainer.injectors.ProviderAdapter;

import javax.servlet.http.HttpServletRequest;

public class StringFromRequest extends ProviderAdapter {
    private final String paramName;

    public StringFromRequest(String paramName) {
        this.paramName = paramName;
    }

    public Class getComponentImplementation() {
        return String.class;
    }

    public Object getComponentKey() {
        return paramName;
    }

    public Object provide(HttpServletRequest req) {
        String parameter = req.getParameter(paramName);
        if (parameter == null) {
            throw new RuntimeException(paramName + " not provided");
        }
        return parameter;
    }
}
