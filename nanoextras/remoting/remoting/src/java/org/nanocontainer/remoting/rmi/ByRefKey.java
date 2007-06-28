package org.nanocontainer.remoting.rmi;

import java.io.Serializable;

/**
 * @author Neil Clayton
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ByRefKey implements Serializable {
    private final Serializable value;

    public ByRefKey(Serializable value) {
        this.value = value;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ByRefKey)) return false;

        final ByRefKey byRefKey = (ByRefKey) o;
        return !(value != null ? !value.equals(byRefKey.value) : byRefKey.value != null);

    }

    public int hashCode() {
        return (value != null ? value.hashCode() : 0);
    }

    public Serializable getValue() {
        return value;
    }
}