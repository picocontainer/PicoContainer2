/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.idea.action;

import org.nanocontainer.swing.action.StartContainerAction;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class StartContainer extends SwingActionAdapter {
    public StartContainer(StartContainerAction delegate) {
        super(delegate);
    }
}
