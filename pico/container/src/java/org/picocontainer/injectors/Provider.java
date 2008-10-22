/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Characteristics;

import java.lang.reflect.Type;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * Providers are a type of Injector that can participate in Injection via a custom method.
 *
 * Implementors of this class must implement a single method called provide.  That method must return
 * the component type intended to be provided.  The method can accept parameters that PicoContainer
 * will satisfy.
 */
public interface Provider {
    
}
