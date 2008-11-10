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
import javax.servlet.http.Cookie;

public class StringFromCookie extends ProviderAdapter {

    private final String name;

    public StringFromCookie(String name) {
        this.name = name;
    }

    public Class getComponentImplementation() {
        return String.class;
    }

    public Object getComponentKey() {
        return name;
    }

    public Object provide(HttpServletRequest req) {
        return "Gil Bates";
//        Cookie[] cookies = req.getCookies();
//        for (Cookie cookie : cookies) {
//            if (cookie.getName().equals(name)) {
//                return cookie.getValue();
//            }
//        }
//        throw new RuntimeException(name + " not provided");
    }
}