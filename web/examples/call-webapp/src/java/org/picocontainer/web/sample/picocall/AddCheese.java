package org.picocontainer.web.sample.picocall;

import org.picocontainer.web.sample.model.Cheese;
import org.picocontainer.web.sample.model.Brand;
import org.picocontainer.web.sample.service.CheeseService;

public class AddCheese {

    private Cheese cheese = new Cheese();
    private CheeseService cheeseService;
    private final Brand brand;

    public AddCheese(CheeseService cheeseService, Brand brand) {
        this.cheeseService = cheeseService;
        this.brand = brand;
        cheeseService.save(cheese);
    }

    public Cheese getCheese() {
        return cheese;
    }

    public String getBrand() {
        return "Brand:" + brand;
    }

}
