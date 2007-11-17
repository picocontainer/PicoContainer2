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
import org.picocontainer.Characteristics;
import org.picocontainer.annotations.Inject;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.injectors.SetterInjection;
import org.picocontainer.injectors.AnnotatedFieldInjection;

import java.io.StringReader;
import java.io.IOException;

import junit.framework.TestCase;

public class CommandLineArgumentsPicoContainerTestCase extends TestCase {

    public void testBasicParsing() {
        CommandLineArgumentsPicoContainer apc = new CommandLineArgumentsPicoContainer(new String[] {
            "foo=bar", "foo2=12", "foo3=true", "foo4="
        });
        assertEquals("bar",apc.getComponent("foo"));
        assertEquals("12",apc.getComponent("foo2"));
        assertEquals("true",apc.getComponent("foo3"));
        assertEquals("true",apc.getComponent("foo4"));
    }

    public void testAsParentContainer() {
        CommandLineArgumentsPicoContainer apc = new CommandLineArgumentsPicoContainer(new String[] {
            "a=aaa", "b=bbb", "d=22"});
        assertEquals("aaa",apc.getComponent("a"));
        assertEquals("bbb",apc.getComponent("b"));
        assertEquals("22",apc.getComponent("d"));

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
        CommandLineArgumentsPicoContainer apc = new CommandLineArgumentsPicoContainer(":", new String[] {
            "foo:bar", "foo2:12", "foo3:true"
        });
        assertEquals("bar",apc.getComponent("foo"));
        assertEquals("12",apc.getComponent("foo2"));
        assertEquals("true",apc.getComponent("foo3"));
    }

    public void testParsingWithWrongSeparator() {
        CommandLineArgumentsPicoContainer apc = new CommandLineArgumentsPicoContainer(":", new String[] {
            "foo=bar", "foo2=12", "foo3=true"
        });
        assertEquals("true",apc.getComponent("foo=bar"));
        assertEquals("true",apc.getComponent("foo2=12"));
        assertEquals("true",apc.getComponent("foo3=true"));
    }

    public void testParsingOfPropertiesFile() throws IOException {
        CommandLineArgumentsPicoContainer apc = new CommandLineArgumentsPicoContainer(":",
                               new StringReader("foo:bar\nfoo2:12\nfoo3:true\n"));
        assertEquals("bar",apc.getComponent("foo"));
        assertEquals("12",apc.getComponent("foo2"));
        assertEquals("true",apc.getComponent("foo3"));
    }

    public void testParsingOfPropertiesFileAndArgs() throws IOException {
        CommandLineArgumentsPicoContainer apc = new CommandLineArgumentsPicoContainer(":",
                               new StringReader("foo:bar\nfoo2:12\n"), new String[] {"foo3:true"});
        assertEquals("bar",apc.getComponent("foo"));
        assertEquals("12",apc.getComponent("foo2"));
        assertEquals("true",apc.getComponent("foo3"));
    }

    public void testParsingOfPropertiesFileAndArgsWithClash() throws IOException {
        CommandLineArgumentsPicoContainer apc = new CommandLineArgumentsPicoContainer(":",
                               new StringReader("foo:bar\nfoo2:99\n"), new String[] {"foo2:12","foo3:true"});
        assertEquals("bar",apc.getComponent("foo"));
        assertEquals("12",apc.getComponent("foo2"));
        assertEquals("true",apc.getComponent("foo3"));
    }

    public void testbyTypeFailsEvenIfOneOfSameType() {
        CommandLineArgumentsPicoContainer apc = new CommandLineArgumentsPicoContainer(new String[] {
            "foo=bar"});
        assertEquals("bar",apc.getComponent("foo"));
        assertNull(apc.getComponent(String.class));
    }

    public void testUnsatisfiableIfNoSuitableTyesForInjection() {
        CommandLineArgumentsPicoContainer apc = new CommandLineArgumentsPicoContainer(new String[] {"zz=zz"});
        DefaultPicoContainer pico = new DefaultPicoContainer(apc);
        pico.as(Characteristics.USE_NAMES).addComponent(NeedsAFew.class);
        try {
            Object foo = pico.getComponent(NeedsAFew.class);
            fail();
        } catch (AbstractInjector.UnsatisfiableDependenciesException e) {
            // expetced;
        }
    }
    public static class NeedsAFew {
        private final String a;
        private final int b;
        private final boolean c;
        public NeedsAFew(String a, int b, boolean c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }

    public void testConstructorInjectionComponentCanDependOnConfig() {
        CommandLineArgumentsPicoContainer apc = new CommandLineArgumentsPicoContainer(new String[] {"a=a", "b=2", "c=true"});
        DefaultPicoContainer pico = new DefaultPicoContainer(apc);
        pico.addConfig("zzz","zzz");
        pico.as(Characteristics.USE_NAMES).addComponent(NeedsAFew.class);
        NeedsAFew needsAFew = pico.getComponent(NeedsAFew.class);
        assertNotNull(needsAFew);
        assertEquals("a", needsAFew.a);
        assertEquals(2, needsAFew.b);
        assertEquals(true, needsAFew.c);
    }

    public static class NeedsAFew2 {
        private String a;
        private int b;
        private boolean c;

        public void setA(String a) {
            this.a = a;
        }

        public void setB(int b) {
            this.b = b;
        }

        public void setC(boolean c) {
            this.c = c;
        }
    }

    public void testSetterInjectionComponentCanDependOnConfig() {
        CommandLineArgumentsPicoContainer apc = new CommandLineArgumentsPicoContainer(new String[] {"a=a", "b=2", "c=true"});
        DefaultPicoContainer pico = new DefaultPicoContainer(new SetterInjection(), apc);
        pico.addConfig("zzz","zzz");
        pico.as(Characteristics.USE_NAMES).addComponent(NeedsAFew2.class);
        NeedsAFew2 needsAFew = pico.getComponent(NeedsAFew2.class);
        assertNotNull(needsAFew);
        assertEquals("a", needsAFew.a);
        assertEquals(2, needsAFew.b);
        assertEquals(true, needsAFew.c);
    }

    public static class NeedsAFew3 {
        @Inject
        private String a;
        @Inject
        private int b;
        @Inject
        private boolean c;
    }

    public void testAnnotatedFieldInjectionComponentCanDependOnConfig() {
        CommandLineArgumentsPicoContainer apc = new CommandLineArgumentsPicoContainer(new String[] {"a=a", "b=2", "c=true"});
        DefaultPicoContainer pico = new DefaultPicoContainer(new AnnotatedFieldInjection(), apc);
        pico.addConfig("zzz","zzz");
        pico.as(Characteristics.USE_NAMES).addComponent(NeedsAFew3.class);
        NeedsAFew3 needsAFew = pico.getComponent(NeedsAFew3.class);
        assertNotNull(needsAFew);
        assertEquals("a", needsAFew.a);
        assertEquals(2, needsAFew.b);
        assertEquals(true, needsAFew.c);
    }

}
