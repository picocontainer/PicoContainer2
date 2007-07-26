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

import org.nanocontainer.aop.AbstractAopTestCase;
import org.nanocontainer.aop.AspectablePicoContainer;
import org.nanocontainer.aop.AspectablePicoContainerFactory;
import org.nanocontainer.aop.LoggingInterceptor;
import org.nanocontainer.aop.PointcutsFactory;
import org.nanocontainer.testmodel.AnotherInterface;
import org.nanocontainer.testmodel.Dao;
import org.nanocontainer.testmodel.DaoImpl;
import org.nanocontainer.testmodel.IdGenerator;
import org.nanocontainer.testmodel.IdGeneratorImpl;
import org.nanocontainer.testmodel.Identifiable;
import org.nanocontainer.testmodel.IdentifiableMixin;
import org.nanocontainer.testmodel.OrderEntity;
import org.nanocontainer.testmodel.OrderEntityImpl;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.testmodel.SimpleTouchable;

/**
 * @author Stephen Molitor
 */
public final class DynaopAspectablePicoContainerFactoryTestCase extends AbstractAopTestCase {

    private final AspectablePicoContainerFactory containerFactory = new DynaopAspectablePicoContainerFactory();
    private final AspectablePicoContainer pico = containerFactory.createContainer();
    private final PointcutsFactory cuts = pico.getPointcutsFactory();

    public void testInterceptor() {
        StringBuffer log = new StringBuffer();
        pico.registerInterceptor(cuts.instancesOf(Dao.class), cuts.allMethods(), new LoggingInterceptor(log));
        pico.addComponent(Dao.class, DaoImpl.class);
        Dao dao = pico.getComponent(Dao.class);
        verifyIntercepted(dao, log);
    }

    public void testContainerSuppliedInterceptor() {
        pico.registerInterceptor(cuts.instancesOf(Dao.class), cuts.allMethods(), LoggingInterceptor.class);

        pico.addComponent("log", StringBuffer.class);
        pico.addComponent(LoggingInterceptor.class);
        pico.addComponent(Dao.class, DaoImpl.class);

        Dao dao = pico.getComponent(Dao.class);
        StringBuffer log = (StringBuffer) pico.getComponent("log");
        verifyIntercepted(dao, log);
    }

    public void testComponentInterceptor() {
        StringBuffer log = new StringBuffer();

        pico.registerInterceptor(cuts.component("intercepted"), cuts.allMethods(), new LoggingInterceptor(log));
        pico.addComponent("intercepted", DaoImpl.class);
        pico.addComponent("notIntercepted", DaoImpl.class);

        Dao intercepted = (Dao) pico.getComponent("intercepted");
        Dao notIntercepted = (Dao) pico.getComponent("notIntercepted");

        verifyIntercepted(intercepted, log);
        verifyNotIntercepted(notIntercepted, log);
    }

    public void testContainerSuppliedComponentInterceptor() {
        pico.registerInterceptor(cuts.component("intercepted"), cuts.allMethods(), LoggingInterceptor.class);

        pico.addComponent("log", StringBuffer.class);
        pico.addComponent(LoggingInterceptor.class);
        pico.addComponent("intercepted", DaoImpl.class);
        pico.addComponent("notIntercepted", DaoImpl.class);

        StringBuffer log = (StringBuffer) pico.getComponent("log");
        Dao intercepted = (Dao) pico.getComponent("intercepted");
        Dao notIntercepted = (Dao) pico.getComponent("notIntercepted");

        verifyIntercepted(intercepted, log);
        verifyNotIntercepted(notIntercepted, log);
    }

    public void testMixin() {
        pico.registerMixin(cuts.instancesOf(Dao.class), IdentifiableMixin.class);
        pico.addComponent(Dao.class, DaoImpl.class);
        Dao dao = pico.getComponent(Dao.class);
        verifyMixin(dao);
        assertTrue(dao instanceof AnotherInterface);
    }

    public void testContainerSuppliedMixin() {
        pico.addComponent(IdGenerator.class, IdGeneratorImpl.class);
        pico.addComponent("order1", OrderEntityImpl.class);
        pico.addComponent("order2", OrderEntityImpl.class);
        pico.registerMixin(cuts.instancesOf(OrderEntity.class), new Class[]{Identifiable.class},
                IdentifiableMixin.class);

        Identifiable i1 = (Identifiable) pico.getComponent("order1");
        Identifiable i2 = (Identifiable) pico.getComponent("order2");

        assertEquals(1, i1.getId());
        assertEquals(2, i2.getId());

        i1.setId(3);
        assertEquals(3, i1.getId());
        assertEquals(2, i2.getId());
    }

