/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 * Original Code By: Centerline Computers, Inc.                              *
 *****************************************************************************/
package org.picocontainer.injectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Michael Rimov
 *
 */
public class PrimitiveMemberCheckerTestCase {
    
    
    @SuppressWarnings("unused") 
    public static class TestClass {
        
        public String able;
        
        public int baker;
        
        public TestClass(int value) {
           //Does nothing.
        }
        
        public TestClass(String value) {
            //Does nothing.            
        }
        
        
        public void doSomething(String avalue) {
            //Does nothing.            
        }
        
        public void doSomething(int anotherValue ) {
            //Does nothing.            
        }
    }

    private PrimitiveMemberChecker checker;

    @Before
    public void setUp() {
        checker = new PrimitiveMemberChecker();
    }
    
    @After
    public void tearDown() {
        checker = null;
    }
    
    /**
     * Test method for {@link org.picocontainer.injectors.PrimitiveMemberChecker#isPrimitiveArgument(java.lang.reflect.AccessibleObject, int)}.
     * @throws NoSuchFieldException 
     */
    @Test
    public void testIsPrimitiveField() throws NoSuchFieldException {
        Field targetOne = TestClass.class.getField("able");
        assertFalse(checker.isPrimitiveArgument(targetOne, 0));
        
        Field targetTwo = TestClass.class.getField("baker");
        assertTrue(checker.isPrimitiveArgument(targetTwo, 0));
    }
    
    @Test
    public void testIsPrimitiveConstructorArg() throws NoSuchMethodException {
        Constructor cOne = TestClass.class.getConstructor(Integer.TYPE);
        assertTrue(checker.isPrimitiveArgument(cOne, 0));
        
        Constructor cTwo = TestClass.class.getConstructor(String.class);
        assertFalse(checker.isPrimitiveArgument(cTwo, 0));
    }
    
    @Test
    public void testIsPrimitiveMethodArg() throws NoSuchMethodException {
        Method mOne = TestClass.class.getMethod("doSomething", Integer.TYPE);
        assertTrue(checker.isPrimitiveArgument(mOne, 0));
        
        Method mTwo = TestClass.class.getMethod("doSomething", String.class);
        assertFalse(checker.isPrimitiveArgument(mTwo, 0));        
    }

    
    @Test
    public void testArrayIndexOutOfBoundsIfIntegerArgTooBig() throws SecurityException, NoSuchMethodException {
        Method mOne = TestClass.class.getMethod("doSomething", Integer.TYPE);
        try {
            boolean result = checker.isPrimitiveArgument(mOne, 1);
            fail("Should have thrown exception, instead got return value " + result);
        } catch (ArrayIndexOutOfBoundsException e) {
            //Message contents differentiate from a generic exception
            assertTrue(e.getMessage().contains("Index i > types array length "));
        }
    }
        
}
