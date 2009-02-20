package org.picocontainer.web.remoting;

import java.io.IOException;
import java.lang.reflect.Method;

public interface MethodAndParamVisitor {

    void method(String methodName, Method method) throws IOException;

    void superClass(String superClass) throws IOException;
}
