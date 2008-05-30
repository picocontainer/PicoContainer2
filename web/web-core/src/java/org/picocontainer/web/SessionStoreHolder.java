package org.picocontainer.web;

import org.picocontainer.behaviors.Storing;
import org.picocontainer.lifecycle.DefaultLifecycleState;

public class SessionStoreHolder {
    private final Storing.StoreWrapper storeWrapper;
    private final DefaultLifecycleState defaultLifecycleState;

    public SessionStoreHolder(Storing.StoreWrapper storeWrapper,
                              DefaultLifecycleState defaultLifecycleState) {

        this.storeWrapper = storeWrapper;
        this.defaultLifecycleState = defaultLifecycleState;
    }

    Storing.StoreWrapper getStoreWrapper() {
        return storeWrapper;
    }

    DefaultLifecycleState getDefaultLifecycleState() {
        return defaultLifecycleState;
    }
}
