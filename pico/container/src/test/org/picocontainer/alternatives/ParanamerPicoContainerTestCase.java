/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.alternatives;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.thoughtworks.paranamer.DefaultParanamer;
import com.thoughtworks.paranamer.Paranamer;

public class ParanamerPicoContainerTestCase {

    @Test public void testCanInstantiateParanamer(){
        Paranamer paranamer = new DefaultParanamer();
        assertNotNull(paranamer);
    }
    
}
