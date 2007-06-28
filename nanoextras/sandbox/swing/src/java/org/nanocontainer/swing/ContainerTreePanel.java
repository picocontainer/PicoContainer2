package org.nanocontainer.swing;

import org.nanocontainer.guimodel.BeanProperty;
import org.nanocontainer.guimodel.ComponentAdapterModel;
import org.picocontainer.ComponentAdapter;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
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
public class ContainerTreePanel extends JPanel {
    private final ContainerTree tree;
    private JTable table;

    public ContainerTreePanel(final ContainerTree tree, JComponent toolbar) {
        super(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        this.tree = tree;
        this.table = new JTable();

        JScrollPane topPane = new JScrollPane(this.tree);
        topPane.setMinimumSize(new Dimension(300, 150));
        topPane.setPreferredSize(new Dimension(400, 200));

        JScrollPane bottomPane = new JScrollPane(this.table);
        bottomPane.setMinimumSize(new Dimension(300, 150));
        bottomPane.setPreferredSize(new Dimension(400, 200));

        splitPane.setTopComponent(topPane);
        splitPane.setBottomComponent(bottomPane);

        add(toolbar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // This selection listener update the table according
        // to the selected node.
        this.tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent evt) {
                final TreePath selPath = evt.getNewLeadSelectionPath();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ComponentAdapter componentAdapter = (ComponentAdapter) selPath.getLastPathComponent();

                        TableModel model = ComponentAdapterTableModel.getInstance(componentAdapter);
                        table.setModel(model);

                        validate();
                    }
                });
            }
        });

        // This mouse listener displays an edit dialog when
        // a property is selected.
        this.table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TreePath path = tree.getSelectionPath();
                    ComponentAdapter obj = (ComponentAdapter) path.getLastPathComponent();

                    ComponentAdapterModel model = ComponentAdapterModel.getInstance(obj);
                    BeanProperty bp = model.getProperty(table.getSelectedRow());

                    // Only display the dialog if we can set the value
                    // through its editor
                    if (bp.isWritable() && (bp.getPropertyEditor() != null)) {
                        BeanPropertyEditDialog dialog = new BeanPropertyEditDialog(bp);
                        dialog.show();
                        table.repaint();
                    }
                }
            }
        });
    }
}
