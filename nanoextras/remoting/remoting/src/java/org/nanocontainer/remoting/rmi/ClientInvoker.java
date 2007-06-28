package org.nanocontainer.remoting.rmi;

import java.lang.reflect.Method;

import com.thoughtworks.proxy.Invoker;

/**
 * @author Neil Clayton
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ClientInvoker implements Invoker {
    private final ByRefKey key;
    private final RemotingInterceptor interceptor;
    private static Method getKey;

    {
        try {
            getKey = KeyHolder.class.getMethod("getKey", (Class[])null);
        } catch (NoSuchMethodException e) {
            throw new InternalError();
        }
    }

    public ClientInvoker(ByRefKey key, RemoteInterceptorImpl interceptor) {
        this.key = key;
        this.interceptor = interceptor;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (getKey.equals(method)) {
            return key;
        }
        Invocation invocation = new Invocation(method.getName(), method.getParameterTypes(), args);
        return interceptor.invoke(invocation);
    }
}