package org.picocontainer.web.samples.jsf;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Characteristics;
import org.picocontainer.web.WebappComposer;

import javax.servlet.ServletContext;

public class JsfDemoWebappComposer implements WebappComposer {

        public void composeApplication(MutablePicoContainer applicationContainer, ServletContext context) {
        applicationContainer.addComponent(CheeseDao.class, InMemoryCheeseDao.class);
    }

    public void composeSession(MutablePicoContainer sessionContainer) {
        sessionContainer.addComponent(CheeseService.class, DefaultCheeseService.class);
    }

    public void composeRequest(MutablePicoContainer requestContainer) {
        requestContainer.as(Characteristics.NO_CACHE).addComponent(Brand.class, Brand.FromRequest.class);
        requestContainer.addComponent("cheeseBean", org.picocontainer.web.samples.jsf.ListCheeseController.class);
        requestContainer.addComponent("addCheeseBean", org.picocontainer.web.samples.jsf.AddCheeseController.class);
    }

}
