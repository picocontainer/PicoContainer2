package org.picocontainer.web;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Storing;

public class RequestContainerHolder {

    private final MutablePicoContainer container;
    private final Storing storing;
    private final ThreadLocalLifecycleState lifecycleState;

    public RequestContainerHolder(MutablePicoContainer container, Storing storing, ThreadLocalLifecycleState lifecycleState) {
        this.container = container;
        this.storing = storing;
        this.lifecycleState = lifecycleState;
    }

    MutablePicoContainer getContainer() {
        return container;
    }

    Storing getStoring() {
        return storing;
    }

    ThreadLocalLifecycleState getLifecycleStateModel() {
        return lifecycleState;
    }
}