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
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoMeterReport {
    public PicoMeterReport() {
    }

    public void writeReport(PicoMeterClass picoMeterClass, PrintWriter printWriter) throws IOException {
        printWriter.print("<HTML><HEAD><TITLE>");
        printWriter.print(picoMeterClass.getInspectedClass().getName());
        printWriter.print("</TITLE><LINK REL =\"stylesheet\" TYPE=\"text/css\" HREF=\"style.css\" TITLE=\"Style\"></HEAD>");
        List instantiations = picoMeterClass.getInstantiations();
        Map lineNumberToInstantiationMap = createMap(instantiations);

        LineNumberReader reader = picoMeterClass.getSource();
        String line = null;
        while ((line = reader.readLine()) != null) {
            Instantiation instantiation = (Instantiation) lineNumberToInstantiationMap.get(new Integer(reader.getLineNumber()));
            String code;
            if (instantiation != null) {
                String before = line.substring(0, instantiation.getStartColumn());
                String instantiationString = line.substring(instantiation.getStartColumn(), instantiation.getEndColumn());
                String after = line.substring(instantiation.getEndColumn());
                code =
                        "<PRE class=\"src\">" +
                        before +
                        "</PRE>" +
                        "<PRE class=\"instantiation\">" +
                        instantiationString +
                        "</PRE>" +
                        "<PRE class=\"src\">" +
                        after +
                        "</PRE><BR/>";
            } else {
                code =
                        "<PRE class=\"src\">" +
                        line +
                        "</PRE><BR/>";
            }
            printWriter.println(code);
        }
        printWriter.print("</HTML>");
        printWriter.flush();
    }

    private Map createMap(List instantiations) {
        Map result = new HashMap();
        for (Iterator iterator = instantiations.iterator(); iterator.hasNext();) {
            Instantiation instantiation = (Instantiation) iterator.next();
            result.put(new Integer(instantiation.getStartLine()), instantiation);
        }
        return result;
    }
}
