package org.picocontainer.gems.jndi;

import java.util.Properties;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentFactory;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;

/**
 * TODO: decide where to get JNDI name as we do not have 
 * implementation here. ? Property
 * @author k.pribluda
 *
 */
public class JNDIProviding implements ComponentFactory {

	public <T> ComponentAdapter<T> createComponentAdapter(
			ComponentMonitor componentMonitor,
			LifecycleStrategy lifecycleStrategy,
			Properties componentProperties, Object componentKey,
			Class<T> componentImplementation, Parameter... parameters)
			throws PicoCompositionException {
		return null;
	}

}
