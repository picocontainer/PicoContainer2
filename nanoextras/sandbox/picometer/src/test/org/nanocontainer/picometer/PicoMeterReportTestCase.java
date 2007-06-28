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

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoMeterReportTestCase extends AbstractPicoMeterTestCase {
    public void testSourceIsHighlighted() throws IOException {
        PicoMeterClass instantiatesThree = new PicoMeterClass(PicoMeterClassTestCase.InstantiatesThree.class, source);
        PicoMeterReport picoMeterReport = new PicoMeterReport();
        picoMeterReport.writeReport(instantiatesThree, new PrintWriter(System.out));
    }

}
