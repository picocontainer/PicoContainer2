import junit.framework.Test;
import org.atinject.tck.Tck;
import org.atinject.tck.auto.Car;
import org.atinject.tck.auto.Convertible;
import org.atinject.tck.auto.DriversSeat;
import org.atinject.tck.auto.Engine;
import org.atinject.tck.auto.FuelTank;
import org.atinject.tck.auto.Seat;
import org.atinject.tck.auto.Tire;
import org.atinject.tck.auto.V8Engine;
import org.atinject.tck.auto.accessories.Cupholder;
import org.atinject.tck.auto.accessories.SpareTire;

import javax.inject.Provider;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StoneageSimulationJsr330Tests {
    public static Test suite() throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException, NoSuchFieldException {
        Constructor<?> seatCtor = Seat.class.getDeclaredConstructors()[0];
        Constructor<?> driversSeatCtor = DriversSeat.class.getDeclaredConstructors()[0];
        seatCtor.setAccessible(true);

        final Seat[] plainSeat = new Seat[1];

        Provider<Seat> plainSeatProvider = new Provider<Seat>() {
            public Seat get() {
                return plainSeat[0];
            }
        };

        Cupholder cupholder = new Cupholder(plainSeatProvider);
        plainSeat[0] = (Seat) seatCtor.newInstance(cupholder);
        final DriversSeat driversSeat = (DriversSeat) driversSeatCtor.newInstance(cupholder);

        FuelTank fuelTank = new FuelTank();

        final Tire plainTire = new Tire(fuelTank);

        final SpareTire spareTire = new SpareTire(fuelTank, new FuelTank());

        Method subtypeMethodInjection = SpareTire.class.getDeclaredMethod("subtypeMethodInjection", FuelTank.class);
        subtypeMethodInjection.setAccessible(true);
        subtypeMethodInjection.invoke(spareTire, new FuelTank());

        Field fieldInjection = SpareTire.class.getDeclaredField("fieldInjection");
        fieldInjection.setAccessible(true);
        fieldInjection.set(spareTire, new FuelTank());


        Provider<Seat> driversSeatProvider = new Provider<Seat>() {
            public Seat get() {
                return driversSeat;
            }
        };

        Provider<Tire> plainTireProvider = new Provider<Tire>() {
            public Tire get() {
                return plainTire;
            }
        };

        Provider<Tire> spareTireProvider = new Provider<Tire>() {
            public Tire get() {
                return spareTire;
            }
        };

        Provider<Engine> engineProvider = new Provider<Engine>() {
            public Engine get() {
                return new V8Engine();
            }
        };

        Constructor<?> convertibelCtor = null;
        Constructor<?>[] convertibelCtors = Convertible.class.getDeclaredConstructors();
        for (int i = 0; i < convertibelCtors.length; i++) {
            convertibelCtor = convertibelCtors[i];
            if (convertibelCtor.getParameterTypes().length > 0) {
                break;
            }
        }
        convertibelCtor.setAccessible(true);

        System.out.println("l" + convertibelCtor.getParameterTypes().length);
        Car car = (Car) convertibelCtor.newInstance(
                plainSeat[0],
                driversSeat,
                plainTire,
                spareTire,
                plainSeatProvider,
                driversSeatProvider,
                plainTireProvider,
                engineProvider);


        Field driversSeatAFld = Convertible.class.getDeclaredField("driversSeatA");
        driversSeatAFld.setAccessible(true);
        driversSeatAFld.set(car, driversSeat);

        Field driversSeatBFld = Convertible.class.getDeclaredField("driversSeatB");
        driversSeatBFld.setAccessible(true);
        driversSeatBFld.set(car, driversSeat);

        Field spareTireFld = Convertible.class.getDeclaredField("spareTire");
        spareTireFld.setAccessible(true);
        spareTireFld.set(car, spareTire);

        Field cupholderFld = Convertible.class.getDeclaredField("cupholder");
        cupholderFld.setAccessible(true);
        cupholderFld.set(car, cupholder);

        Field engineProviderFld = Convertible.class.getDeclaredField("engineProvider");
        engineProviderFld.setAccessible(true);
        engineProviderFld.set(car, engineProvider);

        Field fieldPlainSeatFld = Convertible.class.getDeclaredField("fieldPlainSeat");
        fieldPlainSeatFld.setAccessible(true);
        fieldPlainSeatFld.set(car, plainSeat[0]);

        Field fieldDriversSeatFld = Convertible.class.getDeclaredField("fieldDriversSeat");
        fieldDriversSeatFld.setAccessible(true);
        fieldDriversSeatFld.set(car, driversSeat);

        Field fieldPlainTire = Convertible.class.getDeclaredField("fieldPlainTire");
        fieldPlainTire.setAccessible(true);
        fieldPlainTire.set(car, plainTire);

        Field fieldSpareTire = Convertible.class.getDeclaredField("fieldSpareTire");
        fieldSpareTire.setAccessible(true);
        fieldSpareTire.set(car, spareTire);

        Field fieldPlainSeatProvider = Convertible.class.getDeclaredField("fieldPlainSeatProvider");
        fieldPlainSeatProvider.setAccessible(true);
        fieldPlainSeatProvider.set(car, plainSeatProvider);

        Field fieldDriversSeatProvider = Convertible.class.getDeclaredField("fieldDriversSeatProvider");
        fieldDriversSeatProvider.setAccessible(true);
        fieldDriversSeatProvider.set(car, driversSeatProvider);

        Field fieldPlainTireProvider = Convertible.class.getDeclaredField("fieldPlainTireProvider");
        fieldPlainTireProvider.setAccessible(true);
        fieldPlainTireProvider.set(car, plainTireProvider);

        Field fieldSpareTireProvider = Convertible.class.getDeclaredField("fieldSpareTireProvider");
        fieldSpareTireProvider.setAccessible(true);
        fieldSpareTireProvider.set(car, spareTireProvider);

        Method injectMethodWithZeroArgs = Convertible.class.getDeclaredMethod("injectMethodWithZeroArgs");
        injectMethodWithZeroArgs.setAccessible(true);
        injectMethodWithZeroArgs.invoke(car);

        Method injectMethodWithNonVoidReturn = Convertible.class.getDeclaredMethod("injectMethodWithNonVoidReturn");
        injectMethodWithNonVoidReturn.setAccessible(true);
        injectMethodWithNonVoidReturn.invoke(car);

        Method injectInstanceMethodWithManyArgs = Convertible.class.getDeclaredMethod("injectInstanceMethodWithManyArgs",
                Seat.class, Seat.class, Tire.class, Tire.class, Provider.class, Provider.class, Provider.class, Provider.class);
        injectInstanceMethodWithManyArgs.setAccessible(true);
        injectInstanceMethodWithManyArgs.invoke(car, plainSeat[0], driversSeat, plainTire,
                spareTire, plainSeatProvider, driversSeatProvider, plainTireProvider, spareTireProvider);

        return Tck.testsFor(car, true, true);
    }
}