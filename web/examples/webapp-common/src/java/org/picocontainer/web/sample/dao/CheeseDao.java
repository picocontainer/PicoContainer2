/**
 * **************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 * *
 * ***************************************************************************
 */
package org.picocontainer.web.sample.dao;

import java.util.Collection;

import org.picocontainer.web.sample.model.Cheese;

/**
 * @author Stephen Molitor
 */
public interface CheeseDao {

    void save(Cheese cheese);

    void remove(Cheese cheese);
    
    Cheese get(String name);

    Collection all();

}