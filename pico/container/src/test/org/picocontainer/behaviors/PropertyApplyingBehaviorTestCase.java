package org.picocontainer.behaviors;

import junit.framework.TestCase;
import org.picocontainer.testmodel.CoupleBean;
import org.picocontainer.behaviors.PropertyApplyingBehavior;

/**
 *
 * @author greg
 * @author $Author: $ (last edit)
 * @version $Revision: $ 
 */
public class PropertyApplyingBehaviorTestCase extends TestCase {
    public void testBeanPropertyComponentAdapterCanUsePropertyEditors() {
        Object c = PropertyApplyingBehavior.convert(CoupleBean.class.getName(), "a's name:Camilla;b's name:Charles;", this.getClass().getClassLoader());
        assertNotNull(c);
        assertTrue(c instanceof CoupleBean);
        assertEquals("Camilla", ((CoupleBean) c).getPersonA().getName());
        assertEquals("Charles", ((CoupleBean) c).getPersonB().getName());
    }

}
