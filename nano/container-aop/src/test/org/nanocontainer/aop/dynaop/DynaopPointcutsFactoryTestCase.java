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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.nanocontainer.aop.ClassPointcut;
import org.nanocontainer.aop.ComponentPointcut;
import org.nanocontainer.aop.MethodPointcut;
import org.nanocontainer.aop.PointcutsFactory;
import org.nanocontainer.testmodel.Dao;

import dynaop.util.NestedException;

/**
 * @author Stephen Molitor
 */
public final class DynaopPointcutsFactoryTestCase {

    private final PointcutsFactory cuts = new DynaopPointcutsFactory();
    private Method apple;
    private Method apricot;
    private Method banana;
    private Method getA;
    private Method misleadingGetA;
    private Method isA;
    private Method getB;
    private Method isB;
    private Method setA;
    private Method setB;
    private Method equals;
    private Method hashCode;
    private Method toString;
    private Method subFooMethod;

    @Test public void testAllClasses() {
        ClassPointcut cut = cuts.allClasses();
        assertTrue(cut.picks(Object.class));
        assertTrue(cut.picks(Serializable.class));
        assertTrue(cut.picks(Foo.class));
        assertTrue(cut.picks(this.getClass()));
    }

    @Test public void testInstancesOf() {
        ClassPointcut cut = cuts.instancesOf(Serializable.class);
        assertTrue(cut.picks(Serializable.class));
        assertTrue(cut.picks(String.class));
        assertFalse(cut.picks(Foo.class));
    }

    @Test public void testClassName() {
        ClassPointcut cut = cuts.className("Foo*");
        assertTrue(cut.picks(Foo.class));
        assertTrue(cut.picks(FooBar.class));
        assertFalse(cut.picks(Bar.class));

        assertFalse(cuts.className("^Foo*").picks(Foo.class));
    }

    @Test public void testOneClass() {
        ClassPointcut cut = cuts.oneClass(Foo.class);
        assertTrue(cut.picks(Foo.class));
        assertFalse(cut.picks(FooBar.class));
    }

    @Test public void testPackageName() {
        ClassPointcut cut = cuts.packageName("org.nanocontainer.aop.dynaop");
        assertTrue(cut.picks(Foo.class));
        assertTrue(cut.picks(Bar.class));
        assertFalse(cut.picks(org.nanocontainer.testmodel.Dao.class));
    }

    @Test public void testClassPointcutIntersection() {
        ClassPointcut a = cuts.union(cuts.oneClass(Foo.class), cuts.oneClass(Bar.class));
        ClassPointcut b = cuts.union(cuts.oneClass(Foo.class), cuts.oneClass(FooBar.class));
        ClassPointcut c = cuts.intersection(a, b);
        assertTrue(c.picks(Foo.class));
        assertFalse(c.picks(Bar.class));
        assertFalse(c.picks(FooBar.class));
    }

    @Test public void testClassPointcutNot() {
        ClassPointcut cut = cuts.not(cuts.oneClass(Foo.class));
        assertFalse(cut.picks(Foo.class));
        assertTrue(cut.picks(Bar.class));
    }

    @Test public void testClassPointcutUnion() {
        ClassPointcut cut = cuts.union(cuts.oneClass(Foo.class), cuts.oneClass(Bar.class));
        assertTrue(cut.picks(Foo.class));
        assertTrue(cut.picks(Bar.class));
        assertFalse(cut.picks(FooBar.class));
    }

    @Test public void testAllMethods() {
        MethodPointcut cut = cuts.allMethods();
        Method[] methods = Foo.class.getMethods();
        for (Method method : methods) {
            assertTrue(cut.picks(method));
        }
    }

    @Test public void testGetMethods() {
        MethodPointcut cut = cuts.getMethods();
        assertTrue(cut.picks(getA));
        assertTrue(cut.picks(getB));
        assertTrue(cut.picks(misleadingGetA));
        assertFalse(cut.picks(isA));
        assertFalse(cut.picks(isB));
        assertFalse(cut.picks(setA));
        assertFalse(cut.picks(setB));
        assertFalse(cut.picks(apple));
        assertFalse(cut.picks(apricot));
        assertFalse(cut.picks(banana));
    }

