package org.picocontainer.converters;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.picocontainer.ConverterSet;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.containers.CompositePicoContainer;
import org.picocontainer.containers.EmptyPicoContainer;

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
            public ConverterSet getConverter() {
                return new BuiltInConverter() {
                    @Override
                    protected void addBuiltInConverters() {
                        addConverter(new MyConverter(), Boolean.class);

                    }
                };
            }
        };

        //Verify use of MyConverter instead of usual BooleanConverter
        int oldInvocationCount = MyConverter.invocationCount;
        dpc.getConverter().convert("true", Boolean.class);
        assertEquals(oldInvocationCount + 1, MyConverter.invocationCount);
    }

    @Test
    public void parentContainerSuppliesByDefault() {
        PicoContainer parent = new DefaultPicoContainer() {
            @Override
            public ConverterSet getConverter() {
                return new BuiltInConverter() {
                    @Override
                    protected void addBuiltInConverters() {
                        addConverter(new MyConverter(), Boolean.class);

                    }
                };
            }
        };
        DefaultPicoContainer dpc = new DefaultPicoContainer(parent);
        //Verify use of MyConverter instead of usual
        int oldInvocationCount = MyConverter.invocationCount;
        dpc.getConverter().convert("true", Boolean.class);
        assertEquals(oldInvocationCount + 1, MyConverter.invocationCount);
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
            public ConverterSet getConverter() {
                return new BuiltInConverter() {
                    @Override
                    protected void addBuiltInConverters() {
                        addConverter(new BooleanConverter(), Boolean.class);

                    }
                };
            }
        };
        PicoContainer two = new DefaultPicoContainer() {
            @Override
            public ConverterSet getConverter() {
                return new BuiltInConverter() {
                    @Override
                    protected void addBuiltInConverters() {
                        addConverter(new ShortConverter(), Short.class);

                    }
                };
            }
        };
        CompositePicoContainer compositePC = new CompositePicoContainer(one, two);
        ConverterSet converter = compositePC.getConverter();
        assertFalse(converter.canConvert(Character.class));
        assertTrue(converter.canConvert(Short.class));
        assertTrue(converter.canConvert(Boolean.class));
        assertEquals(null, converter.convert("a", Character.class));
        assertEquals((short)12, converter.convert("12", Short.class));
        assertEquals(Boolean.TRUE, converter.convert("TRUE", Boolean.class));

    }


    public static class MyConverter extends BooleanConverter {
        public static int invocationCount = 0;

        /**
         * {@inheritDoc}
         * @see org.picocontainer.converters.BooleanConverter#convert(java.lang.String)
         */
        @Override
        public Boolean convert(String paramValue) {
            invocationCount++;
            return super.convert(paramValue);
        }        
    }

}
