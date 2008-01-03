/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.parameters;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.picocontainer.PicoCompositionException;


/**
 * test that constant parameter behaves well.
 * @author Konstantin Pribluda
 */
public class ConstantParameterTestCase {
    
    /**
    *  constant parameter with instance type shall verify for expected primitives
     * @throws Exception
     */
    @Test public void testThatInstaceTypeAcceptedForPrimitives() throws Exception {
        ConstantParameter param = new ConstantParameter(239);
        try{
            param.verify(null,null,Integer.TYPE, null, false, null);
        } catch(PicoCompositionException ex) {
            fail("failed verification for primitive / instance ");
        }
    }
}
