package org.picocontainer.web.webwork;

import javax.servlet.ServletContextEvent;

import org.picocontainer.web.PicoServletContainerListener;

import webwork.action.factory.ActionFactory;

public class WebWorkPicoServletContainerListener extends PicoServletContainerListener {

    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        ActionFactory.setActionFactory(new WebWorkActionFactory());
    }
}
