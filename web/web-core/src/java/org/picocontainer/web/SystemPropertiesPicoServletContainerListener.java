package org.picocontainer.web;

import org.picocontainer.PicoContainer;
import org.picocontainer.containers.SystemPropertiesPicoContainer;

public class SystemPropertiesPicoServletContainerListener extends PicoServletContainerListener {

    private static final long serialVersionUID = -8261455022408211321L;

    protected PicoContainer makeParentContainer() {
        return new SystemPropertiesPicoContainer();
    }
}
