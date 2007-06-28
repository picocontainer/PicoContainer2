/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan                                           *
 *****************************************************************************/

package org.nanocontainer.aop.defaults;

import dynaop.Aspects;
import dynaop.Pointcuts;
import dynaop.ProxyFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.nanocontainer.aop.AspectablePicoContainer;
import org.nanocontainer.aop.AspectsApplicator;
import org.nanocontainer.aop.AspectsContainer;
import org.nanocontainer.aop.AspectsManager;
import org.nanocontainer.aop.ClassPointcut;
import org.nanocontainer.aop.ComponentPointcut;
import org.nanocontainer.aop.MethodPointcut;
import org.nanocontainer.aop.dynaop.InstanceMixinFactory;
import org.nanocontainer.script.NodeBuilderDecorationDelegate;
import org.nanocontainer.script.NanoContainerMarkupException;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ComponentFactory;

import java.util.List;
import java.util.Map;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @version $Revision$
 */
public class AopNodeBuilderDecorationDelegate implements NodeBuilderDecorationDelegate {

    private final AspectsManager aspectsManager;
    private Object currentKey;
    private AspectablePicoContainer currentPico;
    private ClassPointcut currentClassCut;
    private MethodPointcut currentMethodCut;

    public AopNodeBuilderDecorationDelegate(AspectsManager aspectsManager) {
        this.aspectsManager = aspectsManager;
    }

    public ComponentFactory decorate(ComponentFactory componentFactory, Map attributes) {
        return createAdapterFactory(aspectsManager, componentFactory);
    }

    public MutablePicoContainer decorate(MutablePicoContainer picoContainer) {
        currentPico = mixinAspectablePicoContainer(aspectsManager, picoContainer);
        return currentPico;
    }

    public Object createNode(Object name, Map attributes, Object parentElement) {
        if (name.equals("aspect")) {
            return createAspectNode(attributes, name);
        } else if (name.equals("pointcut")) {
            return createPointcutNode(attributes, name);
        } else {
            throw new NanoContainerMarkupException("Don't know how to create a '" + name + "' child of a '" + parentElement.toString() + "' element");
        }
    }

    private Object createPointcutNode(Map attributes, Object name) {
        currentClassCut = (ClassPointcut) attributes.remove("classCut");
        currentMethodCut = (MethodPointcut) attributes.remove("methodCut");
        return name;
    }

    private Object createAspectNode(Map attributes, Object name) {
        ClassPointcut classCut = (ClassPointcut) attributes.remove("classCut");
        if(classCut != null) {
            currentClassCut = classCut;
        }
        MethodPointcut methodCut = (MethodPointcut) attributes.remove("methodCut");
        if(methodCut != null) {
            currentMethodCut = methodCut;
        }

        MethodInterceptor interceptor = (MethodInterceptor) attributes.remove("interceptor");
        Object interceptorKey = attributes.remove("interceptorKey");
        Class mixinClass = (Class) attributes.remove("mixinClass");
        List mixinInterfaces = (List) attributes.remove("mixinInterfaces");

        ComponentPointcut componentCut = (ComponentPointcut) attributes.remove("componentCut");
        if (componentCut == null && currentKey != null) {
            componentCut = currentPico.getPointcutsFactory().component(currentKey);
        }

        if (interceptor != null || interceptorKey != null) {
            registerInterceptor(currentPico, currentClassCut, componentCut, currentMethodCut, interceptor, interceptorKey);
        } else if (mixinClass != null) {
            registerMixin(currentPico, currentClassCut, componentCut, toClassArray(mixinInterfaces), mixinClass);
        } else {
            throw new NanoContainerMarkupException("No advice specified - must specify one of interceptor, interceptorKey, mixinClass, or mixinKey");
        }

        return name;
    }


    private AspectsComponentAdapterFactory createAdapterFactory(AspectsApplicator aspectsApplicator,
                                                                ComponentFactory delegateAdapterFactory) {
        if (delegateAdapterFactory != null) {
            return (AspectsComponentAdapterFactory) new AspectsComponentAdapterFactory(aspectsApplicator).forThis(delegateAdapterFactory);
        } else {
            return new AspectsComponentAdapterFactory(aspectsApplicator);
        }
    }

    private AspectablePicoContainer mixinAspectablePicoContainer(AspectsManager aspectsManager,
                                                                 MutablePicoContainer pico) {
        Aspects aspects = new Aspects();
        aspects.mixin(Pointcuts.ALL_CLASSES, new Class[]{AspectsContainer.class}, new InstanceMixinFactory(aspectsManager));
        aspects.interfaces(Pointcuts.ALL_CLASSES, new Class[]{AspectablePicoContainer.class});
        return (AspectablePicoContainer) ProxyFactory.getInstance(aspects).wrap(pico);
    }

    private void registerInterceptor(AspectablePicoContainer pico, ClassPointcut classCut,
                                     ComponentPointcut componentCut, MethodPointcut methodCut, MethodInterceptor interceptor,
                                     Object interceptorKey) {
        // precondition:
        if (interceptor == null && interceptorKey == null) {
            throw new RuntimeException("assertion failed -- non-null interceptor or interceptorKey expected");
        }

        // validate script:
        if (classCut == null && componentCut == null) {
            throw new NanoContainerMarkupException("currentClassCut or componentCut required for interceptor advice");
        }
        if (methodCut == null) {
            throw new NanoContainerMarkupException("currentMethodCut required for interceptor advice");
        }

        if (classCut != null) {
            if (interceptor != null) {
                pico.registerInterceptor(classCut, methodCut, interceptor);
            } else {
                pico.registerInterceptor(classCut, methodCut, interceptorKey);
            }
        } else {
            if (interceptor != null) {
                pico.registerInterceptor(componentCut, methodCut, interceptor);
            } else {
                pico.registerInterceptor(componentCut, methodCut, interceptorKey);
            }
        }
    }

    private void registerMixin(AspectablePicoContainer pico, ClassPointcut classCut, ComponentPointcut componentCut,
                               Class[] mixinInterfaces, Class mixinClass) {
        // precondition:
        if (mixinClass == null) {
            throw new RuntimeException("assertion failed -- mixinClass required");
        }

        // validate script:
        if (classCut == null && componentCut == null) {
            throw new NanoContainerMarkupException("currentClassCut or componentCut required for mixin advice");
        }

        if (classCut != null) {
            if (mixinInterfaces != null) {
                pico.registerMixin(classCut, mixinInterfaces, mixinClass);
            } else {
                pico.registerMixin(classCut, mixinClass);
            }
        } else {
            if (mixinInterfaces != null) {
                pico.registerMixin(componentCut, mixinInterfaces, mixinClass);
            } else {
                pico.registerMixin(componentCut, mixinClass);
            }
        }
    }

    private Class[] toClassArray(List l) {
        if (l == null) {
            return null;
        }
        return (Class[]) l.toArray(new Class[l.size()]);
    }

    public void rememberComponentKey(Map attributes) {
        Object key = attributes.get("key");
        Object clazz = attributes.get("class");
        if (key != null) {
            currentKey = key;
        } else {
            currentKey = clazz;
        }
    }
}
