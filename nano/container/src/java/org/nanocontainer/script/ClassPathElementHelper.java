/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.script;

import org.nanocontainer.ClassPathElement;
import org.nanocontainer.NanoContainer;

import java.net.URL;
import java.net.MalformedURLException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.File;

public class ClassPathElementHelper {
    public static final String HTTP = "http://";

    public static ClassPathElement addClassPathElement(final String path, NanoContainer nanoContainer) {
        URL pathURL;
        try {
            if (path.toLowerCase().startsWith(HTTP)) {
                pathURL = new URL(path);
            } else {
                Object rVal = AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        try {
                            File file = new File(path);
                            if (!file.exists()) {
                                return new NanoContainerMarkupException("classpath '" + path + "' does not exist ");
                            }
                            return file.toURL();
                        } catch (MalformedURLException e) {
                            return e;
                        }

                    }
                });
                if (rVal instanceof MalformedURLException) {
                    throw (MalformedURLException) rVal;
                }
                if (rVal instanceof NanoContainerMarkupException) {
                    throw (NanoContainerMarkupException) rVal;
                }
                pathURL = (URL) rVal;
            }
        } catch (MalformedURLException e) {
            throw new NanoContainerMarkupException("classpath '" + path + "' malformed ", e);
        }
        return nanoContainer.addClassLoaderURL(pathURL);
    }
}
