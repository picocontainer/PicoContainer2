package org.picocontainer.gems.injectors;

import com.thoughtworks.xstream.XStream;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import org.junit.Test;
import org.picocontainer.DefaultPicoContainer;
import org.slf4j.Logger;

public class Slf4jInjectorTestCase {

    public static class Foo {
        private Logger logger;
        public Foo(final Logger logger) {
            this.logger = logger;
        }
    }

    @Test
    public void thatItInjectsTheApplicableInstance() throws NoSuchFieldException, IllegalAccessException {

        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new Slf4JInjector());
        pico.addComponent(Foo.class);

        Foo foo = pico.getComponent(Foo.class);

        assertNotNull(foo);
        assertNotNull(foo.logger);

        XStream xStream = new XStream();
        xStream.addPermission(NoTypePermission.NONE); //forbid everything
        xStream.addPermission(NullPermission.NULL);   // allow "null"
        xStream.addPermission(PrimitiveTypePermission.PRIMITIVES); // allow primitive types
        xStream.addPermission(AnyTypePermission.ANY);
        assertTrue(xStream.toXML(foo.logger).contains("<name>"+Foo.class.getName()+"</name>"));
        

    }



}