    public void testComponentMixin() {
        pico.addComponent("hasMixin", DaoImpl.class);
        pico.addComponent("noMixin", DaoImpl.class);

        pico.registerMixin(cuts.component("hasMixin"), IdentifiableMixin.class);

        Dao hasMixin = (Dao) pico.getComponent("hasMixin");
        Dao noMixin = (Dao) pico.getComponent("noMixin");

        verifyMixin(hasMixin);
        verifyNoMixin(noMixin);
        assertTrue(hasMixin instanceof AnotherInterface);
    }

    public void testContainerSuppliedComponentMixin() {
        pico.addComponent(IdGenerator.class, IdGeneratorImpl.class);
        pico.registerMixin(cuts.componentName("hasMixin*"), new Class[]{Identifiable.class}, IdentifiableMixin.class);
        pico.addComponent("hasMixin1", OrderEntityImpl.class);
        pico.addComponent("hasMixin2", OrderEntityImpl.class);
        pico.addComponent("noMixin", OrderEntityImpl.class);

        OrderEntity hasMixin1 = (OrderEntity) pico.getComponent("hasMixin1");
        OrderEntity hasMixin2 = (OrderEntity) pico.getComponent("hasMixin2");
        OrderEntity noMixin = (OrderEntity) pico.getComponent("noMixin");

        assertTrue(hasMixin1 instanceof Identifiable);
        assertTrue(hasMixin2 instanceof Identifiable);
        assertFalse(noMixin instanceof Identifiable);

        assertEquals(1, ((Identifiable) hasMixin1).getId());
        assertEquals(2, ((Identifiable) hasMixin2).getId());
    }

    public void testMixinExplicitInterfaces() {
        pico.registerMixin(cuts.instancesOf(Dao.class), new Class[]{Identifiable.class}, IdentifiableMixin.class);
        pico.addComponent(Dao.class, DaoImpl.class);
        Dao dao = pico.getComponent(Dao.class);
        verifyMixin(dao);
        assertFalse(dao instanceof AnotherInterface);
    }

    public void testComponentMixinExplicitInterfaces() {
        pico.addComponent("hasMixin", DaoImpl.class);
        pico.addComponent("noMixin", DaoImpl.class);

        pico.registerMixin(cuts.component("hasMixin"), new Class[]{Identifiable.class}, IdentifiableMixin.class);

        Dao hasMixin = (Dao) pico.getComponent("hasMixin");
        Dao noMixin = (Dao) pico.getComponent("noMixin");

        verifyMixin(hasMixin);
        verifyNoMixin(noMixin);

        assertFalse(hasMixin instanceof AnotherInterface);
    }

    public void testCreateWithParentContainer() {
        MutablePicoContainer parent = new DefaultPicoContainer();
        parent.addComponent("key", "value");
        AspectablePicoContainerFactory containerFactory = new DynaopAspectablePicoContainerFactory();
        PicoContainer child = containerFactory.createContainer(parent);
        assertEquals("value", child.getComponent("key"));
    }
    
    public void testMakeChildContainer(){
        AspectablePicoContainerFactory aspectableContainerFactory = new DynaopAspectablePicoContainerFactory();
        AspectablePicoContainer parent = aspectableContainerFactory.createContainer();
        parent.addComponent("t1", SimpleTouchable.class);
        AspectablePicoContainer child = aspectableContainerFactory.makeChildContainer(parent);
        Object t1 = child.getParent().getComponent("t1");
        assertNotNull(t1);
        assertTrue(t1 instanceof SimpleTouchable);        
    }

    public void testInterfacesWithClassPointcut() {
        pico.addComponent(Dao.class, DaoImpl.class);
        pico.registerMixin(cuts.instancesOf(Dao.class), IdentifiableMixin.class);
        pico.registerInterfaces(cuts.instancesOf(Dao.class), new Class[]{AnotherInterface.class});
        Dao dao = pico.getComponent(Dao.class);
        assertTrue(dao instanceof Identifiable);
        assertTrue(dao instanceof AnotherInterface);
    }

    public void testInterfacesWithClassPointcutNoAdviceStillSetsUp() {
        pico.addComponent(Dao.class, DaoImpl.class);
        pico.registerInterfaces(cuts.instancesOf(Dao.class), new Class[]{AnotherInterface.class});
        Dao dao = pico.getComponent(Dao.class);

        // dynaop doesn't add any interfaces if there's no advice applied to the
        // object:
        assertFalse(dao instanceof Identifiable);
        assertTrue(dao instanceof AnotherInterface);
    }

    public void testInterfacesWithComponentPointcut() {
        pico.addComponent(Dao.class, DaoImpl.class);
        pico.registerMixin(cuts.component(Dao.class), IdentifiableMixin.class);
        pico.registerInterfaces(cuts.component(Dao.class), new Class[]{AnotherInterface.class});
        Dao dao = pico.getComponent(Dao.class);
        assertTrue(dao instanceof Identifiable);
        assertTrue(dao instanceof AnotherInterface);
    }

}