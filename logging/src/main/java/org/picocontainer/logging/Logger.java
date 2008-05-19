/*
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * --------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.picocontainer.logging;

import org.apache.commons.logging.Log;

/**
 * Facade for different Logger systems.
 * 
 * @author Mauro Talevi
 * @author Peter Donald 
 */
public interface Logger extends Log {
   
    /**
     * Get the child logger with specified name.
     * 
     * @param name the name of child logger
     * @return the child logger
     */
    Logger getChildLogger(String name);
}
