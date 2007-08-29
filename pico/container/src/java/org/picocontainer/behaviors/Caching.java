/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.Characteristics;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.behaviors.AbstractBehaviorFactory;
import org.picocontainer.references.SimpleReference;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ObjectReference;

import java.util.Properties;

/**
 * factory class creating cached behaviours
 * @author Aslak Helles&oslash;y
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @author Konstantin Pribluda
 */
public class Caching extends AbstractBehaviorFactory {

	public <T> ComponentAdapter<T> createComponentAdapter(
			ComponentMonitor componentMonitor,
			LifecycleStrategy lifecycleStrategy,
			Properties componentProperties, Object componentKey,
			Class<T> componentImplementation, Parameter... parameters)
			throws PicoCompositionException {
		if (removePropertiesIfPresent(componentProperties,
				Characteristics.NO_CACHE)) {
			return super.createComponentAdapter(componentMonitor,
					lifecycleStrategy, componentProperties, componentKey,
					componentImplementation, parameters);
		}
		removePropertiesIfPresent(componentProperties, Characteristics.CACHE);
		return new Cached<T>(super.createComponentAdapter(componentMonitor,
				lifecycleStrategy, componentProperties, componentKey,
				componentImplementation, parameters), newObjectReference());

	}

	public <T> ComponentAdapter<T> addComponentAdapter(
			ComponentMonitor componentMonitor,
			LifecycleStrategy lifecycleStrategy,
			Properties componentProperties, ComponentAdapter<T> adapter) {
		if (removePropertiesIfPresent(componentProperties,
				Characteristics.NO_CACHE)) {
			return super.addComponentAdapter(componentMonitor,
					lifecycleStrategy, componentProperties, adapter);
		}
		removePropertiesIfPresent(componentProperties, Characteristics.CACHE);
		return new Cached<T>(super.addComponentAdapter(componentMonitor,
				lifecycleStrategy, componentProperties, adapter),
				newObjectReference());
	}

	protected <T> ObjectReference<T> newObjectReference() {
		return new SimpleReference<T>();
	}
}
