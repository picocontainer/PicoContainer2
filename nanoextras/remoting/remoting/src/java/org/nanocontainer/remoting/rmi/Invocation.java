package org.nanocontainer.remoting.rmi;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Neil Clayton
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public final class Invocation implements Serializable {
    private final String methodName;
    private final Class[] parameterTypes;
    private Object[] args;

    public Invocation(String methodName, Class[] parameterTypes, Object[] args) {
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.args = args;
    }

    public Object invoke(Object target) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = target.getClass().getMethod(methodName, parameterTypes);
        return method.invoke(target, args);
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }
}