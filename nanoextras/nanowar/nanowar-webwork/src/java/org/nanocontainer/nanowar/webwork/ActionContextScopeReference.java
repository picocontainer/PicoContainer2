package org.nanocontainer.nanowar.webwork;

import org.picocontainer.ObjectReference;
import webwork.action.ActionContext;

/**
 * References an object that lives as an attribute of the
 * webwork action context
 *
 * @author Konstantin Pribluda
 */
public final class ActionContextScopeReference implements ObjectReference {

    private final String key;

    public ActionContextScopeReference(String key) {
        this.key = key;
    }

    public void set(Object item) {
        ActionContext.getContext().put(key, item);
    }

    public Object get() {
        return ActionContext.getContext().get(key);
    }

}
