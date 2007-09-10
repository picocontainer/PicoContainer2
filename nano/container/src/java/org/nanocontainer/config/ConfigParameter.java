package org.nanocontainer.config;

import org.nanocontainer.integrationkit.PicoCompositionException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ParameterName;
import org.picocontainer.PicoContainer;
import org.picocontainer.parameters.BasicComponentParameter;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * represents parameter out from config entry.
 * config entries are strings ( properties or CL parameters )
 * and this require conversion  to desired type.
 * 
 * 
 * @author k.pribluda
 *
 */
@SuppressWarnings("serial")
public class ConfigParameter extends BasicComponentParameter {

	
	/**
	 * config parameters are always string keyed
	 * @param key
	 */
	public ConfigParameter(String key) {
		super(key);
	}
	
	/**
	 * we resolve instance against string
	 */
	@SuppressWarnings("unchecked")
	public <T> T resolveInstance(PicoContainer container,
			ComponentAdapter adapter, Class<T> expectedType,
			ParameterName expectedParameterName, boolean useNames) {
		String  result = (String) super.resolveInstance(container,adapter,String.class,expectedParameterName,useNames);
		SingleValueConverter converter = ConverterUtils.getConverter(expectedType);
		if(converter == null) {
			throw new NoConverterAvailableException("unable to find converter from string for class:" + expectedType);
		}
		return (T) converter.fromString(result);
	}

	/**
	 * whether target adaper can be resolved. 
	 * we explicitely look to string adapters,
	 * as we will convert it later
	 */
	public boolean isResolvable(PicoContainer container,
			ComponentAdapter adapter, Class expectedType,
			ParameterName expectedParameterName, boolean useNames) {
		return super.isResolvable(container,adapter,String.class,expectedParameterName,useNames);
	}

	/**
	 * we verify that we can find config entry (typed by string with
	 * correct name).  but we will check that supplied expectedType
	 * can be converted from string  
	 */
	public void verify(PicoContainer container, ComponentAdapter adapter,
			Class expectedType, ParameterName expectedParameterName,
			boolean useNames) {
		super.verify(container,adapter,String.class,expectedParameterName,useNames);
	}

	public static final class NoConverterAvailableException extends PicoCompositionException {

		public NoConverterAvailableException(String message) {
			super(message);
		}
	}
}
