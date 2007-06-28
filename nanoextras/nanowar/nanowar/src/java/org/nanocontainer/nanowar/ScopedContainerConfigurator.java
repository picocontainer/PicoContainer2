/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.nanowar;

/**
 * Configurator for the ScopedContainer
 * @author Mauro Talevi
 */
public final class ScopedContainerConfigurator {
	public final static String CONTAINER_BUILDER_KEY = "containerBuilder";   
	public final static String APPLICATION_CONFIG_KEY = "applicationConfig";   
	public final static String SESSION_CONFIG_KEY = "sessionConfig";   
	public final static String REQUEST_CONFIG_KEY = "requestConfig";   

	public final static String DEFAULT_CONTAINER_BUILDER = "org.nanocontainer.script.xml.XMLContainerBuilder";   
    public final static String DEFAULT_APPLICATION_CONFIG = "nanowar-application.xml";
    public final static String DEFAULT_SESSION_CONFIG = "nanowar-session.xml";
    public final static String DEFAULT_REQUEST_CONFIG = "nanowar-request.xml";

    private final String containerBuilder;
    private final String applicationConfig;
    private final String sessionConfig;
    private final String requestConfig;

    public ScopedContainerConfigurator(){
        this(DEFAULT_CONTAINER_BUILDER, DEFAULT_APPLICATION_CONFIG,
             DEFAULT_SESSION_CONFIG, DEFAULT_REQUEST_CONFIG);
    }
    
    public ScopedContainerConfigurator(String containerBuilder, String applicationConfig,
            						   String sessionConfig, String requestConfig){
        this.containerBuilder = containerBuilder;
        this.applicationConfig = applicationConfig;
        this.sessionConfig = sessionConfig;
        this.requestConfig = requestConfig;
    }
    
    public String getApplicationConfig() {
        return applicationConfig;
    }
    public String getContainerBuilder() {
        return containerBuilder;
    }
    public String getRequestConfig() {
        return requestConfig;
    }
    public String getSessionConfig() {
        return sessionConfig;
    }

}
