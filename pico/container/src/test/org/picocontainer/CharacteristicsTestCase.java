package org.picocontainer;

import junit.framework.TestCase;

public class CharacteristicsTestCase extends TestCase {

    public void testCharacteristicsAreImmutable() {
        String b4 = Characteristics.CDI.toString();
        try {
            Characteristics.CDI.remove("injection");
            fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

}
