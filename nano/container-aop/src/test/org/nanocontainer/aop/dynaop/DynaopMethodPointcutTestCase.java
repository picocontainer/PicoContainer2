/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.aop.dynaop;

import dynaop.MethodPointcut;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import java.lang.reflect.Method;

/**
 * @author Stephen Molitor
 */
public final class DynaopMethodPointcutTestCase extends MockObjectTestCase {

    private final Mock mockDelegate = mock(dynaop.MethodPointcut.class);

    public void testPicks() throws SecurityException, NoSuchMethodException {
        Method method1 = String.class.getMethod("length");
        Method method2 = String.class.getMethod("hashCode");

        mockDelegate.expects(once()).method("picks").with(eq(method1)).will(returnValue(false));
        mockDelegate.expects(once()).method("picks").with(eq(method2)).will(returnValue(true));

        dynaop.MethodPointcut pointcut = new DynaopMethodPointcut((MethodPointcut) mockDelegate.proxy());
        assertFalse(pointcut.picks(method1));
        assertTrue(pointcut.picks(method2));
    }

}
