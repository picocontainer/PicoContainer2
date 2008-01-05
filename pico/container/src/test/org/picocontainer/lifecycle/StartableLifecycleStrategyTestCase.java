/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.lifecycle;

import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import java.io.Serializable;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picocontainer.Disposable;
import org.picocontainer.Startable;
import org.picocontainer.monitors.NullComponentMonitor;

/**
 * 
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public class StartableLifecycleStrategyTestCase {

	private Mockery mockery = mockeryWithCountingNamingScheme();
	
    private StartableLifecycleStrategy strategy;
    
    @Before
    public void setUp(){
        strategy = new StartableLifecycleStrategy(new NullComponentMonitor());
    }

    @Test public void testStartable(){
        Object startable = mockComponent(true, false);
        strategy.start(startable);
        strategy.stop(startable);
    }

    @Test public void testDisposable(){
        Object startable = mockComponent(false, true);
        strategy.dispose(startable);
    }

    @Test public void testSerializable(){
        Object serializable = mockComponent(false, false);
        strategy.start(serializable);
        strategy.stop(serializable);
        strategy.dispose(serializable);
    }
    
    private Object mockComponent(boolean startable, boolean disposeable) {
        if ( startable ) {
        	 final Startable mock = mockery.mock(Startable.class);
        	 mockery.checking(new Expectations() {{
                 one(mock).start(); 
                 one(mock).stop(); 
             }});
        	 return mock;
        }
        if ( disposeable ) {
       	 final Disposable mock = mockery.mock(Disposable.class);
    	 mockery.checking(new Expectations() {{
             one(mock).dispose(); 
         }});
    	 return mock;
        }
        return mockery.mock(Serializable.class);
    }
}
