package org.picocontainer.web.sample.picocall;

import org.picocontainer.web.sample.ExampleWebappComposer;
import org.picocontainer.web.sample.model.Brand;
import org.picocontainer.MutablePicoContainer;

public class PicoCallWebappComposer extends ExampleWebappComposer {

    public void composeRequest(MutablePicoContainer mutablePicoContainer) {
        mutablePicoContainer.addComponent(AddCheese.class);
        mutablePicoContainer.addComponent(CheeseInventory.class);
        mutablePicoContainer.addComponent(RemoveCheese.class);
        super.composeRequest(mutablePicoContainer);
    }
}
