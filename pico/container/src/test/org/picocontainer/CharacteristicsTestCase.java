package org.picocontainer;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class CharacteristicsTestCase  {

    @Test(expected=UnsupportedOperationException.class)    
    public void testCharacteristicsAreImmutable() {
        assertNotNull(Characteristics.CDI.toString());
        Characteristics.CDI.remove("injection");
    }

}
