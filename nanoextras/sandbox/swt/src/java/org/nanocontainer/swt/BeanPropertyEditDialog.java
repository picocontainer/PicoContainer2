/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.swt;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nanocontainer.guimodel.BeanProperty;

import java.beans.PropertyEditor;

/**
 * A dialog to edit a bean property
 * <p>A few restrictions have been set :<ul>
 * <li>The BeanProperty must have a PropertyEditor</li>
 * <li>CustomEditor are not supported</li>
 * </ul></p>
 *
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class BeanPropertyEditDialog extends Dialog {
    private BeanProperty property;
    private Text valueText;
    private PropertyEditor editor;

    /**
     * Build a dialog around a BeanProperty
     *
     * @param parentShell
     * @param p
     */
    public BeanPropertyEditDialog(Shell parentShell, BeanProperty p) {
        super(parentShell);
        this.property = p;
        this.editor = this.property.getPropertyEditor();
        this.editor.setValue(this.property.getValue());
    }


    /**
     * Set up a nice title
     *
     * @param shell
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Bean Property");
    }

    /**
     * Creates the content of the dialog
     *
     * @param parent
     * @return
     */
    protected Control createDialogArea(Composite parent) {
        GridData layoutData;
        Composite global = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = new GridLayout(2, false);
        global.setLayout(gridLayout);

        Label descLabel = new Label(global, SWT.NONE);
        descLabel.setText("Enter a the value for this property");
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 2;
        descLabel.setLayoutData(layoutData);

        Label nameLabel = new Label(global, SWT.NONE);
        nameLabel.setText("Name");
        layoutData = new GridData();
        layoutData.horizontalAlignment = SWT.RIGHT;
        nameLabel.setLayoutData(layoutData);

        Label nameText = new Label(global, SWT.BORDER);
        nameText.setText(this.property.getName());
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.grabExcessHorizontalSpace = true;
        nameText.setLayoutData(layoutData);

        Label valueLabel = new Label(global, SWT.NONE);
        valueLabel.setText("Value");
        layoutData = new GridData();
        layoutData.horizontalAlignment = SWT.RIGHT;
        valueLabel.setLayoutData(layoutData);

        this.valueText = new Text(global, SWT.BORDER);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.grabExcessHorizontalSpace = true;
        this.valueText.setLayoutData(layoutData);

        this.valueText.setText(this.editor.getAsText());
        this.valueText.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent event) {
                editor.setAsText(valueText.getText());
            }
        });

        return parent;
    }

    /**
     * Triggered by a OK push.
     */
    protected void okPressed() {
        // Copy modified value
        this.property.setValue(this.editor.getValue());
        super.okPressed();
    }
}