    @Test public void testIsMethods() {
        MethodPointcut cut = cuts.isMethods();
        assertFalse(cut.picks(getA));
        assertFalse(cut.picks(getB));
        assertTrue(cut.picks(isA));
        assertTrue(cut.picks(isB));
        assertFalse(cut.picks(setA));
        assertFalse(cut.picks(setB));
        assertFalse(cut.picks(apple));
        assertFalse(cut.picks(apricot));
        assertFalse(cut.picks(banana));
    }

    @Test public void testSetMethods() {
        MethodPointcut cut = cuts.setMethods();
        assertFalse(cut.picks(getA));
        assertFalse(cut.picks(getB));
        assertFalse(cut.picks(isA));
        assertFalse(cut.picks(isB));
        assertTrue(cut.picks(setA));
        assertTrue(cut.picks(setB));
        assertFalse(cut.picks(apple));
        assertFalse(cut.picks(apricot));
        assertFalse(cut.picks(banana));
    }

    @Test public void testSignature() {
        assertTrue(cuts.signature("getA").picks(getA));
        assertTrue(cuts.signature("getA").picks(misleadingGetA));
        assertTrue(cuts.signature("getA()").picks(getA));
        assertTrue(cuts.signature("String getA\\(String\\)").picks(misleadingGetA));
        assertFalse(cuts.signature("String getA\\(String\\)").picks(getA));
        assertTrue(cuts.signature("get*").picks(getA));
        assertTrue(cuts.signature("get*").picks(getB));
        assertTrue(cuts.signature("get*").picks(misleadingGetA));
    }

    @Test public void testOneMethod() {
        MethodPointcut cut = cuts.oneMethod(getA);
        assertTrue(cut.picks(getA));
        assertFalse(cut.picks(getB));
        assertFalse(cut.picks(isA));
        assertFalse(cut.picks(isB));
        assertFalse(cut.picks(setA));
        assertFalse(cut.picks(setB));
        assertFalse(cut.picks(apple));
        assertFalse(cut.picks(apricot));
        assertFalse(cut.picks(banana));
    }

    @Test public void testReturnType() {
        MethodPointcut cut = cuts.returnType(cuts.oneClass(String.class));
        assertTrue(cut.picks(getA));
        assertTrue(cut.picks(getB));
        assertFalse(cut.picks(isA));
        assertFalse(cut.picks(isB));
        assertFalse(cut.picks(setA));
        assertFalse(cut.picks(setB));
        assertFalse(cut.picks(apple));
        assertFalse(cut.picks(apricot));
        assertFalse(cut.picks(banana));
    }

    @Test public void testMethodPointcutIntersection() {
        MethodPointcut a = cuts.union(cuts.oneMethod(apple), cuts.oneMethod(apricot));
        MethodPointcut b = cuts.union(cuts.oneMethod(apple), cuts.oneMethod(banana));
        MethodPointcut c = cuts.intersection(a, b);

        assertTrue(c.picks(apple));
        assertFalse(c.picks(apricot));
        assertFalse(c.picks(banana));
    }

    @Test public void testMethodPointcutNot() {
        MethodPointcut cut = cuts.not(cuts.oneMethod(getA));
        assertFalse(cut.picks(getA));
        assertTrue(cut.picks(getB));
        assertTrue(cut.picks(isA));
        assertTrue(cut.picks(isB));
        assertTrue(cut.picks(setA));
        assertTrue(cut.picks(setB));
        assertTrue(cut.picks(apple));
        assertTrue(cut.picks(apricot));
        assertTrue(cut.picks(banana));
    }

