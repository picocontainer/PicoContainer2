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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.nanocontainer.guimodel.BeanProperty;
import org.picocontainer.PicoContainer;

/**
 * An horizontal sash form that displays a PicoContainer tree (top) and a
 * table with selected node properties (bottom).
 *
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class ContainerTreePanel extends SashForm {

    /**
     * Default constructor
     *
     * @param parent
     * @param flags
     */
    public ContainerTreePanel(Composite parent, int flags) {
        super(parent, flags);
        this.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    /**
     * Helper method that allows to set-up the root container.
     *
     * @param container
     */
    public void setContainer(PicoContainer container) {
        // Create the TreeViewer
        ContainerTreeViewer tv = new ContainerTreeViewer(this, SWT.BORDER);
        tv.setContainer(container);

        // Create the TableViewer
        final TableViewer tbv = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE);
        tbv.setContentProvider(new ComponentAdapterContentProvider());
        tbv.setLabelProvider(new BeanLabelProvider());

        TableLayout tableLayout = new TableLayout();
        tableLayout.addColumnData(new ColumnWeightData(100));
        tableLayout.addColumnData(new ColumnWeightData(100));
        tbv.getTable().setLayout(tableLayout);
        tbv.getTable().setHeaderVisible(true);
        tbv.getTable().setLinesVisible(true);

        TableColumn column = new TableColumn(tbv.getTable(), SWT.NONE);
        column.setText("Property");
        column = new TableColumn(tbv.getTable(), SWT.NONE);
        column.setText("Value");

        // This selection listener is responsible to change the
        // table viewer according to the selected node in the tree.
        tv.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                Object selected = selection.getFirstElement();
                tbv.setInput(selected);
                tbv.refresh();
            }
        });

        // This mouse listener displays an edit dialog when
        // a property is selected.
        tbv.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                Object selected = selection.getFirstElement();
                BeanProperty bp = (BeanProperty) selected;

                // Only display the dialog if we can set the value
                // through its editor
                if (bp.isWritable() && (bp.getPropertyEditor() != null)) {
                    BeanPropertyEditDialog dialog = new BeanPropertyEditDialog(null, bp);
                    if (dialog.open() == IDialogConstants.OK_ID) {
                        tbv.refresh();
                    }
                }
            }
        });
    }
}
