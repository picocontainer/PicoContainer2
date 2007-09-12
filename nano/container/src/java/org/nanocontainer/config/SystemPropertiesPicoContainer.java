package org.nanocontainer.config;

import org.picocontainer.PicoContainer;

/**
 * container backed by system properties. 
 * @author k.pribluda
 */
@SuppressWarnings("serial")
public class SystemPropertiesPicoContainer extends PropertiesPicoContainer {

	public SystemPropertiesPicoContainer() {
		this(null);
	}
	public SystemPropertiesPicoContainer(PicoContainer parent) {
		super(System.getProperties(),parent);
	}
}
