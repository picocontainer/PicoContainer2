package org.picocontainer.web;

import org.picocontainer.MutablePicoContainer;

public class ApplicationContainerHolder {

    private final MutablePicoContainer container;

    public ApplicationContainerHolder(MutablePicoContainer container) {
        this.container = container;
    }

    MutablePicoContainer getContainer() {
        return container;
    }
}
