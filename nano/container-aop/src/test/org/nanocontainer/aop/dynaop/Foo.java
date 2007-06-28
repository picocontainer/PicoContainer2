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


public class Foo {

    public void apple() {
    }

    public void apricot() {
    }

    public void banana() {
    }

    public String getA() {
        return "a";
    }

    public String getA(String a) {
        return "a";
    }

    public boolean isA() {
        return false;
    }

    public String getB() {
        return "b";
    }

    public boolean isB() {
        return false;
    }

    public void setA(String a) {
    }

    public void setB(String b) {
    }

}