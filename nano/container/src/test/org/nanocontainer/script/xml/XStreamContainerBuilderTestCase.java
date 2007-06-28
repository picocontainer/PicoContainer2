/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/
package org.nanocontainer.script.xml;

import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.nanocontainer.testmodel.DefaultWebServerConfig;
import org.nanocontainer.testmodel.ThingThatTakesParamsInConstructor;
import org.nanocontainer.testmodel.WebServerImpl;
import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.behaviors.AbstractBehavior;

import java.io.Reader;
import java.io.StringReader;

public class XStreamContainerBuilderTestCase extends AbstractScriptedContainerBuilderTestCase {

    public void testContainerBuilding() {

        Reader script = new StringReader("" +
                "<container>" +
                "    <instance key='foo'>" +
                "    	<string>foo bar</string>" +
                "    </instance>" +
                "    <instance key='bar'>" +
                "    	<int>239</int>" +
                "    </instance>" +
                "    <instance>" +
                "    	<org.nanocontainer.testmodel.DefaultWebServerConfig>" +
                " 			<port>555</port>" +
                "    	</org.nanocontainer.testmodel.DefaultWebServerConfig>" +
                "    </instance>" +
                "	 <implementation class='org.nanocontainer.testmodel.WebServerImpl'>" +
                "		<dependency class='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
                "	 </implementation>" +
                "	 <implementation key='konstantin needs beer' class='org.nanocontainer.testmodel.ThingThatTakesParamsInConstructor'>" +
                "		<constant>" +
                "			<string>it's really late</string>" +
                "		</constant>" +
                "		<constant>" +
                "			<int>239</int>" +
                "		</constant>" +
                "	 </implementation>" +
                "</container>");

        PicoContainer pico = buildContainer(new XStreamContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        assertEquals(5, pico.getComponents().size());
        assertEquals("foo bar", pico.getComponent("foo"));
        assertEquals(239, pico.getComponent("bar"));
        assertEquals(555, pico.getComponent(DefaultWebServerConfig.class).getPort());

        assertNotNull(pico.getComponent(WebServerImpl.class));
        assertNotNull(pico.getComponent(ThingThatTakesParamsInConstructor.class));
        final Object o = pico.getComponent("konstantin needs beer");
        final ThingThatTakesParamsInConstructor o2 = pico.getComponent(ThingThatTakesParamsInConstructor.class);
        assertSame(o, o2);
        assertEquals("it's really late239", ((ThingThatTakesParamsInConstructor) pico.getComponent("konstantin needs beer")).getValue());
    }

    public void testComponentAdapterInjection() throws Throwable {
        Reader script = new StringReader("<container>" +
                "<adapter key='testAdapter'>" +
                "<instance key='firstString'>" +
                "<string>bla bla</string>" +
                "</instance>" +
                "<instance key='secondString' >" +
                "<string>glarch</string>" +
                "</instance>" +
                "<instance key='justInt'>" +
                "<int>777</int>" +
                "</instance>" +
                "<implementation key='testAdapter' class='org.nanocontainer.script.xml.TestAdapter'>" +
                "<dependency key='firstString'/>" +
                "<dependency key='justInt'/>" +
                "<dependency key='secondString'/>" +
                "</implementation>" +
                "</adapter>" +
                "</container>");

        PicoContainer pico = buildContainer(new XStreamContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        TestAdapter tca = (TestAdapter) pico.getComponentAdapter(TestAdapter.class);
        assertNotNull(tca);
    }

    public void testInstantiationOfComponentsWithInstancesOfSameComponent() throws Exception {
        Reader script = new StringReader("" +
                "<container>" +
                "  <instance key='bean1'>" +
                "	<org.nanocontainer.script.xml.TestBean>" +
                "		<foo>10</foo>" +
                "		<bar>hello1</bar>" +
                "	</org.nanocontainer.script.xml.TestBean>" +
                "  </instance>" +
                "  <instance key='bean2'>" +
                "	<org.nanocontainer.script.xml.TestBean>" +
                "		<foo>10</foo>" +
                "		<bar>hello2</bar>" +
                "	</org.nanocontainer.script.xml.TestBean>" +
                "  </instance>" +
                "  <implementation class='org.nanocontainer.script.xml.TestBeanComposer'>" +
                "		<dependency key='bean1'/>" +
                "		<dependency key='bean2'/>" +
                "  </implementation>" +
                "</container>");
        PicoContainer pico = buildContainer(new XStreamContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        assertNotNull(pico.getComponent(TestBeanComposer.class));
        TestBeanComposer composer = pico.getComponent(TestBeanComposer.class);
        assertEquals("bean1", "hello1", composer.getBean1().getBar());
        assertEquals("bean2", "hello2", composer.getBean2().getBar());
    }
    
    // do not know how to extract parameters off adapter....
    public void testThatDependencyUsesClassAsKey() {
        Reader script = new StringReader("" +
        "<container>" +                                          
        "   <implementation class='java.lang.String'/>" +
        "   <implementation key='foo' class='org.nanocontainer.script.xml.TestBean'>" +
        "       <dependency class='java.lang.String'/>" +
        "   </implementation>" + 
        "</container>"
        );
        
       PicoContainer pico = buildContainer(new XStreamContainerBuilder(script, getClass().getClassLoader()), null,null);
        ComponentAdapter componentAdapter = pico.getComponentAdapter("foo");
        AbstractBehavior adapter = (AbstractBehavior) componentAdapter;
       assertNotNull(adapter);
    }
    
    
    public void testDefaultContsructorRegistration() throws Exception {
        
        Reader script = new StringReader(
        "<container>" + 
        "   <implementation class='org.nanocontainer.script.xml.TestBean' constructor='default'/>" +
        "   <instance>" + 
        "       <string>blurge</string>" + 
        "   </instance>" + 
        "</container>"
         );  
        
        
        PicoContainer pico = buildContainer(new XStreamContainerBuilder(script, getClass().getClassLoader()), null,null);
        TestBean bean = pico.getComponent(TestBean.class);
        assertEquals("default",bean.getConstructorCalled());
    }
}

