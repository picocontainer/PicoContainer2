package org.picocontainer.web.samples.jsf;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.web.sample.ExampleWebappComposer;

public class JsfWebappComposer extends ExampleWebappComposer {

    public void composeRequest(MutablePicoContainer requestContainer) {
        requestContainer.addComponent("cheeseBean", org.picocontainer.web.samples.jsf.ListCheeseController.class);
        requestContainer.addComponent("addCheeseBean", org.picocontainer.web.samples.jsf.AddCheeseController.class);
    }

}
