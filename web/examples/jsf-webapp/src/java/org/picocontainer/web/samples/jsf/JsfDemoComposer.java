package org.picocontainer.web.samples.jsf;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Characteristics;
import org.picocontainer.web.WebappComposer;

import javax.servlet.ServletContext;

public class JsfDemoComposer implements WebappComposer {

    public void composeApplication(MutablePicoContainer pico, ServletContext context) {
        pico.addComponent(CheeseDao.class, InMemoryCheeseDao.class);
    }

    public void composeSession(MutablePicoContainer pico) {
        pico.addComponent(CheeseService.class, DefaultCheeseService.class);
    }

    public void composeRequest(MutablePicoContainer pico) {
        pico.as(Characteristics.NO_CACHE).addComponent(Brand.class, Brand.FromRequest.class);
        pico.addComponent("cheeseBean", org.picocontainer.web.samples.jsf.ListCheeseController.class);
        pico.addComponent("addCheeseBean", org.picocontainer.web.samples.jsf.AddCheeseController.class);
    }

}
