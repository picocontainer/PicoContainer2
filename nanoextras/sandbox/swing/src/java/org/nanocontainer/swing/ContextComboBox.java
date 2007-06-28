package org.nanocontainer.swing;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ContextComboBox extends JComboBox {
    private ContextComboBoxModel model;
    private JTextField textField;
    private boolean updateField = true;
    private Object item;

    public ContextComboBox(ContextComboBoxModel model) {
        super(model);
        this.model = model;
        setEditable(true);
        setEditor(new BasicComboBoxEditor() {
            {
                textField = (JTextField) getEditorComponent();
                textField.getDocument().addDocumentListener(new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        updatePopup();
                    }

                    public void insertUpdate(DocumentEvent e) {
                        updatePopup();
                    }

                    public void removeUpdate(DocumentEvent e) {
                        updatePopup();
                    }
                });

                textField.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("item = " + item);
                        setSelectedItem(item);
                        updateField = false;
                        textField.setText(item.toString());
                        updateField = true;
                    }
                });
            }
        });
        addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                item = e.getItem();
                final int stateChange = e.getStateChange();
                System.out.println("stateChange = " + stateChange);
            }

        });
    }

    private void updatePopup() {
        if (updateField) {
            final String text = textField.getText();
            updateField = false;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    model.setFilter(text);
                    hidePopup();
                    if (model.getSize() > 0) {
                        showPopup();
                        setSelectedIndex(0);
                        repaint();
                    }

                    textField.setText(text);
                    updateField = true;
                }
            });
        }
    }
}