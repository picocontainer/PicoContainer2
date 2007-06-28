/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.script.groovy;

import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.nanocontainer.testmodel.A;
import org.picocontainer.PicoContainer;
import org.picocontainer.DefaultPicoContainer;

import java.io.Reader;
import java.io.StringReader;

import groovy.lang.Binding;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @version $Revision$
 */
public class GroovyContainerBuilderTestCase extends AbstractScriptedContainerBuilderTestCase {

    public void testContainerCanBeBuiltWithParent() {
        Reader script = new StringReader("" +
                "builder = new org.nanocontainer.script.groovy.GroovyNodeBuilder()\n" +
                "pico = builder.container(parent:parent) { \n" +
                "  component(StringBuffer)\n" +
                "}");
        PicoContainer parent = new DefaultPicoContainer();
        PicoContainer pico = buildContainer(new GroovyContainerBuilder(script, getClass().getClassLoader()), parent, "SOME_SCOPE");
        //PicoContainer.getParent() is now ImmutablePicoContainer
        assertNotSame(parent, pico.getParent());
        assertEquals(StringBuffer.class, pico.getComponent(StringBuffer.class).getClass());
    }

        public void testAdditionalBindingViaSubClassing() {
                Reader script = new StringReader("" +
                "builder = new org.nanocontainer.script.groovy.GroovyNodeBuilder()\n" +
                "pico = builder.container(parent:parent) { \n" +
                "  component(key:String.class, instance:foo)\n" +
                "}");

        PicoContainer parent = new DefaultPicoContainer();
        PicoContainer pico = buildContainer(new SubclassGroovyContainerBuilder(script, getClass().getClassLoader()), parent, "SOME_SCOPE");

                assertNotSame(parent, pico.getParent());
        assertEquals("bar", pico.getComponent(String.class));
        }

    public void testBuildingWithDefaultBuilder() {
        // NOTE script does NOT define a "builder"
        Reader script = new StringReader("" +
                "pico = builder.container(parent:parent) { \n" +
                "  component(key:String.class, instance:'foo')\n" +
                "}");

        PicoContainer parent = new DefaultPicoContainer();
        PicoContainer pico = buildContainer(new GroovyContainerBuilder(script, getClass().getClassLoader()), parent, "SOME_SCOPE");

        assertNotSame(parent, pico.getParent());
        assertEquals("foo", pico.getComponent(String.class));
    }

    public void testBuildingWithAppendingNodes() {
        Reader script = new StringReader("" +
                "pico = builder.container(parent:parent) { \n" +

                "}\n" +
                "\n" +
                "builder.append(container:pico) {" +
                "  component(key:String.class, instance:'foo')\n" +
                "}\n" +
                "");

        PicoContainer parent = new DefaultPicoContainer();
        PicoContainer pico = buildContainer(new GroovyContainerBuilder(script, getClass().getClassLoader()), parent, "SOME_SCOPE");

        assertNotSame(parent, pico.getParent());
        assertEquals("foo", pico.getComponent(String.class));
    }



    public void testBuildingWithPicoSyntax() {
        Reader script = new StringReader("" +
                "parent.addComponent('foo', java.lang.String)\n"  +
                "pico = new org.picocontainer.DefaultPicoContainer(parent)\n" +
                "pico.addComponent(org.nanocontainer.testmodel.A)\n" +
                "");

        PicoContainer parent = new DefaultPicoContainer();
        PicoContainer pico = buildContainer(new GroovyContainerBuilder(script, getClass().getClassLoader()), parent, "SOME_SCOPE");

        assertNotSame(parent, pico.getParent());
        assertNotNull(pico.getComponent(A.class));
        assertNotNull(pico.getComponent("foo"));
    }



    public void testBuildingWithPicoSyntaxAndNullParent() {
        Reader script = new StringReader("" +
                "pico = new org.picocontainer.DefaultPicoContainer(parent)\n" +
                "pico.addComponent(org.nanocontainer.testmodel.A)\n" +
                "");

        PicoContainer parent = null;
        PicoContainer pico = buildContainer(new GroovyContainerBuilder(script, getClass().getClassLoader()), parent, "SOME_SCOPE");

        assertNotSame(parent, pico.getParent());
        assertNotNull(pico.getComponent(A.class));
    }

    /**
         * Child SubclassGroovyContainerBuilder which adds additional bindings
         */
        private class SubclassGroovyContainerBuilder extends GroovyContainerBuilder {
                public SubclassGroovyContainerBuilder(final Reader script, ClassLoader classLoader) {
                        super(script, classLoader);
                }

                protected void handleBinding(Binding binding) {
                        super.handleBinding(binding);

                        binding.setVariable("foo", "bar");
                }

        }

}
