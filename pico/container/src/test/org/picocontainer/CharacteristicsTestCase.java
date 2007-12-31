package org.picocontainer;

import junit.framework.TestCase;

import org.junit.Test;

public class CharacteristicsTestCase extends TestCase {

    @Test public void testCharacteristicsAreImmutable() {
        String b4 = Characteristics.CDI.toString();
        try {
            Characteristics.CDI.remove("injection");
            fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

}
