/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.picometer;

import java.util.Comparator;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class InstantiationCountSorter implements Comparator {
    public int compare(Object o1, Object o2) {
        PicoMeterClass c1 = (PicoMeterClass) o1;
        PicoMeterClass c2 = (PicoMeterClass) o2;
        return c1.getInstantiations().size() - c2.getInstantiations().size();
    }
}
