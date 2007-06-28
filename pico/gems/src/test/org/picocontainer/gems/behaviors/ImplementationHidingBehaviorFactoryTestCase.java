package org.picocontainer.gems.behaviors;


import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ComponentFactory;
import org.picocontainer.injectors.ConstructorInjectionFactory;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.behaviors.CachingBehaviorFactory;
import org.picocontainer.injectors.AdaptiveInjectionFactory;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.gems.behaviors.ImplementationHidingBehaviorFactory;
import org.picocontainer.gems.behaviors.ImplementationHidingBehavior;
import org.picocontainer.gems.adapters.ElephantProxy;
import org.picocontainer.gems.adapters.ElephantImpl;
import org.picocontainer.gems.adapters.Elephant;
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


public final class ImplementationHidingBehaviorFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {

    private final ComponentFactory implementationHidingComponentAdapterFactory = new ImplementationHidingBehaviorFactory().forThis(new AdaptiveInjectionFactory());
    private final ComponentFactory cachingBehaviorFactory = new CachingBehaviorFactory().forThis(implementationHidingComponentAdapterFactory);

    public void testComponentRegisteredWithInterfaceKeyOnlyImplementsThatInterfaceUsingStandardProxyfactory() {

        DefaultPicoContainer pico = new DefaultPicoContainer(new ImplementationHidingBehaviorFactory().forThis(new ConstructorInjectionFactory()));
        pico.addComponent(Collection.class, ArrayList.class);
        Object collection = pico.getComponent(Collection.class);
        assertTrue(collection instanceof Collection);
        assertTrue(collection instanceof List);
        assertFalse(collection instanceof ArrayList);
    }

    public void testComponentRegisteredWithOtherKeyImplementsAllInterfacesUsingStandardProxyFactory() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new ImplementationHidingBehaviorFactory().forThis(new ConstructorInjectionFactory()));
        pico.addComponent("list", ArrayList.class);
        Object collection = pico.getComponent("list");
        assertTrue(collection instanceof List);
        assertFalse(collection instanceof ArrayList);
    }

    public void testComponentRegisteredWithOtherKeyImplementsAllInterfacesUsingCGLIBProxyFactory() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new ImplementationHidingBehaviorFactory().forThis(new ConstructorInjectionFactory()));
        pico.addComponent("list", ArrayList.class);
        Object collection = pico.getComponent("list");
        assertTrue(collection instanceof Collection);
        assertTrue(collection instanceof List);
        assertFalse(collection instanceof ArrayList);
        assertTrue(collection.getClass().getSuperclass().equals(Object.class));
    }

    public void testIHCAFwithCTORandNoCaching() {
        // http://lists.codehaus.org/pipermail/picocontainer-dev/2004-January/001985.html
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new ImplementationHidingBehavior(new ConstructorInjector("l", ArrayList.class)));

        List list1 = (List) pico.getComponent("l");
        List list2 = (List) pico.getComponent("l");

        assertNotSame(list1, list2);
        assertFalse(list1 instanceof ArrayList);

        list1.add("Hello");
        assertTrue(list1.contains("Hello"));
        assertFalse(list2.contains("Hello"));
    }

    protected ComponentFactory createComponentFactory() {
        return cachingBehaviorFactory;
    }

    public void testElephantWithoutAsmProxy() throws IOException {
        assertions(new ElephantProxy(new ElephantImpl()));
    }

    public void testElephantWithAsmProxy() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new ImplementationHidingBehavior(new ConstructorInjector("l", ArrayList.class)));
        Elephant elephant = pico.addComponent(Elephant.class, ElephantImpl.class).getComponent(Elephant.class);

        assertions(elephant);

    }

    private void assertions(Elephant foo) throws IOException {
        assertEquals("onetwo", foo.objects("one", "two"));
        assertEquals("onetwo", foo.objectsArray(new String[]{"one"}, new String[]{"two"})[0]);
        assertEquals(3, foo.iint(1, 2));
        assertEquals(3, foo.llong(1, 2));
        assertEquals(6, foo.bbyte((byte) 1, (byte) 2, (byte) 3));
        assertEquals((float) 10, foo.ffloat(1, 2, 3, 4));
        assertEquals((double) 3, foo.ddouble(1, 2));
        assertEquals('c', foo.cchar('a', 'b'));
        assertEquals(3, foo.sshort((short) 1, (short) 2));
        assertEquals(true, foo.bboolean(true, true));
        assertEquals(true, foo.bbooleanArray(new boolean[]{true}, new boolean[]{true})[0]);
    }

}