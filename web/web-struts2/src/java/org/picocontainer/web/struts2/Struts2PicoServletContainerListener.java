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
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.behaviors.Storing;

public class Struts2PicoServletContainerListener extends PicoServletContainerListener {

    protected ScopedContainers makeScopedContainers() {

        NullComponentMonitor cm = makeComponentMonitor();

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
    protected NullComponentMonitor makeComponentMonitor() {
        return new NullComponentMonitor() {
            public Object noComponentFound(MutablePicoContainer mutablePicoContainer, Object o) {
                return noComponent(mutablePicoContainer, o);
            }

            private Object noComponent(MutablePicoContainer mutablePicoContainer, Object o) {
                if (o instanceof Class) {
                    try {
                        return ((Class) o).newInstance();
                    } catch (InstantiationException e) {
                        throw new PicoCompositionException("can't instantiate " + o);
                    } catch (IllegalAccessException e) {
                        throw new PicoCompositionException("illegal access " + o);
                    }
                }
                return super.noComponentFound(mutablePicoContainer, o);
            }
        };
    }
}
