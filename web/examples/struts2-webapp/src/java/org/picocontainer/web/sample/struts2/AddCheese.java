package org.picocontainer.web.sample.struts2;

import com.opensymphony.xwork2.ActionSupport;

import org.picocontainer.web.sample.model.Cheese;
import org.picocontainer.web.sample.service.CheeseService;

public class AddCheese extends ActionSupport {

    private Cheese cheese = new Cheese();
    private CheeseService cheeseService;

    public AddCheese(CheeseService cheeseService) {
        this.cheeseService = cheeseService;
    }

    public Cheese getCheese() {
        return cheese;
    }

    public String execute() throws Exception {
        System.out.println("Adding cheese "+cheese);
        cheeseService.save(cheese);
        return SUCCESS;
    }

}
