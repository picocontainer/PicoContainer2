/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.sample;

import org.picocontainer.web.sample.service.Brand;

import javax.servlet.http.HttpServletRequest;

public class BrandFromRequest implements Brand {

    private String name;

    public BrandFromRequest(HttpServletRequest req) {
        this.name = req.getRemoteHost().toUpperCase();
        if (name == null) {
            name = "";
        } else if ("127.0.0.1".equals(name)) {
            name = "testing-brand";
        }
    }

    public String getName() {
        return name;
    }

}
