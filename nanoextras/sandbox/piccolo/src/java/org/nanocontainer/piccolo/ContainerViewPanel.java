/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.piccolo;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import org.nanocontainer.swing.ComponentAdapterTableModel;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * An horizontal split-panel that displays a PicoContainer tree (top) and a
 * table with selected node properties (bottom).
 *
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class ContainerViewPanel extends JSplitPane {
    private ContainerView view;
    private JTable table;
    private PNode lastSelection;

    /**
     * Build a new instance of a panel
     *
     * @param container
     */
    public ContainerViewPanel(PicoContainer container) {
        super(JSplitPane.VERTICAL_SPLIT);
        this.view = new ContainerView(container);
        this.table = new JTable();

        JScrollPane bottomPane = new JScrollPane(this.table);
        bottomPane.setMinimumSize(new Dimension(300, 150));
        bottomPane.setPreferredSize(new Dimension(400, 200));

        this.setTopComponent(this.view);
        this.setBottomComponent(bottomPane);

        // This selection listener update the table according
        // to the selected node.
        this.view.addInputEventListener(new PBasicInputEventHandler() {
            public void mousePressed(PInputEvent event) {
                PNode node = event.getPickedNode();
                if (node instanceof PCamera) {
                } else {
                    lastSelection = node;
                    ComponentAdapter obj = (ComponentAdapter) lastSelection.getClientProperty(Constants.USER_OBJECT);

                    TableModel model = ComponentAdapterTableModel.getInstance(obj);
                    table.setModel(model);
                    validate();
                }
            }
        });

        // This mouse listener displays an edit dialog when
        // a property is selected.
        this.table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    try {
                        Object obj = lastSelection.getClientProperty(Constants.USER_OBJECT);

//						ComponentAdapterModel model = ComponentAdapterModel.getBeanModel(obj.getClass());
//						BeanProperty bp = model.getProperty(obj, table.getSelectedRow());
//
//						// Only display the dialog if we can set the value
//						// through its editor
//						if (bp.isWritable() && (bp.getPropertyEditor() != null)) {
//							BeanPropertyEditDialog dialog = new BeanPropertyEditDialog(bp);
//							dialog.show();
//							table.repaint();
//						}
                    } catch (RuntimeException ex) {
                        // Ignore it
                    }
                }
            }
        });
    }
}
