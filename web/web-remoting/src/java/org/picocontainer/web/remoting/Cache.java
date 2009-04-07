package org.picocontainer.web.remoting;

public interface Cache {
    Object get(Object key);
    void put(Object key, Object toCache);
}
