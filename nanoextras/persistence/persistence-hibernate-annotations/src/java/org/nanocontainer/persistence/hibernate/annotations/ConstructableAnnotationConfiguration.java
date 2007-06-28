/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Centerline Computers, Inc.                               *
 *****************************************************************************/
package org.nanocontainer.persistence.hibernate.annotations;

import java.io.File;
import java.net.URL;
import org.hibernate.HibernateException;
import org.hibernate.cfg.AnnotationConfiguration;
import org.w3c.dom.Document;

/**
 * This class handles the configuration with Hibernate's Annotation configuration.
 * It is often constructed with some reference to a hibernate configuration file.
 * @author Michael Rimov
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz> 
 */
public class ConstructableAnnotationConfiguration extends AnnotationConfiguration {

    /**
     * Serialization UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * @throws HibernateException
     */
    public ConstructableAnnotationConfiguration() {
        this.configure();
    }

    /**
     * Constructs an Annotation Configuration with the given configuration file
     * URL.
     * @param url
     * @throws HibernateException
     */
    public ConstructableAnnotationConfiguration(URL url) {
        this.configure(url);
    }

    /**
     * 
     * @param resource
     * @throws HibernateException
     */
    public ConstructableAnnotationConfiguration(String resource) {
        this.configure(resource);
    }

    /**
     * Constructs annotation configuration with a given <tt>java.io.File</tt>
     * @param configFile
     * @throws HibernateException
     */
    public ConstructableAnnotationConfiguration(File configFile) {
        this.configure(configFile);
    }

    /**
     * Constructs annotaion configuration with a parsed XML document.
     * @param document
     * @throws HibernateException
     */
    public ConstructableAnnotationConfiguration(Document document) {
        this.configure(document);
    }

}
