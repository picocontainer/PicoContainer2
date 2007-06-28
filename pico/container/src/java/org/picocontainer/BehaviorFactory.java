package org.picocontainer;


public interface BehaviorFactory extends ComponentFactory {
    ComponentFactory forThis(ComponentFactory delegate);
}
