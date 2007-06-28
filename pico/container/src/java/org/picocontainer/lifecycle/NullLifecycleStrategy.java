package org.picocontainer.lifecycle;

import org.picocontainer.LifecycleStrategy;

import java.io.Serializable;

public class NullLifecycleStrategy implements LifecycleStrategy, Serializable {

    private static LifecycleStrategy instance;
    public static synchronized LifecycleStrategy getInstance() {
        if (instance == null) {
            instance = new NullLifecycleStrategy();
        }
        return instance;
    }

    public void start(Object component) {
    }

    public void stop(Object component) {
    }

    public void dispose(Object component) {
    }

    public boolean hasLifecycle(Class type) {
        return false;
    }
}
