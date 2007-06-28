/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Jon Tirsen                                               *
 *****************************************************************************/

package org.nanocontainer.nanning;

import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.Mixin;
import org.codehaus.nanning.config.AspectSystem;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.DecoratingComponentAdapter;
import org.picocontainer.defaults.NotConcreteRegistrationException;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public class NanningComponentAdapter extends DecoratingComponentAdapter {

    private final AspectSystem aspectSystem;

    public NanningComponentAdapter(AspectSystem aspectSystem, ComponentAdapter decoratedComponentAdapter) {
        super(decoratedComponentAdapter);
        this.aspectSystem = aspectSystem;
    }

    public Object getComponentInstance(PicoContainer pico) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        Object component = super.getComponentInstance(pico);
        // TODO Nanning will at the moment only aspectify stuff when it has one and only one interface
        if (component.getClass().getInterfaces().length == 1) {
            Class intf = component.getClass().getInterfaces()[0];
            // the trick: set up first mixin manually with the component as target
            AspectInstance aspectInstance = new AspectInstance(intf);
            Mixin mixin = new Mixin(intf, component);
            aspectInstance.addMixin(mixin);

            // let the aspects do its work
            aspectSystem.initialize(aspectInstance);
            component = aspectInstance.getProxy();
        }

        return component;
    }
}
