/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.nanning;

import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.Mixin;
import org.codehaus.nanning.config.Aspect;
import org.codehaus.nanning.config.AspectSystem;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DecoratingComponentAdapter;
import org.picocontainer.defaults.DecoratingComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.NotConcreteRegistrationException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Jon Tirsen
 * @version $Revision$
 */
public class NanningComponentAdapterFactory extends DecoratingComponentAdapterFactory implements Serializable {
    private AspectSystem aspectSystem;

    public NanningComponentAdapterFactory(AspectSystem aspectSystem,
                                          ComponentAdapterFactory delegate) {
        super(delegate);
        this.aspectSystem = aspectSystem;
    }

    public NanningComponentAdapterFactory() {
        this(new AspectSystem(), new DefaultComponentAdapterFactory());
    }

    public ComponentAdapter createComponentAdapter(Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter[] parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        ComponentAdapter componentAdapter = super.createComponentAdapter(componentKey, componentImplementation, parameters);

        if (Aspect.class.isAssignableFrom(componentImplementation)) {
            componentAdapter = new AspectAdapter(componentAdapter, aspectSystem);

        } else if (canBeWeaved(componentImplementation)) {
            Class componentInterface = getComponentInterface(componentImplementation);
            componentAdapter = new WeavingAdapter(componentAdapter, aspectSystem, componentInterface);
        }

        return componentAdapter;
    }

    private Class getComponentInterface(Class componentImplementation) {
        return (Class) getAllInterfaces(componentImplementation).get(0);
    }

    List getAllInterfaces(Class componentImplementation) {
        if (componentImplementation == null) {
            return Collections.EMPTY_LIST;
        }
        List result = new ArrayList(Arrays.asList(componentImplementation.getInterfaces()));
        result.addAll(getAllInterfaces(componentImplementation.getSuperclass()));
        return result;
    }

    private boolean canBeWeaved(Class componentImplementation) {
        return getAllInterfaces(componentImplementation).size() == 1;
    }

    public static class WeavingAdapter extends DecoratingComponentAdapter {

        private final AspectSystem aspectSystem;
        private Class componentInterface;

        public WeavingAdapter(ComponentAdapter delegate, AspectSystem aspectSystem, Class componentInterface) {
            super(delegate);
            this.aspectSystem = aspectSystem;
            this.componentInterface = componentInterface;
        }

        public Object getComponentInstance(PicoContainer pico) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
            Object component = super.getComponentInstance(pico);
            // TODO Nanning will at the moment only aspectify stuff when it has one and only one interface

            // the trick: set up first mixin manually with the component as target
            AspectInstance aspectInstance = new AspectInstance(componentInterface);
            Mixin mixin = new Mixin(componentInterface, component);
            aspectInstance.addMixin(mixin);

            // let the aspects do its work
            getAspectSystem().initialize(aspectInstance);
            component = aspectInstance.getProxy();

            return component;
        }

        private AspectSystem getAspectSystem() {
            return aspectSystem;
        }
    }

    public static class AspectAdapter extends DecoratingComponentAdapter {
        private AspectSystem aspectSystem;

        public AspectAdapter(ComponentAdapter delegate, AspectSystem aspectSystem) {
            super(delegate);
            this.aspectSystem = aspectSystem;
        }

        public Object getComponentInstance(PicoContainer pico)
                throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
            Aspect aspect = (Aspect) super.getComponentInstance(pico);
            getAspectSystem().addAspect(aspect);
            return aspect;
        }

        private AspectSystem getAspectSystem() {
            return aspectSystem;
        }
    }
}
