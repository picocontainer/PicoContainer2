/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer;

import java.lang.reflect.Type;

/**
 * Implementors are responsible for instantiating and injecting dependancies into
 * Constructors, Methods and Fields.
 */
public interface Injector<T> extends ComponentAdapter<T> {

    void decorateComponentInstance(PicoContainer container, Type into, T instance);

}
