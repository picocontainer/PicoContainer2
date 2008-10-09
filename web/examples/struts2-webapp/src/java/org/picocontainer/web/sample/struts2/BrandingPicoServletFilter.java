/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.sample.struts2;

import org.picocontainer.web.struts2.PicoObjectFactory;
import org.picocontainer.web.sample.service.Brand;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Characteristics;

public class BrandingPicoServletFilter extends PicoObjectFactory.ServletFilter {

    protected void initAdditionalScopedComponents(MutablePicoContainer sessionContainer, MutablePicoContainer reqContainer) {
        reqContainer.as(Characteristics.NO_CACHE).addComponent(Brand.class, BrandFromRequest.class);
    }
}
