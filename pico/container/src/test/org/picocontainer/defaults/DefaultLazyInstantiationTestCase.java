/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.tck.AbstractLazyInstantiationTestCase;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultLazyInstantiationTestCase extends AbstractLazyInstantiationTestCase {
    protected MutablePicoContainer createPicoContainer() {
        return new DefaultPicoContainer();
    }
}
