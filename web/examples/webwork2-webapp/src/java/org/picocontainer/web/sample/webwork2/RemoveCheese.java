package org.picocontainer.web.sample.webwork2;

import com.opensymphony.xwork.ActionSupport;

import org.picocontainer.web.sample.model.Cheese;
import org.picocontainer.web.sample.service.CheeseService;

public class RemoveCheese extends ActionSupport {

    private Cheese cheese = new Cheese();
    private CheeseService cheeseService;

    public RemoveCheese(CheeseService cheeseService) {
        this.cheeseService = cheeseService;
    }

    public Cheese getCheese() {
        return cheese;
    }


    public String execute() throws Exception {
        cheeseService.remove(cheese);
        return SUCCESS;
    }

}
