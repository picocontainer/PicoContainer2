/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.ObjectReference;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ReferenceTestCase extends MockObjectTestCase {
    private final String key = "foo";
    private final Object value = new Object();

    public void testRequestScope() throws UnsupportedEncodingException {
        Mock mock = createMock(ServletRequest.class);
        RequestScopeReference ref = new RequestScopeReference((ServletRequest) mock.proxy(), key);
        setGetAndVerify(ref, mock);
    }

    public void testApplicationScope() {
        Mock mock = createMock(ServletContext.class);
        ApplicationScopeReference ref = new ApplicationScopeReference((ServletContext) mock.proxy(), key);
        setGetAndVerify(ref, mock);
    }

    public void testSessionScope() {
        Mock mock = createMock(HttpSession.class);
        SessionScopeReference ref = new SessionScopeReference((HttpSession) mock.proxy(), key);
        setGetAndVerify(ref, mock);
    }

    private void setGetAndVerify(ObjectReference ref, Mock mock) {
        ref.set(value);
        assertEquals(value, ref.get());
    }

    private Mock createMock(final Class clazz) {
        Mock mock = mock(clazz);
        mock.expects(once())
                .method("setAttribute")
                .with(eq(key), eq(value));
        mock.expects(once())
                .method("getAttribute")
                .with(eq(key))
                .will(returnValue(value));
        return mock;
    }

}
