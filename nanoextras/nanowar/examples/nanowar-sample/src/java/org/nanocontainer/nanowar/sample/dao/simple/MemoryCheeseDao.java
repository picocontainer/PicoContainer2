/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.sample.dao.simple;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.nanocontainer.nanowar.sample.dao.CheeseDao;
import org.nanocontainer.nanowar.sample.model.Cheese;

/**
 * @author Stephen Molitor
 */
public class MemoryCheeseDao implements CheeseDao, Serializable {

    private final Map cheeses;

    public MemoryCheeseDao() {
        cheeses = new HashMap();
        cheeses.put("cheddar", new Cheese("cheddar","England"));
    }

    public void save(Cheese cheese) {
        cheeses.put(cheese.getName(), cheese);
    }

    public void remove(Cheese cheese) {
        cheeses.remove(cheese.getName());
    }
    public Cheese get(String name) {
        return (Cheese) cheeses.get(name);
    }

    public Collection all() {
        return cheeses.values();
    }

}