import junit.framework.Test;
import org.atinject.tck.Tck;
import org.atinject.tck.auto.Car;

public class MyTck {
    public static Test suite() {
        Car car = null; // new MyInjector().getInstance(Car.class);
        return Tck.testsFor(car, true, true);
    }
}