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

import dynaop.ClassPointcut;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @author Stephen Molitor
 */
public final class DynaopClassPointcutTestCase extends MockObjectTestCase {

    private final Mock mockDelegate = mock(dynaop.ClassPointcut.class);

    public void testPicks() {
        mockDelegate.expects(once()).method("picks").with(eq(String.class)).will(returnValue(false));
        mockDelegate.expects(once()).method("picks").with(eq(Integer.class)).will(returnValue(true));

        ClassPointcut pointcut = new DynaopClassPointcut((ClassPointcut) mockDelegate.proxy());
        assertFalse(pointcut.picks(String.class));
        assertTrue(pointcut.picks(Integer.class));
    }

}