/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.picocontainer.script.groovy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Stephen Molitor
 */
public class LoggingInterceptor implements MethodInterceptor {

    private final StringBuffer log;

    public LoggingInterceptor(StringBuffer log) {
        this.log = log;
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.append("start");
        Object result = invocation.proceed();
        log.append("end");
        return result;
    }

}