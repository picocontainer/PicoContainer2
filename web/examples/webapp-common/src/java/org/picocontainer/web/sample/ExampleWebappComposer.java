package org.picocontainer.web.sample;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.web.WebappComposer;
import org.picocontainer.web.sample.dao.CheeseDao;
import org.picocontainer.web.sample.dao.simple.InMemoryCheeseDao;
import org.picocontainer.web.sample.service.defaults.DefaultCheeseService;
import org.picocontainer.web.sample.service.CheeseService;

public class ExampleWebappComposer implements WebappComposer {

    public void composeApplication(MutablePicoContainer applicationContainer) {
        applicationContainer.addComponent(CheeseDao.class, InMemoryCheeseDao.class);
    }

    public void composeSession(MutablePicoContainer sessionContainer) {
        sessionContainer.addComponent(CheeseService.class, DefaultCheeseService.class);
    }

    public void composeRequest(MutablePicoContainer requestContainer) {
    }

}
