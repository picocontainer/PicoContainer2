package org.picocontainer.gems.properties;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ParameterName;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;

/**
 * pico container backed by java.util.Properties instance. keys are always
 * strings, and type returned can be of any value as long it can be converted
 * from string.
 * 
 * as puprose of this container is to provide configuration values
 * ( which are referrred explicitely ), it will not retrieve by type
 * 
 * @author k.pribluda
 * 
 */
public class PropertiesPicoContainer implements PicoContainer {

	Properties properties;
	PicoContainer parent;
	
	Properties getProperties() {
		return properties;
	}

	public PropertiesPicoContainer(Properties properties) {
		this(properties,null);
	}

	public PropertiesPicoContainer(Properties properties, PicoContainer parent) {
		this.properties = properties;
		this.parent = parent;
	}

	void setProperties(Properties properties) {
		this.properties = properties;
	}

	public Object getComponent(Object componentKeyOrType) {
		return null;
	}

	public <T> T getComponent(Class<T> componentType) {
		return null;
	}

	public List getComponents() {
		return null;
	}

	public PicoContainer getParent() {
		return null;
	}

	public ComponentAdapter<?> getComponentAdapter(Object componentKey) {

		return null;
	}

	public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType,
			ParameterName componentParameterName) {
		return null;
	}

	public Collection<ComponentAdapter<?>> getComponentAdapters() {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> List<ComponentAdapter<T>> getComponentAdapters(
			Class<T> componentType) {
		return null;
	}

	public <T> List<T> getComponents(Class<T> componentType) {

		return null;
	}

	/**
	 * accept visitor for container only. no further traversal is necessary
	 */
	public void accept(PicoVisitor visitor) {
		visitor.visitContainer(this);
	}

}
