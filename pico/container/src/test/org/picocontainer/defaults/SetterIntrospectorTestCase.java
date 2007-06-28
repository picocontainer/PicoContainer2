package org.picocontainer.defaults;

import junit.framework.TestCase;

import java.beans.IntrospectionException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class SetterIntrospectorTestCase extends TestCase {
    public static class TestBean {
        public void setPublicMethod(int i) {
        }

        public void setPublicMETHODAgain(int i) {
        }

        public void setMOOky(int i) {
        }

        public void setFOOBAR(int i) {
        }

        public void set(int i) {
        }

        public void sets(int i) {
        }

        public void fooBar(int i) {
        }

        public void setX(int i) {
        }

        public static void setStaticMethod(int i) {
        }

        public static void setMany() {
        }

        protected void setProtectedMethod(int i) {
        }

        private void setPrivateMethod(int i) {
        }
    }

    public void testShouldConvertPropertyNamesInSameWayAsBeanInfo() throws IntrospectionException {

// TODO - to test via SetterInjectionComponentAdaptor with mock/expects.

//        BeanInfo beanInfo = Introspector.getBeanInfo(TestBean.class);
//        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
//
//        Map setters = getSetters(TestBean.class);
//        assertEquals(propertyDescriptors.length, setters.size());
//
//        for (int i = 0; i < propertyDescriptors.length; i++) {
//            PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
//            String expectedPropertyName = propertyDescriptor.getName();
//            assertEquals("No property found for " + expectedPropertyName, propertyDescriptor.getWriteMethod(), setters.get(expectedPropertyName));
//        }
    }

}
