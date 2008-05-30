package org.picocontainer.web.webwork2;

import org.picocontainer.web.PicoServletContainerListener;

import javax.servlet.ServletContextEvent;

import com.opensymphony.xwork.ObjectFactory;

@SuppressWarnings("serial")
public class WebWork2PicoServletContainerListener extends PicoServletContainerListener {

    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        ObjectFactory.setObjectFactory(new PicoObjectFactory());
    }
}
