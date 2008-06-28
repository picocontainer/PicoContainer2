package org.picocontainer.web.sample.struts;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.web.WebappComposer;
import org.picocontainer.web.sample.dao.CheeseDao;
import org.picocontainer.web.sample.dao.simple.InMemoryCheeseDao;
import org.picocontainer.web.sample.service.defaults.DefaultCheeseService;

public class StrutsWebappComposer implements WebappComposer {

    public void composeApplication(MutablePicoContainer applicationContainer) {
        applicationContainer.addComponent(CheeseDao.class, InMemoryCheeseDao.class);
    }

    public void composeSession(MutablePicoContainer sessionContainer) {
        sessionContainer.addComponent(DefaultCheeseService.class);
    }

    public void composeRequest(MutablePicoContainer requestContainer) {
    }
}
