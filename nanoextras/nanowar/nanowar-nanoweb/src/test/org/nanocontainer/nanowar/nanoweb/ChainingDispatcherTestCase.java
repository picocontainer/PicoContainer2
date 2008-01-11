package org.nanocontainer.nanowar.nanoweb;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * @author Aslak Helles&oslash;y
 */
public class ChainingDispatcherTestCase {

    @Test public void testDispatcherChain() {
        ChainingDispatcher dispatcher = new ChainingDispatcher(".vm");
        String[] views = dispatcher.getViews("/foo/bar", "zap", "success");
        assertEquals("/foo/bar_zap_success.vm", views[0]);
        assertEquals("/foo/bar_success.vm", views[1]);
        assertEquals("/foo/success.vm", views[2]);
        assertEquals("/success.vm", views[3]);
    }

}