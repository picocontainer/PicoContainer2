/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.web.sample.picocall;

import org.picocontainer.web.sample.service.CheeseService;
import org.picocontainer.web.sample.model.Brand;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Paul Hammant
 */
public class CheeseInventory {

    private final CheeseService cheeseService;
    private List cheeses;
    private Brand brand;

    public CheeseInventory(CheeseService cheeseService, Brand brand) {
        this.cheeseService = cheeseService;
        this.brand = brand;
        cheeses = new ArrayList(cheeseService.getCheeses());
    }

    public List getCheeses() {
        return cheeses;
    }

    public String getBrand() {
        return "Brand:" + brand.getName();
    }
}


