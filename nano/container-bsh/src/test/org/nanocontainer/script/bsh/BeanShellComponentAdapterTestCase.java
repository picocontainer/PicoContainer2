/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Leo Simons                                               *
 *****************************************************************************/
package org.nanocontainer.script.bsh;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;

/**
 * <a href="http://www.junit.org/">JUnit</a>
 * {@link junit.framework.TestCase testcase} for
 * BeanShellAdapter.
 *
 * @author <a href="mail at leosimons dot com">Leo Simons</a>
 * @author Nick Sieger
 * @version $Id$
 */
public class BeanShellComponentAdapterTestCase extends TestCase {

    private MutablePicoContainer pico;

    ComponentAdapter setupComponentAdapter(Class implementation) {
        pico = new DefaultPicoContainer();
        pico.addComponent("whatever", ArrayList.class);

        ComponentAdapter adapter = new BeanShellAdapter("thekey", implementation, null);
        pico.addAdapter(adapter);
        return adapter;
    }

    public void testGetComponentInstance() {
        ComponentAdapter adapter = setupComponentAdapter(ScriptableDemoBean.class);

        ScriptableDemoBean bean = (ScriptableDemoBean) adapter.getComponentInstance(pico);

        assertEquals("Bsh demo script should have set the key", "thekey", bean.key);

        assertTrue(bean.whatever instanceof ArrayList);
    }

    public void testGetComponentInstanceBadScript() {
        ComponentAdapter adapter = setupComponentAdapter(BadScriptableDemoBean.class);

        try {
            adapter.getComponentInstance(pico);
            fail("did not throw exception on missing 'instance' variable");
        } catch (BeanShellScriptCompositionException bssie) {
            // success
        }
    }

}
