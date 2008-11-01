package org.picocontainer.web.sample.picocall;

import org.picocontainer.web.sample.model.Cheese;
import org.picocontainer.web.sample.service.CheeseService;

public class RemoveCheese {

    private Cheese cheese = new Cheese();
    private CheeseService cheeseService;

    public RemoveCheese(CheeseService cheeseService) {
        this.cheeseService = cheeseService;
        cheeseService.remove(cheese);
    }

    public Cheese getCheese() {
        return cheese;
    }

}
