package org.picocontainer.web.sample.struts;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.web.WebappComposer;

import javax.servlet.ServletContext;

public class Struts1DemoComposer implements WebappComposer {

    public void composeApplication(MutablePicoContainer pico, ServletContext context) {
        pico.addComponent(CheeseDao.class, InMemoryCheeseDao.class);
    }

    public void composeSession(MutablePicoContainer pico) {
        pico.addComponent(DefaultCheeseService.class);
    }

    public void composeRequest(MutablePicoContainer pico) {
    }
}
