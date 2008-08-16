/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.webcontainer.groovy;

import org.picocontainer.MutablePicoContainer;

import java.util.Map;
import javax.servlet.Servlet;

import groovy.util.NodeBuilder;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;
import org.nanocontainer.webcontainer.PicoContext;
import org.nanocontainer.webcontainer.groovy.adapters.NodeBuilderAdapter;
import org.nanocontainer.webcontainer.groovy.adapters.WaffleAdapter;

public class ContextBuilder extends NodeBuilder {
    private final MutablePicoContainer parentContainer;
    private final PicoContext context;

    public ContextBuilder(MutablePicoContainer parentContainer, PicoContext context) {
        this.parentContainer = parentContainer;
        this.context = context;
    }

    protected Object createNode(Object name, Map map) {
        if (name.equals("filter")) {
            return makeFilter(map);
        } else if (name.equals("servlet")) {
            return makeServlet(map);
        } else if (name.equals("listener")) {
            return makeListener(map);
        } else if (name.equals("staticContent")) {
            setStaticContent(map);
            return null;
        } else if (name.equals("adapter")) {
            return makeAdapter(map);
        } else if (name.equals("waffleApp")) {
            return new WaffleAdapter(context, parentContainer, map).getNodeBuilder();
        }

        return null;
    }

    private void setStaticContent(Map map) {

        if (map.containsKey("welcomePage")) {
            context.setStaticContext((String)map.remove("path"), (String)map.remove("welcomePage"));
        } else {
            context.setStaticContext((String)map.remove("path"));
        }

    }

    private Object makeAdapter(Map map) {
        return new NodeBuilderAdapter((String)map.remove("nodeBuilder"),
                                      context,
                                      parentContainer,
                                      map).getNodeBuilder();
    }

    private Object makeListener(Map map) {
        return context.addListener((Class)map.remove("class"));
    }

    private Object makeServlet(Map map) {

        if (map.containsKey("class")) {
            ServletHolder servlet = context.addServletWithMapping((Class)map.remove("class"), (String)map
                .remove("path"));
            return new ServletHolderBuilder(servlet);
        } else {
            Servlet servlet = (Servlet)map.remove("instance");
            context.addServletWithMapping(servlet, (String)map.remove("path"));
            return servlet;
        }

    }

    private Object makeFilter(Map map) {
        FilterHolder filter = context.addFilterWithMapping((Class)map.remove("class"), (String)map.remove("path"),
                                                           extractDispatchers(map));
        return new FilterHolderBuilder(filter);
    }

    private int extractDispatchers(Map map) {
        Object dispatchers = map.remove("dispatchers");
        if (dispatchers != null) {
            return (Integer)dispatchers;
        }
        // default value
        return 0;
    }

}