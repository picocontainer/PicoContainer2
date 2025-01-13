package org.picocontainer.gems.injectors;

import com.thoughtworks.xstream.XStream;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import org.apache.commons.logging.Log;
import org.junit.Test;
import org.picocontainer.DefaultPicoContainer;

public class CommonsLoggingInjectorTestCase {

    public static class Foo {
        private Log log;
        public Foo(final Log log) {
            this.log = log;
        }
    }


    @Test
    public void thatItInjectsTheApplicableInstance() {

        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new CommonsLoggingInjector());
        pico.addComponent(Foo.class);
        
        Foo foo = pico.getComponent(Foo.class);

        assertNotNull(foo);
        assertNotNull(foo.log);

        XStream xStream = new XStream();
        xStream.addPermission(NoTypePermission.NONE); //forbid everything
        xStream.addPermission(NullPermission.NULL);   // allow "null"
        xStream.addPermission(PrimitiveTypePermission.PRIMITIVES); // allow primitive types
        xStream.addPermission(AnyTypePermission.ANY);
        assertTrue(xStream.toXML(foo.log).contains("<name>"+Foo.class.getName()+"</name>"));


    }



}