    @Test public void testMethodPointcutUnion() {
        MethodPointcut cut = cuts.union(cuts.oneMethod(apple), cuts.oneMethod(apricot));
        assertTrue(cut.picks(apple));
        assertTrue(cut.picks(apricot));
        assertFalse(cut.picks(banana));
        assertFalse(cut.picks(getA));
        assertFalse(cut.picks(getB));
        assertFalse(cut.picks(isA));
        assertFalse(cut.picks(isB));
        assertFalse(cut.picks(setA));
        assertFalse(cut.picks(setB));
    }

    @Test public void testComponent() {
        ComponentPointcut cut = cuts.component(Dao.class);
        assertTrue(cut.picks(Dao.class));
        assertFalse(cut.picks("Dao"));
    }

    @Test public void testComponentName() {
        ComponentPointcut cut = cuts.componentName("foo*");
        assertTrue(cut.picks("foo"));
        assertTrue(cut.picks("foobar"));
        assertFalse(cut.picks("bar"));
    }

    @Test public void testMalformedPatternExceptionRethrown() {
        try {
            cuts.className("(");
            fail("NestedException should have been raised");
        } catch (NestedException e) {
        }

        try {
            cuts.signature("(");
            fail("NestedException should have been raised");
        } catch (NestedException e) {
        }
    }

    @Test public void testObjectMethods() {
        MethodPointcut cut = cuts.objectMethods();
        assertTrue(cut.picks(equals));
        assertTrue(cut.picks(hashCode));
        assertTrue(cut.picks(toString));
        assertFalse(cut.picks(isA));
        assertFalse(cut.picks(setA));
    }

    @Test public void testDeclaringClass() {
        MethodPointcut cut = cuts.declaringClass(cuts.oneClass(Object.class));
        assertTrue(cut.picks(equals));
        assertTrue(cut.picks(hashCode));
        assertTrue(cut.picks(toString));
        assertFalse(cut.picks(isA));
        assertFalse(cut.picks(setA));
    }

    @Test public void testMembersOf() {
        MethodPointcut cut = cuts.membersOf(Foo.class);
        assertTrue(cut.picks(apple));
        assertTrue(cut.picks(equals));
        assertFalse(cut.picks(subFooMethod));
    }

    @Test public void testCustomClassPointcuts() {
        ClassPointcut picksFoo = new ClassPointcut() {
            public boolean picks(Class clazz) {
                return clazz.equals(Foo.class);
            }
        };
        ClassPointcut picksBar = new ClassPointcut() {
            public boolean picks(Class clazz) {
                return clazz.equals(Bar.class);
            }
        };
        ClassPointcut cut = cuts.union(picksFoo, picksBar);
        assertTrue(cut.picks(Foo.class));
        assertTrue(cut.picks(Bar.class));
        assertFalse(cut.picks(FooBar.class));
    }

    @Test public void testCustomMethodPointcuts() {
        MethodPointcut picksApple = new MethodPointcut() {
            public boolean picks(Method method) {
                return method.equals(apple);
            }
        };
        MethodPointcut picksApricot = new MethodPointcut() {
            public boolean picks(Method method) {
                return method.equals(apricot);
            }
        };
        MethodPointcut cut = cuts.union(picksApple, picksApricot);
        assertTrue(cut.picks(apple));
        assertTrue(cut.picks(apricot));
        assertFalse(cut.picks(banana));
    }

    @Before public void setUp() throws Exception {
        apple = Foo.class.getMethod("apple");
        apricot = Foo.class.getMethod("apricot");
        banana = Foo.class.getMethod("banana");
        getA = Foo.class.getMethod("getA");
        misleadingGetA = Foo.class.getMethod("getA", String.class);
        isA = Foo.class.getMethod("isA");
        getB = Foo.class.getMethod("getB");
        isB = Foo.class.getMethod("isB");
        setA = Foo.class.getMethod("setA", String.class);
        setB = Foo.class.getMethod("setA", String.class);
        equals = Object.class.getMethod("equals", Object.class);
        hashCode = Object.class.getMethod("hashCode");
        toString = Object.class.getMethod("toString");
        subFooMethod = SubFoo.class.getMethod("subFooMethod");
    }

}