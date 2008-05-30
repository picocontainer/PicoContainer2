/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.persistence.hibernate.annotations;

import java.io.File;
import java.net.URL;

import org.hibernate.cfg.AnnotationConfiguration;
import org.w3c.dom.Document;

/**
 * This class handles the configuration with Hibernate's Annotation configuration.
 * See respective {@link org.hibernate.cfg.AnnotationConfiguration configure methods}.
 * 
 * @author Michael Rimov
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz> 
 */
@SuppressWarnings("serial")
public class ConstructableAnnotationConfiguration extends AnnotationConfiguration {

    public ConstructableAnnotationConfiguration() {
        this.configure();
    }

    public ConstructableAnnotationConfiguration(URL url) {
        this.configure(url);
    }

    public ConstructableAnnotationConfiguration(String resource) {
        this.configure(resource);
    }

    public ConstructableAnnotationConfiguration(File file) {
        this.configure(file);
    }

    public ConstructableAnnotationConfiguration(Document document) {
        this.configure(document);
    }

}
