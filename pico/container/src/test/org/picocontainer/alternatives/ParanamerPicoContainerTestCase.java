package org.picocontainer.alternatives;

import junit.framework.TestCase;

import com.thoughtworks.paranamer.DefaultParanamer;
import com.thoughtworks.paranamer.Paranamer;

public class ParanamerPicoContainerTestCase extends TestCase {

    public void testCanInstantiateParanamer(){
        Paranamer paranamer = new DefaultParanamer();
        assertNotNull(paranamer);
    }
    
}
