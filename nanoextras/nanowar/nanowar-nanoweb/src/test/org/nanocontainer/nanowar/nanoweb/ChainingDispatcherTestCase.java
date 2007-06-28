package org.nanocontainer.nanowar.nanoweb;

import junit.framework.TestCase;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ChainingDispatcherTestCase extends TestCase {

    public void testDispatcherChain() {
        ChainingDispatcher dispatcher = new ChainingDispatcher(".vm");
        String[] views = dispatcher.getViews("/foo/bar", "zap", "success");
        assertEquals("/foo/bar_zap_success.vm", views[0]);
        assertEquals("/foo/bar_success.vm", views[1]);
        assertEquals("/foo/success.vm", views[2]);
        assertEquals("/success.vm", views[3]);
    }

}