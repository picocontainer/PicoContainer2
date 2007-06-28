/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.aop.dynaop;

import dynaop.Aspects;
import org.picocontainer.PicoCompositionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Represents the collection of component scoped aspects for a Pico container.
 * Manages a collection of <code>ComponentAspect</code> objects, and knows how
 * to register their aspects.
 *
 * @author Stephen Molitor
 * @version $Revision$
 */
class ComponentAspectsCollection {

    private final Collection componentsAspects = new ArrayList();

    /**
     * Adds a component aspect to this collection.
     *
     * @param componentAspect the component aspect to add.
     */
    void add(ComponentAspect componentAspect) {
        componentsAspects.add(componentAspect);
    }

    /**
     * Registers all aspects whose component pointcut matches
     * <code>componentKey</code>. Creates and returns a new
     * <code>dynaop.Aspects</code> object that is the union of the addComponent
     * and container scoped aspects. By copying the container scoped aspects to
     * a new <code>dynaop.Aspects</code> and adding the component aspects to
     * this new object, we avoid having to create proxies on top of proxies.
     *
     * @param componentKey     the component key.
     * @param containerAspects the container scoped aspects.
     * @return a new <code>dynaop.Aspects</code> object that contains
     *         everything in <code>containerAspects</code> plus the addComponent
     *         aspects that match <code>componentKey</code>.
     */
    Aspects registerAspects(Object componentKey, Aspects containerAspects) {
        Aspects aspects = copyAspects(containerAspects);
        for (Object componentsAspect : componentsAspects) {
            ComponentAspect componentAspect = (ComponentAspect)componentsAspect;
            componentAspect.registerAspect(componentKey, aspects);
        }
        return aspects;
    }

    private static Aspects copyAspects(Aspects aspects) {
        // TODO: Lobby Bob Lee to make the Aspects copy constructor public.
        try {
            Constructor constructor = getAspectsCopyConstructor();
            constructor.setAccessible(true);
            return (Aspects) constructor.newInstance(aspects);
        } catch (SecurityException e) {
            throw new PicoCompositionException("security exception copying dynaop.Aspects", e);
        } catch (IllegalArgumentException e) {
            throw new PicoCompositionException("illegal argument passed to dynaop.Aspects copy constructor", e);
        } catch (InstantiationException e) {
            throw new PicoCompositionException("error instantiating dynaop.Aspects copy constructor object", e);
        } catch (IllegalAccessException e) {
            throw new PicoCompositionException("illegal access exception while trying to make dynaop.Aspects copy constructor accessible", e);
        } catch (InvocationTargetException e) {
            throw new PicoCompositionException("dynaop.Aspects copy constructor threw an exception", e);
        }
    }

    private static Constructor getAspectsCopyConstructor() {
        final Class[] params = new Class[]{Aspects.class};
        Constructor[] constructors = Aspects.class.getDeclaredConstructors();
        for (Constructor constructor : constructors) {
            if (Arrays.equals(params, constructor.getParameterTypes())) {
                return constructor;
            }
        }
        throw new PicoCompositionException("dynaop.Aspects copy constructor not found");
    }

}