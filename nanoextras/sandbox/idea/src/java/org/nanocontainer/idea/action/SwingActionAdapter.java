/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Adapts an IDEA action to a vanilla Swing AbstractAction.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class SwingActionAdapter extends AnAction {
    private final AbstractAction delegate;

    public SwingActionAdapter(AbstractAction delegate) {
        super((String) delegate.getValue(AbstractAction.NAME));
        this.delegate = delegate;

        getTemplatePresentation().setIcon((Icon) delegate.getValue(AbstractAction.SMALL_ICON));
        // get notified when the delegate changes state...
        delegate.addPropertyChangeListener(new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if(name.equals("enabled")) {
                    Boolean enabled = (Boolean) evt.getNewValue();
                    getTemplatePresentation().setEnabled(enabled.booleanValue());
                }
            }
        });
    }

    public void actionPerformed(AnActionEvent event) {
        delegate.actionPerformed(new ActionEvent(this, 0, ""));
    }
}