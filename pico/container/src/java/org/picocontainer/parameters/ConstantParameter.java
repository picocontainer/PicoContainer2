/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Jon Tirsen                        *
 *****************************************************************************/

package org.picocontainer.parameters;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.ParameterName;

import java.io.Serializable;
import java.lang.reflect.Field;


/**
 * A ConstantParameter should be used to pass in "constant" arguments to constructors. This
 * includes {@link String}s,{@link Integer}s or any other object that is not registered in
 * the container.
 *
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @author Thomas Heller
 * @version $Revision$
 */
public class ConstantParameter
        implements Parameter, Serializable {

    private final Object value;

    public ConstantParameter(Object value) {
        this.value = value;
    }

    public Object resolveInstance(PicoContainer container, ComponentAdapter adapter, Class expectedType, ParameterName expectedParameterName) {
        return value;
    }

    public boolean isResolvable(PicoContainer container, ComponentAdapter adapter, Class expectedType, ParameterName expectedParameterName) {
        try {
            verify(container, adapter, expectedType, expectedParameterName);
            return true;
        } catch(final PicoCompositionException e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.picocontainer.Parameter#verify(org.picocontainer.PicoContainer,org.picocontainer.ComponentAdapter,Class,org.picocontainer.ParameterName)
     */
    public void verify(PicoContainer container, ComponentAdapter adapter, Class expectedType, ParameterName expectedParameterName) throws PicoException {
        if (!checkPrimitive(expectedType) && !expectedType.isInstance(value)) {
            throw new PicoCompositionException(expectedType.getClass().getName() + " is not assignable from " +
                                                 (value != null ? value.getClass().getName() : "null"));
        }
    }

    /**
     * Visit the current {@link Parameter}.
     *
     * @see org.picocontainer.Parameter#accept(org.picocontainer.PicoVisitor)
     */
    public void accept(final PicoVisitor visitor) {
        visitor.visitParameter(this);
    }

    private boolean checkPrimitive(Class expectedType) {
        try {
            if (expectedType.isPrimitive()) {
                final Field field = value.getClass().getField("TYPE");
                final Class type = (Class) field.get(value);
                return expectedType.isAssignableFrom(type);
            }
        } catch (NoSuchFieldException e) {
            //ignore
        } catch (IllegalAccessException e) {
            //ignore
        }
        return false;
    }

}
