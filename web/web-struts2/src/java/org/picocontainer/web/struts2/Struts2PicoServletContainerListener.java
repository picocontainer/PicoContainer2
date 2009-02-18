/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * --------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.struts2;

import org.picocontainer.web.PicoServletContainerListener;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.behaviors.Storing;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.Result;

public class Struts2PicoServletContainerListener extends PicoServletContainerListener {

    protected ScopedContainers makeScopedContainers() {

        ComponentMonitor cm = makeRequestComponentMonitor();

        NullLifecycleStrategy ls = new NullLifecycleStrategy();

        DefaultPicoContainer ac = new DefaultPicoContainer(new Caching(), makeParentContainer());
        Storing ss = new Storing();
        DefaultPicoContainer sc = new DefaultPicoContainer(ss, ac);
        Storing rs = new Storing();
        DefaultPicoContainer rc = new DefaultPicoContainer(rs, ls, sc, cm);
        return new ScopedContainers(ac,sc,rc,ss,rs);

    }

    /**
     * Struts2 handles whole value objects in some configurations.
     * This enables lazy instantiation of them    
     */
    @Override
    protected ComponentMonitor makeRequestComponentMonitor() {
        return new StrutsActionInstantiatingComponentMonitor();
    }

    public static class StrutsActionInstantiatingComponentMonitor extends NullComponentMonitor {
        public Object noComponentFound(MutablePicoContainer mutablePicoContainer, Object o) {
            return noComponent(mutablePicoContainer, o);
        }

        private Object noComponent(MutablePicoContainer mutablePicoContainer, Object o) {
            if (o instanceof Class) {
                Class clazz = (Class) o;
                if (Action.class.isAssignableFrom(clazz) || Result.class.isAssignableFrom(clazz)) {
                    try {
                        mutablePicoContainer.addComponent(clazz);
                    } catch (NoClassDefFoundError e) {
                        if (e.getMessage().equals("org/apache/velocity/context/Context")) {
                            // half expected. XWork seems to setup stuff that cannot
                            // work
                            // TODO if this is the case we should make configurable
                            // the list of classes we "expect" not to find.  Odd!
                        } else {
                            throw e;
                        }
                    }

                    return null;
                }
                try {
                    if (clazz.getConstructor(new Class[0]) != null) {
                        return clazz.newInstance();
                    }
                } catch (InstantiationException e) {
                    throw new PicoCompositionException("can't instantiate " + o);
                } catch (IllegalAccessException e) {
                    throw new PicoCompositionException("illegal access " + o);
                } catch (NoSuchMethodException e) {
                }
            }
            return super.noComponentFound(mutablePicoContainer, o);
        }
    }
}
