/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.struts2;

import org.picocontainer.PicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Storing;

/**
 * Allow for the creation of app/session/request containers for a Struts2 web-app
 */
public interface WebAppComposition {
    PicoContainer compose(MutablePicoContainer rootContainer, Storing sessionStoring, Storing requestStoring);
}
