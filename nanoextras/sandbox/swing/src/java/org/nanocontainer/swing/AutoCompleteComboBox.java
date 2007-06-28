package org.nanocontainer.swing;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.Vector;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class AutoCompleteComboBox extends JComboBox {
    private static final Locale[] INSTALLED_LOCALES = Locale.getAvailableLocales();
    private ComboBoxModel model = null;

    public static void main(String[] args) {
        JFrame f = new JFrame("AutoCompleteComboBox");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        AutoCompleteComboBox box = new AutoCompleteComboBox(INSTALLED_LOCALES, false);
        f.getContentPane().add(box);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    public AutoCompleteComboBox(Object[] items, boolean caseSensitive) {
        super(items);
        model = new ComboBoxModel(items);
        setModel(model);
        setEditable(true);
        setEditor(new AutoCompleteEditor(this, caseSensitive));
    }

    public AutoCompleteComboBox(Vector items, boolean caseSensitive) {
        super(items);
        model = new ComboBoxModel(items);
        setModel(model);
        setEditable(true);
        setEditor(new AutoCompleteEditor(this, caseSensitive));
    }

    public AutoCompleteComboBox(boolean caseSensitive) {
        super();
        setEditable(true);
        setEditor(new AutoCompleteEditor(this, caseSensitive));
    }

    public class ComboBoxModel extends DefaultComboBoxModel {
        private TreeSet values = null;

        public ComboBoxModel(List items) {
            super();
            this.values = new TreeSet();
            int i, c;
            for (i = 0, c = items.size(); i < c; i++) values.add(items.get(i).toString());
            Iterator it = values.iterator();
            while (it.hasNext()) super.addElement(it.next().toString());
        }

        public ComboBoxModel(final Object items[]) {
            this(Arrays.asList(items));
        }
    }

    public class AutoCompleteEditor extends BasicComboBoxEditor {
        public AutoCompleteEditor(JComboBox combo, boolean caseSensitive) {
            super();
            editor = new AutoCompleteEditorComponent(combo, caseSensitive);
        }
    }

    public class AutoCompleteEditorComponent extends JTextField {
        JComboBox combo = null;
        boolean caseSensitive = false;

        public AutoCompleteEditorComponent(JComboBox combo, boolean caseSensitive) {
            super();
            this.combo = combo;
            this.caseSensitive = caseSensitive;
        }

        protected Document createDefaultModel() {
            return new PlainDocument() {
                public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                    if (str == null || str.length() == 0) return;
                    int size = combo.getItemCount();
                    String text = getText(0, getLength());
                    for (int i = 0; i < size; i++) {
                        String item = combo.getItemAt(i).toString();
                        if (getLength() + str.length() > item.length()) continue;
                        if (!caseSensitive) {
                            if ((text + str).equalsIgnoreCase(item) || item.substring(0, getLength() + str.length()).equalsIgnoreCase(text + str)) {
                                combo.setSelectedIndex(i);
                                super.remove(0, getLength());
                                super.insertString(0, item, a);
                                return;
                            }
                        } else if (caseSensitive) {
                            if ((text + str).equals(item) || item.substring(0, getLength() + str.length()).equals(text + str)) {
                                combo.setSelectedIndex(i);
                                super.remove(0, getLength());
                                super.insertString(0, item, a);
                                return;
                            }
                        }
                    }
                }
            };
        }
    }
}

