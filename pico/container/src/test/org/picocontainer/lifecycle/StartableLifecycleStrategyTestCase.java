/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.lifecycle;

import java.io.Serializable;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.Disposable;
import org.picocontainer.Startable;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

/**
 * 
 * @author Mauro Talevi
 */
public class StartableLifecycleStrategyTestCase extends MockObjectTestCase {

    private StartableLifecycleStrategy strategy;
    
    public void setUp(){
        strategy = new StartableLifecycleStrategy(NullComponentMonitor.getInstance());
    }

    public void testStartable(){
        Object startable = mockComponent(true, false);
        strategy.start(startable);
        strategy.stop(startable);
    }

    public void testDisposable(){
        Object startable = mockComponent(false, true);
        strategy.dispose(startable);
    }

    public void testSerializable(){
        Object serializable = mockComponent(false, false);
        strategy.start(serializable);
        strategy.stop(serializable);
        strategy.dispose(serializable);
    }
    
    private Object mockComponent(boolean startable, boolean disposeable) {
        Mock mock = mock(Serializable.class);
        if ( startable ) {
            mock = mock(Startable.class);
            mock.expects(atLeastOnce()).method("start");
            mock.expects(atLeastOnce()).method("stop");
        }
        if ( disposeable ) {
            mock = mock(Disposable.class);
            mock.expects(atLeastOnce()).method("dispose");
        }
        return mock.proxy();
    }
}
