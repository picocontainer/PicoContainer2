/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
/*
 * Created by IntelliJ IDEA.
 * User: ahelleso
 * Date: 30-Jan-2004
 * Time: 01:51:26
 */
package org.nanocontainer.swing.action;

import org.nanocontainer.swing.ContainerTree;

import java.awt.event.ActionEvent;

public class StopContainerAction extends TreeSelectionAction {
    public StopContainerAction(String iconPath, ContainerTree tree) {
        super("Stop Container", iconPath, tree);
    }

    public void actionPerformed(ActionEvent e) {

    }

    protected void setEnabled() {

    }
}