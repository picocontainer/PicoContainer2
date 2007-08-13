/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.script.groovy;

import java.io.StringReader;

import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.nanocontainer.testmodel.Dao;
import org.nanocontainer.testmodel.Identifiable;
import org.picocontainer.PicoContainer;

/**
 * @author Stephen Molitor
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 */
public class GroovyNodeBuilderAopTestCase extends AbstractScriptedContainerBuilderTestCase {

    public void testContainerScopedInterceptor() {
        String script = "" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "import org.nanocontainer.aop.dynaop.*\n" +
                "" +
                "log = new StringBuffer()\n" +
                "logger = new LoggingInterceptor(log)\n" +
                "\n" +
                "cuts = new DynaopPointcutsFactory()\n" +
                "builder = new DynaopGroovyNodeBuilder()\n" +
                "nano = builder.container() {\n" +
                "    aspect(classCut:cuts.instancesOf(Dao.class), methodCut:cuts.allMethods(), interceptor:logger)\n" +
                "    component(key:Dao, class:DaoImpl)\n" +
                "    component(key:StringBuffer, instance:log)\n" +
                "}\n";

        PicoContainer pico = buildContainer(script);
        Dao dao = pico.getComponent(Dao.class);
        StringBuffer log = pico.getComponent(StringBuffer.class);
        verifyIntercepted(dao, log);
    }

    public void testContainerScopedPointcutWithNestedAdvices() {
        String script = "" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "import org.nanocontainer.aop.dynaop.*\n" +
                "" +
                "log = new StringBuffer()\n" +
                "logger = new LoggingInterceptor(log)\n" +
                "\n" +
                "cuts = new DynaopPointcutsFactory()\n" +
                "builder = new DynaopGroovyNodeBuilder()\n" +
                "nano = builder.container() {\n" +
                "    pointcut(classCut:cuts.instancesOf(Dao.class), methodCut:cuts.allMethods()) {\n" +
                "        aspect(interceptor:logger)\n" +
                "    }\n" +
                "    component(key:Dao, class:DaoImpl)\n" +
                "    component(key:StringBuffer, instance:log)\n" +
                "}\n";

        PicoContainer pico = buildContainer(script);
        Dao dao = pico.getComponent(Dao.class);
        StringBuffer log = pico.getComponent(StringBuffer.class);
        verifyIntercepted(dao, log);
    }

    public void testContainerScopedContainerSuppliedInterceptor() {
        String script = "" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "import org.nanocontainer.aop.dynaop.*\n" +
                "cuts = new DynaopPointcutsFactory()\n" +
                "builder = new DynaopGroovyNodeBuilder()\n" +
                "nano = builder.container() {\n" +
                "    aspect(classCut:cuts.instancesOf(Dao), methodCut:cuts.allMethods(), interceptorKey:LoggingInterceptor)\n" +
                "    component(key:'log', class:StringBuffer)\n" +
                "    component(LoggingInterceptor)\n" +
                "    component(key:Dao, class:DaoImpl)\n" +
                "}\n";

        PicoContainer pico = buildContainer(script);
        Dao dao = pico.getComponent(Dao.class);
        StringBuffer log = (StringBuffer) pico.getComponent("log");
        verifyIntercepted(dao, log);
    }

    public void testComponentScopedInterceptor() {
        String script = "" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "import org.nanocontainer.aop.dynaop.*\n" +
                "log = new StringBuffer()\n" +
                "logger = new LoggingInterceptor(log)\n" +
                "\n" +
                "cuts = new DynaopPointcutsFactory()\n" +
                "builder = new DynaopGroovyNodeBuilder()\n" +
                "nano = builder.container() {\n" +
                "    component(key:'intercepted', class:DaoImpl) {\n" +
                "        aspect(methodCut:cuts.allMethods(), interceptor:logger)\n" +
                "    }\n" +
                "    component(key:'log', instance:log)\n" +
                "    component(key:'notIntercepted', class:DaoImpl)\n" +
                "}\n";

        PicoContainer pico = buildContainer(script);
        Dao intercepted = (Dao) pico.getComponent("intercepted");
        Dao notIntercepted = (Dao) pico.getComponent("notIntercepted");
        StringBuffer log = (StringBuffer) pico.getComponent("log");

        verifyIntercepted(intercepted, log);
        verifyNotIntercepted(notIntercepted, log);
    }

