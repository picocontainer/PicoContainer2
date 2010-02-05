import junit.framework.Test;
import org.atinject.tck.Tck;
import org.atinject.tck.auto.Car;
import org.atinject.tck.auto.Convertible;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;

public class MyTck {
    public static Test suite() {
        MutablePicoContainer dpc = new DefaultPicoContainer().addComponent(Car.class, Convertible.class);
        Car car = dpc.getComponent(Car.class);
        return Tck.testsFor(car, true, true);
    }
}