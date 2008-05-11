/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.visitors;

import org.picocontainer.*;
import org.picocontainer.visitors.TraversalCheckingVisitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Visitor to verify {@link PicoContainer} instances. The visitor walks down the logical container hierarchy.
 * 
 * @author J&ouml;rg Schaible
 */
public class VerifyingVisitor extends TraversalCheckingVisitor {

    private final List nestedVerificationExceptions;
    private final Set<ComponentAdapter> verifiedComponentAdapters;
    private final Set<ComponentFactory> verifiedComponentFactories;
    private final PicoVisitor componentAdapterCollector;
    private PicoContainer currentPico;

    /**
     * Construct a VerifyingVisitor.
     */
    public VerifyingVisitor() {
        nestedVerificationExceptions = new ArrayList();
        verifiedComponentAdapters = new HashSet<ComponentAdapter>();
        verifiedComponentFactories = new HashSet<ComponentFactory>();
        componentAdapterCollector = new ComponentAdapterCollector();
    }

    /**
     * Traverse through all components of the {@link PicoContainer} hierarchy and verify the components.
     * 
     * @throws PicoVerificationException if some components could not be verified.
     * @see org.picocontainer.PicoVisitor#traverse(java.lang.Object)
     */
    public Object traverse(Object node) throws PicoVerificationException {
        nestedVerificationExceptions.clear();
        verifiedComponentAdapters.clear();
        try {
            super.traverse(node);
            if (!nestedVerificationExceptions.isEmpty()) {
                throw new PicoVerificationException(new ArrayList(nestedVerificationExceptions));
            }
        } finally {
            nestedVerificationExceptions.clear();
            verifiedComponentAdapters.clear();
        }
        return Void.TYPE;
    }

    public void visitContainer(PicoContainer pico) {
        super.visitContainer(pico);
        currentPico = pico;
    }

    public void visitComponentAdapter(ComponentAdapter componentAdapter) {
        super.visitComponentAdapter(componentAdapter);
        if (!verifiedComponentAdapters.contains(componentAdapter)) {
            try {
                componentAdapter.verify(currentPico);
            } catch (RuntimeException e) {
                nestedVerificationExceptions.add(e);
            }
            componentAdapter.accept(componentAdapterCollector);
        }
    }

    public void visitComponentFactory(ComponentFactory componentFactory) {
        super.visitComponentFactory(componentFactory);

        if (!verifiedComponentFactories.contains(componentFactory)) {
            try {
                componentFactory.verify(currentPico);
            } catch (RuntimeException e) {
                nestedVerificationExceptions.add(e);
            }
            componentFactory.accept(componentAdapterCollector);
        }
    }



    private class ComponentAdapterCollector implements PicoVisitor {
        // /CLOVER:OFF
        public Object traverse(Object node) {
            return null;
        }

        public void visitContainer(PicoContainer pico) {
        }

        // /CLOVER:ON

        public void visitComponentAdapter(ComponentAdapter componentAdapter) {
            verifiedComponentAdapters.add(componentAdapter);
        }

        public void visitComponentFactory(ComponentFactory componentFactory) {
            verifiedComponentFactories.add(componentFactory);
        }

        public void visitParameter(Parameter parameter) {
        }
    }
}
