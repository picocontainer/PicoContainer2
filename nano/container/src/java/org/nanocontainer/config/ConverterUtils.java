package org.nanocontainer.config;

import java.util.ArrayList;
import java.util.HashMap;

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
import com.thoughtworks.xstream.converters.extended.CharsetConverter;
import com.thoughtworks.xstream.converters.extended.CurrencyConverter;
import com.thoughtworks.xstream.converters.extended.FileConverter;
import com.thoughtworks.xstream.converters.extended.ISO8601DateConverter;
import com.thoughtworks.xstream.converters.extended.ISO8601GregorianCalendarConverter;
import com.thoughtworks.xstream.converters.extended.ISO8601SqlTimestampConverter;
import com.thoughtworks.xstream.converters.extended.JavaClassConverter;
import com.thoughtworks.xstream.converters.extended.LocaleConverter;
import com.thoughtworks.xstream.converters.extended.SqlDateConverter;
import com.thoughtworks.xstream.converters.extended.SqlTimeConverter;
import com.thoughtworks.xstream.converters.extended.SqlTimestampConverter;
import com.thoughtworks.xstream.converters.extended.StackTraceElementConverter;
import com.thoughtworks.xstream.converters.extended.TextAttributeConverter;
import com.thoughtworks.xstream.converters.extended.ToStringConverter;

/**
 * utility class to map converters ( stolen from xstream ) to classes. do it
 * dynamically on by case basis.
 * 
 * @author k.pribluda
 * 
 */
public class ConverterUtils {

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
		converters.add(new ISO8601GregorianCalendarConverter());
		converters.add(new ISO8601DateConverter());
		converters.add(new ISO8601SqlTimestampConverter());
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
			for(SingleValueConverter candidate: converterMap.values()) {
				if(candidate.canConvert(clazz)) {
					converter = candidate;
					converterMap.put(clazz,converter);
				}
			}
		}
		return converter;
	}
}
