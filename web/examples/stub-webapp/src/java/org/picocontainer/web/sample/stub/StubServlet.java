package org.picocontainer.web.sample.stub;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.web.PicoServletContainerFilter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class StubServlet extends HttpServlet {

    private static ThreadLocal<MutablePicoContainer> currentRequestContainer = new ThreadLocal<MutablePicoContainer>();

    @SuppressWarnings("serial")
    public static class ServletFilter extends PicoServletContainerFilter {
        protected void setAppContainer(MutablePicoContainer container) {
        }
        protected void setRequestContainer(MutablePicoContainer container) {
            if (currentRequestContainer == null) {
                currentRequestContainer = new ThreadLocal<MutablePicoContainer>();
            }
            currentRequestContainer.set(container);
        }
        protected void setSessionContainer(MutablePicoContainer container) {
        }
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.info("s0");
        ServletOutputStream os = response.getOutputStream();
        os.print("<html><body><p>");
        try {
            response.setContentType("text/html");
            MutablePicoContainer container = currentRequestContainer.get();
            RequestScopeComp requestScopeComp = container.getComponent(RequestScopeComp.class);

            os.println(requestScopeComp.getCounter());
        } catch (Throwable e) {
            logger.info("s1:"+e.getMessage());
            logger.info("s2:"+e.getClass().getName());
        }
        os.print("</p></body></html>");
    }
}
