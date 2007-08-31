package org.nanocontainer.nanosar;

import java.util.Hashtable;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;


import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.behaviors.AbstractBehavior;

/**
 * alternative implementation of JMX exposition
 * for components. goal is to have really simple constructor,
 * to ease creation of scripting systems. 
 * 
 * reference to MBean server shall be provided by visitor, 
 * as it may be unavailable at the moment of container creation
 * @author k.pribluda
 * 
 */
public class  SimpleJMXExposed<T> extends AbstractBehavior<T> {

	ObjectName name;
	MBeanServer server;
	
	/**
	 * whether component was registered at mbean server and 
	 * needs disposing
	 */
	boolean registered = false;
	
	public SimpleJMXExposed(ComponentAdapter<T> delegate) {
		super(delegate);
	}

	
	public SimpleJMXExposed(ComponentAdapter<T> delegate, ObjectName objectName) {
		super(delegate);
		name = objectName;
	}


	/**
	 * unbind object from JNDI on disposal. if registered
	 */
    public void dispose(Object component) {
    	// dergister component
    	deregister();
		if( super.hasLifecycle( getComponentImplementation( ) ) ) {
			super.dispose(component);
		}
	}

	/**
	 * name to register at mbean server. default name will be choosen 
	 * if not provided. 
	 * @return
	 */
	public ObjectName getName() {
		return name;
	}


	public void setName(ObjectName name) {
		this.name = name;
	}
	
	/**
	 * register bean to server if possible. 
	 * @param srv mbean server to register to 
	 * @param baseName base object name. 
	 * @throws PicoCompositionException 
	 * @throws NotCompliantMBeanException 
	 * @throws MBeanRegistrationException 
	 * @throws InstanceAlreadyExistsException 
	 */
	@SuppressWarnings("unchecked")
	public void register(PicoContainer container, MBeanServer srv, ObjectName baseName)  {
		// dergister component - just in case we are rebinding 
		// to different server. 
		deregister();
		
		// register component
		this.server = srv;
		
		// establish name from base name if not already set
		if(getName() == null) {
			Hashtable table = baseName.getKeyPropertyList();
			table.put("key",getComponentKey().toString());
			try {
				setName(new ObjectName(baseName.getDomain(),table));
			} catch (JMException e) {
				throw new PicoCompositionException(e);
			}
		}
		
		try {
			server.registerMBean(getComponentInstance(container),getName());
		} catch (JMException e) {
			throw new PicoCompositionException(e);
		}
		
		
		registered = true;
	}
	
	/**
	 * deregister bean in case it was registered
	 *
	 */
	public void deregister() {
        if( registered ) {
            try {
				server.unregisterMBean(getName());
			} catch (JMException e) {
				throw new PicoCompositionException(e);
			}
        }		
	}

    public String getDescriptor() {
        return "SimpleJMXExposed";
    }
}