    public void testContainerScopedMixin() {
        String script = "" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "import org.nanocontainer.aop.dynaop.*\n" +
                "cuts = new DynaopPointcutsFactory()\n" +
                "builder = new DynaopGroovyNodeBuilder()\n" +
                "nano = builder.container() {\n" +
                "    component(key:Dao, class:DaoImpl) \n" +
                "    aspect(classCut:cuts.instancesOf(Dao), mixinClass:IdentifiableMixin)\n" +
                "}";

        PicoContainer pico = buildContainer(script);
        Dao dao = pico.getComponent(Dao.class);
        verifyMixin(dao);
    }

    public void testExplicitAspectsManagerAndDecorationDelegate() {
        String script = "" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "import org.nanocontainer.aop.dynaop.*\n" +
                "aspectsManager = new org.nanocontainer.aop.dynaop.DynaopAspectsManager()\n" +
                "cuts = aspectsManager.getPointcutsFactory()\n" +
                "decorator = new org.nanocontainer.aop.defaults.AopNodeBuilderDecorationDelegate(aspectsManager)\n" +
                "builder = new GroovyNodeBuilder(decorator) \n" +
                "nano = builder.container() {\n" +
                "    component(key:Dao, class:DaoImpl) \n" +
                "    aspect(classCut:cuts.instancesOf(Dao), mixinClass:IdentifiableMixin)\n" +
                "}";

        PicoContainer pico = buildContainer(script);
        Dao dao = pico.getComponent(Dao.class);
        verifyMixin(dao);
    }

    public void testCustomComponentFactory() {
        String script = "" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "import org.nanocontainer.aop.dynaop.*\n" +
                "intLog = new StringBuffer()\n" +
                "logger = new LoggingInterceptor(intLog)\n" +
                "componentFactoryLog = new StringBuffer()\n" +
                "componentFactory = new TestInjection(componentFactoryLog)\n" +
                "cuts = new DynaopPointcutsFactory()\n" +
                "builder = new DynaopGroovyNodeBuilder()\n" +
                "nano = builder.container(componentFactory:componentFactory) {\n" +
                "    aspect(classCut:cuts.instancesOf(Dao.class), methodCut:cuts.allMethods(), interceptor:logger)\n" +
                "    component(key:Dao, class:DaoImpl)\n" +
                "    component(key:'intLog', instance:intLog)\n" +
                "    component(key:'componentFactoryLog', instance:componentFactoryLog)\n" +
                "}";

        PicoContainer pico = buildContainer(script);
        Dao dao = pico.getComponent(Dao.class);
        StringBuffer intLog = (StringBuffer) pico.getComponent("intLog");
        verifyIntercepted(dao, intLog);
        StringBuffer componentFactoryLog = (StringBuffer) pico.getComponent("componentFactoryLog");
        assertEquals("called", componentFactoryLog.toString());
    }

    private PicoContainer buildContainer(String script) {
        GroovyContainerBuilder builder = new GroovyContainerBuilder(new StringReader(script), getClass().getClassLoader());
        return buildContainer(builder, null, "SOME_SCOPE");
    }

    private void verifyIntercepted(Dao dao, StringBuffer log) {
        String before = log.toString();
        String data = dao.loadData();
        assertEquals("data", data);
        assertEquals(before + "startend", log.toString());
    }

    private void verifyNotIntercepted(Dao dao, StringBuffer log) {
        String before = log.toString();
        String data = dao.loadData();
        assertEquals("data", data);
        assertEquals(before, log.toString());
    }

    private void verifyMixin(Object component) {
        assertTrue(component instanceof Identifiable);
        Identifiable identifiable = (Identifiable) component;
        identifiable.setId("id");
        assertEquals("id", identifiable.getId());
    }

}