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
import dynaop.Pointcuts;
import dynaop.ProxyFactory;
import org.nanocontainer.aop.AspectablePicoContainer;
import org.nanocontainer.aop.AspectablePicoContainerFactory;
import org.nanocontainer.aop.AspectsContainer;
import org.nanocontainer.aop.AspectsManager;
import org.nanocontainer.aop.defaults.AspectsComponentAdapterFactory;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentFactory;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.injectors.ConstructorInjectionFactory;
import org.picocontainer.behaviors.CachingBehaviorFactory;

/**
 * Uses dynaop to create <code>AspectablePicoContainer</code> objects.
 *
 * @author Stephen Molitor
 * @author Mauro Talevi
 * @version $Revision$
 */
public class DynaopAspectablePicoContainerFactory implements AspectablePicoContainerFactory {

    public AspectablePicoContainer createContainer(Class containerClass, AspectsManager aspectsManager,
                                                   ComponentFactory componentFactory, PicoContainer parent) {

        ComponentFactory aspectsComponentAdapterFactory = new AspectsComponentAdapterFactory(aspectsManager).forThis(componentFactory);
        MutablePicoContainer pico = createMutablePicoContainer(containerClass, aspectsComponentAdapterFactory, parent);
        return mixinAspectablePicoContainer(aspectsManager, pico);
    }

    public AspectablePicoContainer createContainer(Class containerClass,
                                                   ComponentFactory componentFactory, PicoContainer parent) {
        return createContainer(containerClass, new DynaopAspectsManager(), componentFactory, parent);
    }

    public AspectablePicoContainer createContainer(ComponentFactory componentFactory, PicoContainer parent) {
        return createContainer(DefaultPicoContainer.class, componentFactory, parent);
    }

    public AspectablePicoContainer createContainer(ComponentFactory componentFactory) {
        return createContainer(componentFactory, null);
    }

    public AspectablePicoContainer createContainer(PicoContainer parent) {
        return createContainer(new CachingBehaviorFactory().forThis(new ConstructorInjectionFactory()), parent);
    }

    public AspectablePicoContainer createContainer() {
        return createContainer(new CachingBehaviorFactory().forThis(new ConstructorInjectionFactory()));
    }

    public AspectablePicoContainer makeChildContainer(AspectsManager aspectsManager, AspectablePicoContainer parent) {
        return mixinAspectablePicoContainer(aspectsManager, parent.makeChildContainer());
    }

    public AspectablePicoContainer makeChildContainer(AspectablePicoContainer parent) {
        return makeChildContainer(new DynaopAspectsManager(), parent);
    }
    
    private MutablePicoContainer createMutablePicoContainer(Class containerClass, ComponentFactory componentFactory,
                                                      PicoContainer parent) {
        MutablePicoContainer temp = new DefaultPicoContainer();
        temp.addComponent(containerClass);
        temp.addComponent(ComponentFactory.class, componentFactory);
        if (parent != null) {
            temp.addComponent(PicoContainer.class, parent);
        }
        return (MutablePicoContainer) temp.getComponent(containerClass);
    }

    private AspectablePicoContainer mixinAspectablePicoContainer(AspectsManager aspectsManager,
            MutablePicoContainer pico) {
        Aspects aspects = new Aspects();
        aspects.mixin(Pointcuts.ALL_CLASSES, new Class[]{AspectsContainer.class}, new InstanceMixinFactory(aspectsManager));
        aspects.interfaces(Pointcuts.ALL_CLASSES, new Class[]{AspectablePicoContainer.class});
        return (AspectablePicoContainer) ProxyFactory.getInstance(aspects).wrap(pico);
    }

}