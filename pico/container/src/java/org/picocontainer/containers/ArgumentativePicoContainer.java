/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.containers;


import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoVisitor;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ParameterName;

import java.io.Serializable;
import java.io.StringReader;
import java.io.LineNumberReader;
import java.io.IOException;
import java.util.List;
import java.util.Collection;

public class ArgumentativePicoContainer implements PicoContainer, Serializable {

    public static final String DUMMY_PICOCONTAINER_CONFIG_ITEM = "DUMMY_PICOCONTAINER_CONFIG_ITEM";

    private final MutablePicoContainer delegate = new DefaultPicoContainer();

    public ArgumentativePicoContainer(String[] arguments) {
        this("=", arguments);
    }

    public ArgumentativePicoContainer(String separator, String[] arguments) {
        for (String argument : arguments) {
            processArgument(argument, separator);
        }
        int i = 1;
        delegate.addConfig(DUMMY_PICOCONTAINER_CONFIG_ITEM + ++i, DUMMY_PICOCONTAINER_CONFIG_ITEM + i);
        delegate.addConfig(DUMMY_PICOCONTAINER_CONFIG_ITEM + ++i, DUMMY_PICOCONTAINER_CONFIG_ITEM + i);
        delegate.addConfig(DUMMY_PICOCONTAINER_CONFIG_ITEM + ++i, 0);
        delegate.addConfig(DUMMY_PICOCONTAINER_CONFIG_ITEM + ++i, 0);
        delegate.addConfig(DUMMY_PICOCONTAINER_CONFIG_ITEM + ++i, false);
        delegate.addConfig(DUMMY_PICOCONTAINER_CONFIG_ITEM + ++i, false);
        delegate.addConfig(DUMMY_PICOCONTAINER_CONFIG_ITEM + ++i, 0L);
        delegate.addConfig(DUMMY_PICOCONTAINER_CONFIG_ITEM + ++i, 0L);
    }

    public ArgumentativePicoContainer(String separator, StringReader argumentProperties, String[] arguments)
        throws IOException {
        LineNumberReader lnr = new LineNumberReader(argumentProperties);
        String line = lnr.readLine();
        while (line != null) {
            processArgument(line, separator);
            line = lnr.readLine();
        }
        for (String argument : arguments) {
            processArgument(argument, separator);
        }
    }

    private void processArgument(String argument, String separator) {
        String[] kvs = argument.split(separator);
        if (kvs.length == 2) {
            addConfig(kvs[0], getValue(kvs[1]));
        } else if (kvs.length == 1) {
            addConfig(kvs[0], true);
        } else if (kvs.length > 2) {
            throw new PicoCompositionException(
                "Argument name=value pair '" + argument + "' has too many '=' characters");
        }
    }

    private void addConfig(String key, Object val) {
        if (delegate.getComponent(key) != null) {
            delegate.removeComponent(key);
        }
        delegate.addConfig(key, val);
    }

    public ArgumentativePicoContainer(String separator, StringReader argumentsProps) throws IOException {
        this(separator, argumentsProps, new String[0]);
    }

    private Object getValue(String s) {
        if (s.equals("true")) {
            return true;
        } else if (s.equals("false")) {
            return false;
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
        }

        return s;
    }

    public Object getComponent(Object componentKeyOrType) {
        return delegate.getComponent(componentKeyOrType);
    }

    public <T> T getComponent(Class<T> componentType) {
        return null;
    }

    public List getComponents() {
        return delegate.getComponents();
    }

    public PicoContainer getParent() {
        return new EmptyPicoContainer();
    }

    public ComponentAdapter<?> getComponentAdapter(Object componentKey) {
        return delegate.getComponentAdapter(componentKey);
    }

    public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType, ParameterName componentParameterName) {
        return delegate.getComponentAdapter(componentType, componentParameterName);
    }

    public Collection<ComponentAdapter<?>> getComponentAdapters() {
        return delegate.getComponentAdapters();
    }

    public <T> List<ComponentAdapter<T>> getComponentAdapters(Class<T> componentType) {
        return null;
    }

    public <T> List<T> getComponents(Class<T> componentType) {
        return delegate.getComponents(componentType);
    }

    public void accept(PicoVisitor visitor) {
        delegate.accept(visitor);

    }
}
