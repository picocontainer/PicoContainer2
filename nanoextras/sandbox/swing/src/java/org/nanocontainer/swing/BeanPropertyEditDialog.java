/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.swing;

import org.nanocontainer.guimodel.BeanProperty;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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
public class BeanPropertyEditDialog extends JDialog implements ActionListener {
    private BeanProperty property;
    private JTextField valueText;
    private PropertyEditor editor;

    /**
     * Build a dialog around a BeanProperty
     *
     * @param property
     * @throws HeadlessException
     */
    public BeanPropertyEditDialog(BeanProperty property) throws HeadlessException {
        super((JFrame) null, "Bean Property", true);
        this.property = property;
        this.editor = this.property.getPropertyEditor();
        this.editor.setValue(this.property.getValue());

        JPanel panel = (JPanel) this.getContentPane();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gridbagconstraints = new GridBagConstraints();

        JLabel descriptionLabel = new JLabel();
        descriptionLabel.setText("Enter a the value for this property");

        gridbagconstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridbagconstraints.fill = GridBagConstraints.HORIZONTAL;
        gridbagconstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridbagconstraints.weighty = 1.0d;
        panel.add(descriptionLabel, gridbagconstraints);

        JLabel nameLabel = new JLabel();
        nameLabel.setText(this.property.getName());

        gridbagconstraints.anchor = GridBagConstraints.FIRST_LINE_END;
        gridbagconstraints.fill = GridBagConstraints.HORIZONTAL;
        gridbagconstraints.gridwidth = GridBagConstraints.RELATIVE;
        gridbagconstraints.weighty = 0.0d;
        panel.add(nameLabel, gridbagconstraints);

        this.valueText = new JTextField();
        this.valueText.setText(this.editor.getAsText());
        this.valueText.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                editor.setAsText(valueText.getText());
            }
        });

        gridbagconstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridbagconstraints.fill = GridBagConstraints.HORIZONTAL;
        gridbagconstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridbagconstraints.weighty = 1.0d;
        panel.add(this.valueText, gridbagconstraints);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        JButton button = new JButton("Ok");
        button.setActionCommand("OK");
        buttonPanel.add(button);
        button.addActionListener(this);
        button = new JButton("Cancel");
        buttonPanel.add(button);
        button.addActionListener(this);

        gridbagconstraints.anchor = GridBagConstraints.PAGE_START;
        gridbagconstraints.fill = GridBagConstraints.NONE;
        gridbagconstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridbagconstraints.weighty = 0.0d;
        panel.add(buttonPanel, gridbagconstraints);

        this.pack();
    }

    /**
     * If the OK button is pressed, the property is set.
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("OK")) {
            this.property.setValue(this.editor.getValue());
        }
        this.setVisible(false);
    }
}
