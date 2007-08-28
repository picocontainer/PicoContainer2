/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.gems.adapters;

import java.io.IOException;

public final class ElephantProxy implements Elephant {
    private final transient Elephant delegate;

    public ElephantProxy(Elephant delegate) {
        this.delegate = delegate;
    }

    public String objects(String one, String two) throws IOException {
        return delegate.objects(one, two);
    }

    public String[] objectsArray(String[] one, String[] two) throws IOException {
        return delegate.objectsArray(one, two);
    }

    public int iint(int a, int b) {
        return delegate.iint(a, b);
    }

    public long llong(long a, long b) {
        return delegate.llong(a, b);
    }

    public byte bbyte(byte a, byte b, byte c) {
        return delegate.bbyte(a, b, c);
    }

    public float ffloat(float a, float b, float c, float d) {
        return delegate.ffloat(a, b, c, d);
    }

    public double ddouble(double a, double b) {
        return delegate.ddouble(a, b);
    }

    public char cchar(char a, char b) {
        return delegate.cchar(a, b);
    }

    public short sshort(short a, short b) {
        return delegate.sshort(a, b);
    }

    public boolean bboolean(boolean a, boolean b) {
        return delegate.bboolean(a, b);
    }

    public boolean[] bbooleanArray(boolean[] a, boolean b[]) {
        return delegate.bbooleanArray(a, b);
    }
}
