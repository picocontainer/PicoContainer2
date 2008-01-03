/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Vector;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Constraint;
import org.junit.Test;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.ComponentMonitorStrategy;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.monitors.AbstractComponentMonitor;

/**
 * @author Mauro Talevi
 */
public class AbstractComponentMonitorTestCase extends MockObjectTestCase  {

    @Test public void testDelegatingMonitorThrowsExpectionWhenConstructionWithNullDelegate(){
        try {
            new AbstractComponentMonitor(null);
            fail("NPE expected");
        } catch (NullPointerException e) {
            assertEquals("NPE", "monitor", e.getMessage());
        }
    }

    @Test public void testDelegatingMonitorThrowsExpectionWhenChangingToNullMonitor(){
        AbstractComponentMonitor dcm = new AbstractComponentMonitor();
        try {
            dcm.changeMonitor(null);
            fail("NPE expected");
        } catch (NullPointerException e) {
            assertEquals("NPE", "monitor", e.getMessage());
        }
    }

    @Test public void testDelegatingMonitorCanChangeMonitorInDelegateThatDoesSupportMonitorStrategy() {
        ComponentMonitor monitor = mockMonitorWithNoExpectedMethods();
        AbstractComponentMonitor dcm = new AbstractComponentMonitor(mockMonitorThatSupportsStrategy(monitor));
        dcm.changeMonitor(monitor);
        assertEquals(monitor, dcm.currentMonitor());
        dcm.instantiating(null, null, null);
    }

    @Test public void testDelegatingMonitorChangesDelegateThatDoesNotSupportMonitorStrategy() {
        ComponentMonitor delegate = mockMonitorWithNoExpectedMethods();
        AbstractComponentMonitor dcm = new AbstractComponentMonitor(delegate);
        ComponentMonitor monitor = mockMonitorWithNoExpectedMethods();
        assertEquals(delegate, dcm.currentMonitor());
        dcm.changeMonitor(monitor);
        assertEquals(monitor, dcm.currentMonitor());
    }

    @Test public void testDelegatingMonitorReturnsDelegateThatDoesNotSupportMonitorStrategy() {
        ComponentMonitor delegate = mockMonitorWithNoExpectedMethods();
        AbstractComponentMonitor dcm = new AbstractComponentMonitor(delegate);
        assertEquals(delegate, dcm.currentMonitor());
    }

    private ComponentMonitor mockMonitorWithNoExpectedMethods() {
        Mock mock = mock(ComponentMonitor.class);
        return (ComponentMonitor)mock.proxy();
    }

    private ComponentMonitor mockMonitorThatSupportsStrategy(ComponentMonitor currentMonitor) {
        Mock mock = mock(TestMonitorThatSupportsStrategy.class);
        mock.expects(once()).method("changeMonitor").with(eq(currentMonitor));
        mock.expects(once()).method("currentMonitor").withAnyArguments().will(returnValue(currentMonitor));
        mock.expects(once()).method("instantiating").withAnyArguments();
        return (ComponentMonitor)mock.proxy();
    }

    @Test public void testMonitoringHappensBeforeAndAfterInstantiation() throws NoSuchMethodException {
        final Vector ourIntendedInjectee0 = new Vector();
        final String ourIntendedInjectee1 = "hullo";
        DefaultPicoContainer parent = new DefaultPicoContainer();
        Mock monitor = mock(ComponentMonitor.class);
        DefaultPicoContainer child = new DefaultPicoContainer(new AbstractComponentMonitor((ComponentMonitor) monitor.proxy()), parent);

        Constructor nacotCtor = NeedsACoupleOfThings.class.getConstructors()[0];
        monitor.expects(once()).method("instantiating").with(same(child), isA(ConstructorInjector.class), eq(nacotCtor)).will(returnValue(nacotCtor));
        Constraint durationIsGreaterThanOrEqualToZero = new Constraint() {
            public boolean eval(Object o) {
                Long duration = (Long)o;
                return 0 <= duration;
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return stringBuffer.append("The endTime wasn't after the startTime");
            }
        };
        Constraint isANACOTThatWozCreated = new Constraint() {
            public boolean eval(Object o) {
                return o instanceof NeedsACoupleOfThings;
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return stringBuffer.append("Should have been a hashmap");
            }
        };
        Constraint collectionAndStringWereInjected = new Constraint() {
            public boolean eval(Object o) {
                Object[] ctorArgs = (Object[]) o;
                return ctorArgs.length == 2 && ctorArgs[0] == ourIntendedInjectee0 && ctorArgs[1] == ourIntendedInjectee1;
            }
            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return stringBuffer.append("Should have injected our intended vector and string");
            }
        };
        monitor.expects(once()).method("instantiated").with(new Constraint[] {same(child), isA(ConstructorInjector.class),eq(nacotCtor), isANACOTThatWozCreated, collectionAndStringWereInjected, durationIsGreaterThanOrEqualToZero});
        parent.addComponent(ourIntendedInjectee0);
        parent.addComponent(ourIntendedInjectee1);
        child.addComponent(NeedsACoupleOfThings.class);
        child.getComponent(NeedsACoupleOfThings.class);
    }

    public static class NeedsACoupleOfThings {
        public NeedsACoupleOfThings(Collection collection, String string) {
        }
    }

    public static interface TestMonitorThatSupportsStrategy extends ComponentMonitor, ComponentMonitorStrategy {
    }
}
