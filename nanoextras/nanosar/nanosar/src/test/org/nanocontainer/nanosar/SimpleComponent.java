package org.nanocontainer.nanosar;
/**
 * simple dumb JMX component
 * @author k.pribluda
 *
 */
public class SimpleComponent implements SimpleComponentMBean {

	public String getFoo() {

		return "foo";
	}
}
