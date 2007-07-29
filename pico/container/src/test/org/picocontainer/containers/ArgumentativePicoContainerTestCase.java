/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.containers;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.injectors.AbstractInjector;

import java.io.StringReader;
import java.io.IOException;

import junit.framework.TestCase;

public class ArgumentativePicoContainerTestCase extends TestCase {

    public void testBasicParsing() {
        ArgumentativePicoContainer apc = new ArgumentativePicoContainer(new String[] {
            "foo=bar", "foo2=12", "foo3=true", "foo4="
        });
        assertEquals("bar",apc.getComponent("foo"));
        assertEquals(12,apc.getComponent("foo2"));
        assertEquals(true,apc.getComponent("foo3"));
        assertEquals(true,apc.getComponent("foo4"));
    }

    public void testAsParentContainer() {
        ArgumentativePicoContainer apc = new ArgumentativePicoContainer(new String[] {
            "a=aaa", "b=bbb", "d=22"});
        assertEquals("aaa",apc.getComponent("a"));
        assertEquals("bbb",apc.getComponent("b"));
        assertEquals(22,apc.getComponent("d"));

        DefaultPicoContainer dpc = new DefaultPicoContainer(apc);
        dpc.addComponent(NeedsString.class);
        assertEquals("bbb", dpc.getComponent(NeedsString.class).val);
    }

    public static class NeedsString {
        public String val;
        public NeedsString(String b) {
            val = b;
        }
    }

    public void testParsingWithDiffSeparator() {
        ArgumentativePicoContainer apc = new ArgumentativePicoContainer(":", new String[] {
            "foo:bar", "foo2:12", "foo3:true"
        });
        assertEquals("bar",apc.getComponent("foo"));
        assertEquals(12,apc.getComponent("foo2"));
        assertEquals(true,apc.getComponent("foo3"));
    }

    public void testParsingWithWrongSeparator() {
        ArgumentativePicoContainer apc = new ArgumentativePicoContainer(":", new String[] {
            "foo=bar", "foo2=12", "foo3=true"
        });
        assertEquals(true,apc.getComponent("foo=bar"));
        assertEquals(true,apc.getComponent("foo2=12"));
        assertEquals(true,apc.getComponent("foo3=true"));
    }

    public void testParsingOfPropertiesFile() throws IOException {
        ArgumentativePicoContainer apc = new ArgumentativePicoContainer(":",
                               new StringReader("foo:bar\nfoo2:12\nfoo3:true\n"));
        assertEquals("bar",apc.getComponent("foo"));
        assertEquals(12,apc.getComponent("foo2"));
        assertEquals(true,apc.getComponent("foo3"));
    }

    public void testParsingOfPropertiesFileAndArgs() throws IOException {
        ArgumentativePicoContainer apc = new ArgumentativePicoContainer(":",
                               new StringReader("foo:bar\nfoo2:12\n"), new String[] {"foo3:true"});
        assertEquals("bar",apc.getComponent("foo"));
        assertEquals(12,apc.getComponent("foo2"));
        assertEquals(true,apc.getComponent("foo3"));
    }

    public void testParsingOfPropertiesFileAndArgsWithClash() throws IOException {
        ArgumentativePicoContainer apc = new ArgumentativePicoContainer(":",
                               new StringReader("foo:bar\nfoo2:99\n"), new String[] {"foo2:12","foo3:true"});
        assertEquals("bar",apc.getComponent("foo"));
        assertEquals(12,apc.getComponent("foo2"));
        assertEquals(true,apc.getComponent("foo3"));
    }

    public void testbyTypeFailsEvenIfOneOfSameType() {
        ArgumentativePicoContainer apc = new ArgumentativePicoContainer(new String[] {
            "foo=bar"});
        assertEquals("bar",apc.getComponent("foo"));
        assertNull(apc.getComponent(String.class));
    }

    public void testAmbigousIfNoSuitableTyesForInjection() {
        ArgumentativePicoContainer apc = new ArgumentativePicoContainer(new String[] {"zz=zz"});
        DefaultPicoContainer pico = new DefaultPicoContainer(apc);
        pico.addComponent(NeedsAFew.class);
        try {
            Object foo = pico.getComponent(NeedsAFew.class);
            fail();
        } catch (AbstractInjector.AmbiguousComponentResolutionException e) {
            // expetced;
        }
    }
    public static class NeedsAFew {

        public NeedsAFew(String a, int b, long c, boolean d) {
        }
    }


}
