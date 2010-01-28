package org.picocontainer.converters;

import org.junit.Test;
import org.picocontainer.Converting;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.containers.CompositePicoContainer;
import org.picocontainer.containers.EmptyPicoContainer;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ConverterTestCase {
    
    @Test
    public void builtInConversionByDefault() {
        DefaultPicoContainer dpc = new DefaultPicoContainer();
        assertTrue(dpc.getConverter() instanceof BuiltInConverter);
    }

    @Test
    public void canOverrideConverter() {
        DefaultPicoContainer dpc = new DefaultPicoContainer() {
            @Override
            public Converting.Converter getConverter() {
                return new MyConverter();
            }
        };
        assertTrue(dpc.getConverter() instanceof MyConverter);
    }

    @Test
    public void parentContainerSuppliesByDefault() {
        PicoContainer parent = new DefaultPicoContainer() {
            @Override
            public Converting.Converter getConverter() {
                return new MyConverter();
            }
        };
        DefaultPicoContainer dpc = new DefaultPicoContainer(parent);
        assertTrue(dpc.getConverter() instanceof MyConverter);
    }

    @Test
    public void parentContainerDoesNotSuppliesByDefaultIfItIsNotAConversion() {
        PicoContainer parent = new EmptyPicoContainer();
        DefaultPicoContainer dpc = new DefaultPicoContainer(parent);
        assertTrue(dpc.getConverter() instanceof BuiltInConverter);
    }

    @Test
    public void compositesPossible() {
        PicoContainer one = new DefaultPicoContainer() {
            @Override
            public Converting.Converter getConverter() {
                return new BooleanConverter();
            }
        };
        PicoContainer two = new DefaultPicoContainer() {
            @Override
            public Converting.Converter getConverter() {
                return new ShortConverter();
            }
        };
        CompositePicoContainer compositePC = new CompositePicoContainer(one, two);
        Converting.Converter converter = compositePC.getConverter();
        assertFalse(converter.canConvert(Character.class));
        assertTrue(converter.canConvert(Short.class));
        assertTrue(converter.canConvert(Boolean.class));
        assertEquals(null, converter.convert("a", Character.class));
        assertEquals((short)12, converter.convert("12", Short.class));
        assertEquals(Boolean.TRUE, converter.convert("TRUE", Boolean.class));

    }


    public static class MyConverter extends BooleanConverter {
    }

}
