/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.picocontainer.gems.monitors;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.monitors.DelegatingComponentMonitor;

import java.lang.reflect.Constructor;
import java.util.*;

public final class DotDependencyGraphComponentMonitor extends DelegatingComponentMonitor implements ComponentMonitor {

    final ArrayList allInstantiated = new ArrayList();

    public DotDependencyGraphComponentMonitor(ComponentMonitor delegate) {
        super(delegate);
    }

    public DotDependencyGraphComponentMonitor() {
    }

    public void instantiated(PicoContainer container, ComponentAdapter componentAdapter,
                             Constructor constructor,
                             Object instantiated,
                             Object[] injected,
                             long duration) {

        this.allInstantiated.add(new Instantiation(constructor, instantiated, injected, duration));

        super.instantiated(container, componentAdapter, constructor, instantiated, injected, duration);
    }


    public String getClassDependencyGraph() {

        HashSet lines = new HashSet();

        for (Object anAllInstantiated : allInstantiated) {
            Instantiation instantiation = (Instantiation)anAllInstantiated;
            for (int j = 0; j < instantiation.getInjected().length; j++) {
                Object instantiated = instantiation.getInstantiated();
                Object injected = instantiation.getInjected()[j];
                lines.add(
                    "  '" + instantiated.getClass().getName() + "' -> '" + injected.getClass().getName() + "';\n");
            }
        }

        return sortLines(lines);
    }

    private String sortLines(HashSet lines) {
        ArrayList list = new ArrayList(lines);
        Collections.sort(list);

        String dependencies = "";
        for (Object aList : list) {
            String dep = (String)aList;
            dependencies = dependencies + dep;
        }
        return dependencies.replaceAll("'","\"");
    }

    public String getInterfaceDependencyGraph() {
        HashSet lines = new HashSet();

        for (Object anAllInstantiated : allInstantiated) {
            Instantiation instantiation = (Instantiation)anAllInstantiated;
            for (int j = 0; j < instantiation.getInjected().length; j++) {
                Object injected = instantiation.getInjected()[j];
                Class injectedType = instantiation.getConstructor().getParameterTypes()[j];
                Object instantiated = instantiation.getInstantiated();
                if (injected.getClass() != injectedType) {
                    lines.add("  '" + instantiated.getClass().getName() + "' -> '" + injectedType.getName() +
                              "' [style=dotted,label='needs'];\n");
                    lines.add("  '" + injected.getClass().getName() + "' -> '" + injectedType.getName() +
                              "' [style=dotted, color=red,label='isA'];\n");
                    lines.add("  '" + injectedType.getName() + "' [shape=box, label=" + printClassName(injectedType) +
                              "];\n");
                } else {
                    lines.add("  '" + instantiated.getClass().getName() + "' -> '" + injected.getClass().getName() +
                              "' [label='needs'];\n");
                }
                lines.add("  '" + instantiated.getClass().getName() + "' [label=" +
                          printClassName(instantiated.getClass()) + "];\n");

            }
        }

        return sortLines(lines);
    }

    private String printClassName(Class clazz) {
        String className = clazz.getName();
        return "'" + className.substring(className.lastIndexOf(".")+1) + "\\n" + clazz.getPackage().getName() + "'";

    }

    private static final class Instantiation {
        final Constructor constructor;
        final Object instantiated;
        final Object[] injected;
        final long duration;
        public Instantiation(Constructor constructor, Object instantiated, Object[] injected, long duration) {
            this.constructor = constructor;
            this.instantiated = instantiated;
            this.injected = injected;
            this.duration = duration;
        }

        public Constructor getConstructor() {
            return constructor;
        }

        public Object getInstantiated() {
            return instantiated;
        }
        public Object[] getInjected() {
            return injected;
        }
    }
}
