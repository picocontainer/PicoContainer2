/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *****************************************************************************/

package org.picocontainer.persistence.hibernate.annotations;

import java.io.File;
import java.net.URL;

import org.hibernate.cfg.Configuration;
import org.w3c.dom.Document;

/**
 * Constructable Hibernate configuration. Just a wrapper around various
 * configure() methods. See respective {@link org.hibernate.cfg.Configuration configure methods}.
 * 
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 * @see org.hibernate.cfg.Configuration
 */
@SuppressWarnings("serial")
public class ConstructableConfiguration extends Configuration {

    public ConstructableConfiguration() {
        this.configure();
    }

    public ConstructableConfiguration(URL url) {
        this.configure(url);
    }

    public ConstructableConfiguration(String resource) {
        this.configure(resource);
    }

    public ConstructableConfiguration(File configFile) {
        this.configure(configFile);
    }

    public ConstructableConfiguration(Document document) {
        this.configure(document);
    }

}
