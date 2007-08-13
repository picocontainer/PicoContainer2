package org.picocontainer;

import java.util.Map;
import java.util.List;

/** @author Paul Hammant */
public interface ComponentStore {

    Map<Object, ComponentAdapter> getComponentKeyToAdapterCacheMap();

    List<ComponentAdapter<?>> getComponentAdapterList();

    List<ComponentAdapter<?>> getOrderedComponentAdapterList();
}
