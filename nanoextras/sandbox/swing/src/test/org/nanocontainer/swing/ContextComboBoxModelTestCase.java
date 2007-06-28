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
package org.nanocontainer.swing;

import junit.framework.TestCase;

import javax.swing.JComboBox;
import javax.swing.JFrame;

public class ContextComboBoxModelTestCase extends TestCase {
    private String[] values = new String[]{
        "foo.bar.Orange",
        "foo.bar.Apple",
        "foo.bar.Pear",
        "foo.bar.Application"
    };

    public void testModelWithEmptyFilterHasNone() {
        ContextComboBoxModel model = new ContextComboBoxModel();
        model.setValues(values);
        model.setFilter("");
        assertEquals(0, model.getSize());
    }

    public void testModelWithFilterHasSome() {
        ContextComboBoxModel model = new ContextComboBoxModel();
        model.setValues(values);
        model.setFilter("App");
        assertEquals(2, model.getSize());
    }

    public void testFilterIsCaseInsensitive() {
        ContextComboBoxModel model = new ContextComboBoxModel();
        model.setValues(values);
        model.setFilter("app");
        assertEquals(2, model.getSize());
    }

    public void testModelWithStarPrefixedFilterHasSome() {
        ContextComboBoxModel model = new ContextComboBoxModel();
        model.setValues(values);
        model.setFilter("*pp");
        assertEquals(2, model.getSize());
    }

    public void XtestGui() throws InterruptedException {
        ContextComboBoxModel model = new ContextComboBoxModel();
        model.setValues(values);
        JComboBox box = new ContextComboBox(model);
        model.setFilter("app");
        box.setEditable(true);
        JFrame f = new JFrame();
        f.getContentPane().add(box);
        f.setVisible(true);
        f.pack();
        Thread.sleep(200000);
    }

}