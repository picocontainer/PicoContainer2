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
