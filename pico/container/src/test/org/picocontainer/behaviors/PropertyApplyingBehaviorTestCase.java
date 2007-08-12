/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.behaviors;

import junit.framework.TestCase;
import org.picocontainer.testmodel.CoupleBean;
import org.picocontainer.behaviors.PropertyApplicator;

/**
 *
 * @author greg
 */
public class PropertyApplyingBehaviorTestCase extends TestCase {
    public void testBeanPropertyComponentAdapterCanUsePropertyEditors() {
        Object c = PropertyApplicator.convert(CoupleBean.class.getName(), "a's name:Camilla;b's name:Charles;", this.getClass().getClassLoader());
        assertNotNull(c);
        assertTrue(c instanceof CoupleBean);
        assertEquals("Camilla", ((CoupleBean) c).getPersonA().getName());
        assertEquals("Charles", ((CoupleBean) c).getPersonB().getName());
    }

}
