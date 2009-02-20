package org.picocontainer.web.remoting;

import java.io.IOException;
import java.lang.reflect.Method;

public interface MethodAndParamVisitor {

    void startMethod(String method) throws IOException;

    void endMethod(String method) throws IOException;

    void methodParameters(Method method) throws IOException;

    void superClass(String superClass) throws IOException;
}
