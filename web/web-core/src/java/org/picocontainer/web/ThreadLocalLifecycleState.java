package org.picocontainer.web;

import org.picocontainer.lifecycle.LifecycleState;
import org.picocontainer.lifecycle.DefaultLifecycleState;

public class ThreadLocalLifecycleState implements LifecycleState {

    private LifecycleStateThreadLocal tl = new LifecycleStateThreadLocal();

    public void removingComponent() {
        tl.get().removingComponent();
    }

    public void starting() {
        tl.get().starting();
    }

    public void stopping() {
        tl.get().stopping();
    }

    public void stopped() {
        tl.get().stopped();
    }

    public boolean isStarted() {
        return tl.get().isStarted();
    }

    public void disposing() {
        tl.get().disposing();
    }

    public void disposed() {
        tl.get().disposed();
    }

    public void putLifecycleStateModelForThread(DefaultLifecycleState lifecycleState) {
        tl.set(lifecycleState);
    }

    public DefaultLifecycleState resetStateModelForThread() {
        DefaultLifecycleState dls = new DefaultLifecycleState();
        tl.set(dls);
        return dls;
    }

    public void invalidateStateModelForThread() {
        tl.set(null);
    }

    private static class LifecycleStateThreadLocal extends ThreadLocal<DefaultLifecycleState> {
        protected DefaultLifecycleState initialValue() {
            return new DefaultLifecycleState();
        }
    }

}
