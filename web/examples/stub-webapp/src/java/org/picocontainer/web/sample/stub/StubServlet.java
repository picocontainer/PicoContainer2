package org.picocontainer.web.sample.stub;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.web.PicoServletContainerFilter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import java.io.IOException;

public class StubServlet extends HttpServlet {

    private static ThreadLocal<MutablePicoContainer> currentRequestContainer = new ThreadLocal<MutablePicoContainer>();

    @SuppressWarnings("serial")
    public static class ServletFilter extends PicoServletContainerFilter {
        protected void setAppContainer(MutablePicoContainer container) {
        }
        protected void setRequestContainer(MutablePicoContainer container) {
            currentRequestContainer.set(container);
        }
        protected void setSessionContainer(MutablePicoContainer container) {
        }
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        MutablePicoContainer container = currentRequestContainer.get();
        RequestScopeComp requestScopeComp = container.getComponent(RequestScopeComp.class);

        ServletOutputStream os = response.getOutputStream();
        os.print("<html><body><p>");
        os.println(requestScopeComp.getCounter());
        os.print("</p></body></html>");
    }
}
