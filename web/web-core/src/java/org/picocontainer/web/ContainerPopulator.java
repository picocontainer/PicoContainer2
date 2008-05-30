/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.web;

import org.picocontainer.MutablePicoContainer;

/**
 * <p>
 * Interface for populating a container.  The concern here is only on the
 * container to populate and not on the composition or the building.
 * </p>
 * 
 * @author Mauro Talevi
 * @deprecated Use ContainerBuilder and populate container via MutablePicoContainer API
 */
public interface ContainerPopulator {
    
    /**
     * Populate a container 
     * @param container the MutablePicoContainer to populate
     */
    public void populateContainer(MutablePicoContainer container);

}
