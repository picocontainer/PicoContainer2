package org.picocontainer.injectors;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

public abstract class AbstractFieldInjector<T> extends IterativeInjector<T> {

    public AbstractFieldInjector(Object componentKey, Class componentImplementation,
                                 Parameter[] parameters, ComponentMonitor monitor,
                                 boolean useNames) throws NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters, monitor, useNames);
    }

    @Override
    final protected void unsatisfiedDependencies(PicoContainer container, Set<Type> unsatisfiableDependencyTypes, List<AccessibleObject> unsatisfiableDependencyMembers) {
        StringBuilder sb = new StringBuilder(this.getComponentImplementation().getName()).append(" has unsatisfied dependency for fields [");
        for (int i = 0; i < unsatisfiableDependencyMembers.size(); i++) {
            AccessibleObject accessibleObject = unsatisfiableDependencyMembers.get(i);
            Field m = (Field) accessibleObject;
            sb.append(m.getType().getName()).append(".").append(m.getName());
        }
        String container1 = container.toString();
        throw new UnsatisfiableDependenciesException(sb.toString() + "] from " + container1);
    }

}
