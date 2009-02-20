package org.picocontainer.web.remoting;

import java.io.IOException;

public interface MethodAndParamVisitor {

    void startMethod(String method) throws IOException;

    void endMethod(String method) throws IOException;

    void visitParameter(String parameter) throws IOException;

    void superClass(String superClass) throws IOException;
}
