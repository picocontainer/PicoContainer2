package org.picocontainer;

import java.lang.reflect.Type;

public interface Injector<T> extends ComponentAdapter<T> {

    T decorateComponentInstance(PicoContainer container, Type into, T instance);

}
