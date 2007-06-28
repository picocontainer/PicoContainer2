/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.sample.webwork2;

import org.nanocontainer.nanowar.sample.service.CheeseService;

import com.opensymphony.xwork.Action;

import java.util.List;
import java.util.ArrayList;

/**
 * Example of a XWork action that relies on constructor injection.
 * 
 * @author Paul Hammant
 * @version $Revision: 2099 $
 */
public class CheeseInventory implements Action {

    private final CheeseService cheeseService;
    private List cheeses;

    public CheeseInventory(CheeseService cheeseService) {
        this.cheeseService = cheeseService;
    }

    public List getCheeses() {
        return cheeses;
    }

    public String execute() throws Exception {
        cheeses = new ArrayList(cheeseService.getCheeses());
        return SUCCESS;
    }

}


