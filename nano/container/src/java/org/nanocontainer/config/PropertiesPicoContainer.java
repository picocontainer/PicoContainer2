package org.nanocontainer.config;

import java.util.Properties;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.containers.AbstractDelegatingMutablePicoContainer;
import org.picocontainer.containers.AbstractDelegatingPicoContainer;

/**
 * immutable pico container constructed from properties.
 * intendet to be used with config parameter
 * 
 * @author k.pribluda
 *
 */
@SuppressWarnings("serial")
public class PropertiesPicoContainer extends AbstractDelegatingPicoContainer {

	
	
	/**
	 * create with parent container and populate from properties
	 * @param properties
	 * @param parent
	 */
	public PropertiesPicoContainer(Properties properties, PicoContainer parent) {
		super(new DefaultPicoContainer(parent));		
		// populate container from properties
		for(Object key: properties.keySet()) {
			((MutablePicoContainer)getDelegate()).addComponent(key,properties.get(key));
		}
	}
	
	/**
	 * construct without a parent
	 * @param properties
	 */
	public PropertiesPicoContainer(Properties properties) {
		this(properties,null);
	}
}
