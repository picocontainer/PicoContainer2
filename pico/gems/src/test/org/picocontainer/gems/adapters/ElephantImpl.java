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

public class ElephantImpl implements Elephant {
    public String objects(String one, String two) throws IOException {
        return one + two;
    }

    public String[] objectsArray(String[] one, String[] two) throws IOException {
        return new String[] { one[0] + two[0]};
    }

    public int iint(int a, int b) {
        return a + b;
    }

    public long llong(long a, long b) {
        return a + b;
    }

    public byte bbyte(byte a, byte b, byte c) {
        return (byte) (a + b + c);
    }

    public float ffloat(float a, float b, float c, float d) {
        return a + b + c + d;
    }

    public double ddouble(double a, double b) {
        return a + b;
    }

    public char cchar(char a, char b) {
        return a == 'a' && b == 'b' ? 'c' : '!';
    }

    public short sshort(short a, short b) {
        return (short) (a + b);
    }

    public boolean bboolean(boolean a, boolean b) {
        return a & b;
    }

    public boolean[] bbooleanArray(boolean[] a, boolean b[]) {
        return new boolean[] { a[0] & b[0]};
    }
}