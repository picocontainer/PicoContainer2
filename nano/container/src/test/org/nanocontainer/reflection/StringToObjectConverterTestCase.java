/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.reflection;

import junit.framework.TestCase;

import java.io.File;

public final class StringToObjectConverterTestCase extends TestCase {
    private final StringToObjectConverter converter = new StringToObjectConverter();

    public void testConvertsToString() {
        assertEquals("hello", converter.convertTo(String.class, "hello"));
        assertEquals("", converter.convertTo(String.class, ""));
    }

    public void testConvertsToInts() {
        assertEquals(22, converter.convertTo(Integer.class, "22"));
        assertEquals(-9, converter.convertTo(Integer.class, "-9"));
    }

    public void testConvertsToLong() {
        assertEquals(123456789012L, converter.convertTo(Long.class, "123456789012"));
        assertEquals(-123456789012L, converter.convertTo(Long.class, "-123456789012"));
        assertEquals((long)0, converter.convertTo(Long.class, "0"));
    }

    public void testConvertsToBooleanUsingBestGuess() {
        assertEquals(Boolean.TRUE, converter.convertTo(Boolean.class, "t"));
        assertEquals(Boolean.TRUE, converter.convertTo(Boolean.class, "true"));
        assertEquals(Boolean.TRUE, converter.convertTo(Boolean.class, "T"));
        assertEquals(Boolean.TRUE, converter.convertTo(Boolean.class, "TRUE"));
        assertEquals(Boolean.TRUE, converter.convertTo(Boolean.class, "1"));
        assertEquals(Boolean.TRUE, converter.convertTo(Boolean.class, "yes"));
        assertEquals(Boolean.TRUE, converter.convertTo(Boolean.class, "Yo!"));

        assertEquals(Boolean.FALSE, converter.convertTo(Boolean.class, "f"));
        assertEquals(Boolean.FALSE, converter.convertTo(Boolean.class, "false"));
        assertEquals(Boolean.FALSE, converter.convertTo(Boolean.class, "FALSE"));
        assertEquals(Boolean.FALSE, converter.convertTo(Boolean.class, "0"));
        assertEquals(Boolean.FALSE, converter.convertTo(Boolean.class, "no"));
        assertEquals(Boolean.FALSE, converter.convertTo(Boolean.class, "nada!"));
        assertEquals(Boolean.FALSE, converter.convertTo(Boolean.class, ""));
        assertEquals(Boolean.FALSE, converter.convertTo(Boolean.class, "I'm a lumberjack and I'm okay"));
    }

    public void testCustomConversionsCanBeRegistered() {
        converter.register(File.class, new Converter() {
            public Object convert(String in) {
                return new File(in);
            }
        });
        assertEquals("hello", converter.convertTo(String.class, "hello"));
        assertEquals(new File("hello"), converter.convertTo(File.class, "hello"));
    }

    public void testNullsMapToDefaultValues() {
        assertNull(converter.convertTo(String.class, null));
        assertEquals(0, converter.convertTo(Integer.class, null));
        assertEquals((long)0, converter.convertTo(Long.class, null));
        assertEquals(Boolean.FALSE, converter.convertTo(Boolean.class, null));
    }

    public void testExceptionThrownIfConverterNotRegistered() {
        try {
            converter.convertTo(File.class, "hello");
            fail("Should have thrown exception");
        } catch (InvalidConversionException e) {
            // good
        }
    }

    public void testDodgyFormatThrowExceptions() {
        try {
            converter.convertTo(Integer.class, "fooo");
            fail("Should have thrown exception");
        } catch (NumberFormatException e) {
            // good
        }
    }

}
