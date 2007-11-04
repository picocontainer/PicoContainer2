/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import org.nanocontainer.integrationkit.ContainerComposer;

/**
 * Attribute keys used to store containers in various webapp scopes.
 * 
 * @author Joe Walnes
 */
public interface KeyConstants {
    
    String BUILDER = "nanocontainer.builder";    
    String ACTIONS_CONTAINER = "nanocontainer.actions";
    /**
     * if this parameter is present system properties pico container will
     * be created and made parent to properties container 
     * (if any)
     */
    String SYSTEM_PROPERTIES_CONTAINER = "nanocontainer.systemproperties";
    /**
     * location of properties file on the classpath of 
     * web application. if this parameter is specified
     * special properties pico container will be created and 
     * made parent of application scope container.  
     */
    String PROPERTIES_CONTAINER = "nanocontainer.properties";
    String APPLICATION_CONTAINER = "nanocontainer.application";
    String SESSION_CONTAINER = "nanocontainer.session";
    String REQUEST_CONTAINER = "nanocontainer.request";

    String NANOCONTAINER_PREFIX = "nanocontainer";
    String CONTAINER_COMPOSER = ContainerComposer.class.getName();
    String CONTAINER_COMPOSER_CONFIGURATION = CONTAINER_COMPOSER + ".configuration";
    String KILLER_HELPER = "KILLER_HELPER";

}
