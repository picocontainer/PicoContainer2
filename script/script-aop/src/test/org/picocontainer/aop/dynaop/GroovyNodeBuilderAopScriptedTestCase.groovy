/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.picocontainer.script.groovy

import org.picocontainer.aop.*
import org.picocontainer.aop.dynaop.*
import org.picocontainer.script.groovy.*
import org.picocontainer.script.NanoContainerMarkupException

public class GroovyNodeBuilderAopScriptedTestCase extends GroovyTestCase {

    def builder = new DynaopGroovyNodeBuilder()
    def cuts = new DynaopPointcutsFactory()

    public void testComponentScopedMixin() {
        def scripted = builder.container() {
            component(key:Dao, class:DaoImpl) {
                aspect(mixinClass:IdentifiableMixin)
            }
        }
        def dao = scripted.pico.getComponentInstance(Dao)
        verifyMixin(dao)
    }

    public void testContainerSuppliedMixin() {
        def scripted = builder.container() {
            component(key:'order1', class:OrderEntityImpl)
            component(key:'order2', class:OrderEntityImpl)
            component(key:IdGenerator, class:IdGeneratorImpl)
            aspect(classCut:cuts.instancesOf(OrderEntity), mixinClass:IdentifiableMixin)
        }

        def order1 = scripted.pico.getComponentInstance('order1')
        def order2 = scripted.pico.getComponentInstance('order2')

        assertTrue(order1 instanceof Identifiable)
        assertTrue(order2 instanceof Identifiable)
        assertEquals(new Integer(1), order1.id)
        assertEquals(new Integer(2), order2.id)

        order1.id = new Integer(42)
        assertEquals(new Integer(42), order1.id)
        assertEquals(new Integer(2), order2.id)
    }

    public void testContainerScopedMixinExplicitInterfaces() {
        def scripted = builder.container() {
            component(key:Dao, class:DaoImpl)
            aspect(classCut:cuts.instancesOf(Dao), mixinInterfaces:[ Identifiable ], mixinClass:IdentifiableMixin)
        }
        def dao = scripted.pico.getComponentInstance(Dao)
        verifyMixin(dao)
        assertFalse(dao instanceof AnotherInterface)
    }

    public void testComponentScopedMixinExplicitInterfaces() {
        def scripted = builder.container() {
            component(key:Dao, class:DaoImpl) {
                aspect(mixinClass:IdentifiableMixin, mixinInterfaces:[ Identifiable ])
            }
        }
        def dao = scripted.pico.getComponentInstance(Dao)
        verifyMixin(dao)
        assertFalse(dao instanceof AnotherInterface)
    }

    public void testMissingRequiredComponentArguments() {
        shouldFail(NanoContainerMarkupException, {
            builder.container() {
                component(value:'whoops')
            }
        })
    }

    public void testClassCutOrComponentCutRequiredForInterceptor() {
        shouldFail(NanoContainerMarkupException, {
            builder.container() {
                aspect(interceptorKey:'whoops')
            }
        })
    }

    public void testMethodCutRequiredForInterceptor() {
        shouldFail(NanoContainerMarkupException, {
            builder.container() {
                aspect(classCut:cuts.instancesOf(Dao), interceptorKey:'whoops')
            }
        })
    }

    public void testClassCutOrComponentCutRequiredForMixin() {
        shouldFail(NanoContainerMarkupException, {
            builder.container() {
                aspect(mixinClass:Dao)
            }
        })
    }

    public void testNoAdviceSpecifiedInAspect() {
        shouldFail(NanoContainerMarkupException, {
            builder.container() {
                aspect(classCut:cuts.instancesOf(Dao))
            }
        })
    }

    public void testComponentInstance() {
        // Note:  aspecting of instances is not supported, but we just want to make sure we didn't mess anything up.
        def scripted = builder.container() {
            component(key:'foo', instance:'bar')
        }
        assertEquals('bar', scripted.pico.getComponentInstance('foo'))
    }

    public void testBean() {
        // Note:  aspecting of beanClass instantiated beans isn't supported either, but again we just want to make sure we didn't mess anything up.
        def scripted = builder.container() {
            bean(beanClass:StringBean, firstName:'tom', lastName:'jones')
        }
        def stringBean = scripted.pico.getComponentInstance(StringBean)
        assertNotNull(stringBean)
        assertEquals('tom', stringBean.firstName)
        assertEquals('jones', stringBean.lastName)
    }

    public void testExample() {
        // START SNIPPET: example
        def log = new StringBuffer()
        def logger = new LoggingInterceptor(log)
        def builder = new DynaopGroovyNodeBuilder()
        def cuts = new DynaopPointcutsFactory()

        def scripted = builder.container() {
            component(key:Dao, class:DaoImpl) {
                aspect(methodCut:cuts.allMethods(), interceptor:logger)
            }
            component(key:CustomerEntity, class:CustomerEntityImpl)
            component(key:OrderEntity, class:OrderEntityImpl)

            aspect(classCut:cuts.instancesOf(Entity), mixinClass:IdentifiableMixin)
            aspect(classCut:cuts.packageName('org.picocontainer.aop'), methodCut:cuts.signature('save*'), interceptor:logger)
        }

        def dao = scripted.pico.getComponentInstance(Dao)
        def customer = scripted.pico.getComponentInstance(CustomerEntity)
        def order = scripted.pico.getComponentInstance(OrderEntity)
        // END SNIPPET: example

        verifyIntercepted(dao, log)
        verifyMixin(customer)
        verifyMixin(order)

        def before = log.toString()
        customer.saveMe()
        assertEquals(before + 'startend', log.toString())

        before = log.toString()
        order.saveMeToo()
        assertEquals(before + 'startend', log.toString())
    }

    void verifyIntercepted(dao, log) {
        def before = log.toString()
        def data = dao.loadData()
        assertEquals('data', data)
        assertEquals(before + 'startend', log.toString())
    }

    void verifyNotIntercepted(dao, log) {
        def before = log.toString()
        def data = dao.loadData()
        assertEquals('data', data)
        assertEquals(before, log.toString())
    }

    void verifyMixin(Object component) {
        assertTrue(component instanceof Identifiable)
        component.setId("id")
        assertEquals("id", component.getId())
    }

    void verifyNoMixin(Object component) {
        assertFalse(component instanceof Identifiable)
    }

}
