/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.gems.parameters;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ParameterName;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.parameters.BasicComponentParameter;

import java.util.HashMap;
import java.util.ArrayList;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.basic.BigDecimalConverter;
import com.thoughtworks.xstream.converters.basic.BigIntegerConverter;
import com.thoughtworks.xstream.converters.basic.BooleanConverter;
import com.thoughtworks.xstream.converters.basic.ByteConverter;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.converters.basic.DoubleConverter;
import com.thoughtworks.xstream.converters.basic.FloatConverter;
import com.thoughtworks.xstream.converters.basic.IntConverter;
import com.thoughtworks.xstream.converters.basic.LongConverter;
import com.thoughtworks.xstream.converters.basic.ShortConverter;
import com.thoughtworks.xstream.converters.basic.StringBufferConverter;
import com.thoughtworks.xstream.converters.basic.StringConverter;
import com.thoughtworks.xstream.converters.basic.URLConverter;
import com.thoughtworks.xstream.converters.extended.TextAttributeConverter;
import com.thoughtworks.xstream.converters.extended.CharsetConverter;
import com.thoughtworks.xstream.converters.extended.CurrencyConverter;
import com.thoughtworks.xstream.converters.extended.FileConverter;
import com.thoughtworks.xstream.converters.extended.JavaClassConverter;
import com.thoughtworks.xstream.converters.extended.LocaleConverter;
import com.thoughtworks.xstream.converters.extended.SqlDateConverter;
import com.thoughtworks.xstream.converters.extended.SqlTimeConverter;
import com.thoughtworks.xstream.converters.extended.SqlTimestampConverter;
import com.thoughtworks.xstream.converters.extended.StackTraceElementConverter;
import com.thoughtworks.xstream.converters.extended.ToStringConverter;

/**
 * Represents parameter out from config entry.
 * config entries are strings ( properties or CL parameters )
 * and this require conversion to desired type.
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
		// no need to convert it if nothing was found
		if(result == null) {
			return null;
		}
		
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

	/**
	 * exception to be thrown if no suitable parameter was found
	 * @author k.pribluda
	 */
	public static final class NoConverterAvailableException extends PicoCompositionException {

		public NoConverterAvailableException(String message) {
			super(message);
		}
	}

    public static class ConverterUtils {

        static final HashMap<Class,SingleValueConverter> converterMap =
            new HashMap<Class,SingleValueConverter>();
        static final ArrayList<SingleValueConverter> converters = new ArrayList<SingleValueConverter>();

        /**
         * read all defined converters into converters list
         */
        static {
            converters.add(new TextAttributeConverter());
            converters.add(new BigDecimalConverter());
            converters.add(new BigIntegerConverter());
            converters.add(new BooleanConverter());
            converters.add(new ByteConverter());
            converters.add(new CharsetConverter());
            converters.add(new CurrencyConverter());
            converters.add(new DateConverter());
            converters.add(new DoubleConverter());
            converters.add(new FileConverter());
            converters.add(new FloatConverter());
            converters.add(new IntConverter());
            // those need additional dependencies
            // disable them for now
            /*
            converters.add(new ISO8601GregorianCalendarConverter());
            converters.add(new ISO8601DateConverter());
            converters.add(new ISO8601SqlTimestampConverter());
            */
            converters.add(new JavaClassConverter());
            converters.add(new LocaleConverter());
            converters.add(new LongConverter());
            converters.add(new ShortConverter());
            converters.add(new SqlDateConverter());
            converters.add(new SqlTimeConverter());
            converters.add(new SqlTimestampConverter());
            converters.add(new StackTraceElementConverter());
            converters.add(new StringBufferConverter());
            converters.add(new StringConverter());
            // need generic one...
            //converters.add(new ToStringConverter());
            converters.add(new URLConverter());
        }

        /**
         * retrieve suitable converter for class
         * @param clazz class to be converted
         * @return converter if available
         */
        public static synchronized SingleValueConverter getConverter(Class clazz) {
            SingleValueConverter converter = converterMap.get(clazz);
            // converter not found.
            if(converter == null) {
                for(SingleValueConverter candidate: converters) {
                    if(candidate.canConvert(clazz)) {
                        converter = candidate;
                        converterMap.put(clazz,converter);
                    }
                }
                // fallback - try to utilize ToString converter
                if(converter == null) {
                    try {
                        converter = new ToStringConverter(clazz);
                    } catch (NoSuchMethodException e) {
                        // uups, this converter is not suitable
                        // forget about it silently ;)
                    }
                }
            }
            return converter;
        }
    }


}
