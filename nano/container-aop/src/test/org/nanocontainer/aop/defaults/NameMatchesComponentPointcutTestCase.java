/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.aop.defaults;

import junit.framework.TestCase;
import org.nanocontainer.aop.ComponentPointcut;
import org.nanocontainer.aop.MalformedRegularExpressionException;
import org.nanocontainer.testmodel.Dao;

/**
 * @author Stephen Molitor
 */
public class NameMatchesComponentPointcutTestCase extends TestCase {

    public void testPicks() throws Exception {
        ComponentPointcut cut1 = new NameMatchesComponentPointcut("^foo$");
        assertTrue(cut1.picks("foo"));
        assertFalse(cut1.picks("barfoo"));
        assertFalse(cut1.picks("foobar"));

        ComponentPointcut cut2 = new NameMatchesComponentPointcut("foo*");
        assertTrue(cut2.picks("foo"));
        assertTrue(cut2.picks("barfoo"));
        assertTrue(cut2.picks("foobar"));
    }

    public void testNotStringComponentKey() {
        ComponentPointcut cut = new NameMatchesComponentPointcut("foo");
        assertFalse(cut.picks(Dao.class));
    }

    public void testConstructorThrowsMalformedRegularExpressionException() {
        try {
            new NameMatchesComponentPointcut("(");
            fail("MalformedRegularExpression exception should have been raised");
        } catch (MalformedRegularExpressionException e) {
        }
    }

}