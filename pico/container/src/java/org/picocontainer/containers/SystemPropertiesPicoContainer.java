/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.containers;

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
