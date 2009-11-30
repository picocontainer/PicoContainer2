package org.picocontainer.foo;

import org.junit.Test;
import org.picocontainer.Characteristics;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.Emjection;
import org.picocontainer.MutablePicoContainer;

import static junit.framework.Assert.assertEquals;

public class EmjectionTestCase {

    @Test
    public void basicEmjection() {

        StringBuilder sb = new StringBuilder();
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(Antelope.class);
        pico.as(Characteristics.EMJECTION_ENABLED).addComponent(ZooKeeper.class);
        pico.addComponent(sb);
        ZooKeeper zooKeeper = pico.getComponent(ZooKeeper.class);
        zooKeeper.doHeadCount();

        assertEquals("giraffe=true, antelope=true", sb.toString());

    }


    public static class Zoo {
        private final Emjection emjection = new Emjection();

        private Giraffe giraffe;
        private Antelope antelope;
        private StringBuilder sb;

        public Zoo(Giraffe giraffe, Antelope antelope, StringBuilder sb) {
            this.giraffe = giraffe;
            this.antelope = antelope;
            this.sb = sb;
        }

        public void headCount() {
            sb.append("giraffe=").append(giraffe != null);
            sb.append(", antelope=").append(antelope != null);
        }
    }


    public static class ZooKeeper {

        private final Emjection emjection = new Emjection();

        public void doHeadCount() {
            Zoo zoo = neu(Zoo.class, this, new Giraffe());
            zoo.headCount();
        }

        <T> T neu(Class<T> type, Object... args) {
            return Emjection.neu(type, emjection, args);
        }

    }

    public static class Giraffe {
    }
    public static class Antelope {
    }


}
