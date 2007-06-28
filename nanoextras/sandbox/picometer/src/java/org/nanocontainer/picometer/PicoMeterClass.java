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

import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoMeterClass {
    private final Class clazz;
    private final URL source;
    private final List instantiations = new ArrayList();

    public PicoMeterClass(Class clazz, URL source) throws IOException {
        this.clazz = clazz;
        this.source = source;
        ClassReader reader = new ClassReader(getClassAsStream(clazz));
        reader.accept(new PicoMeterClassVisitor(new PicoMeterMethodVisitor(instantiations, this)), false);
    }

    private InputStream getClassAsStream(Class clazz) {
        return clazz.getResourceAsStream("/" + clazz.getName().replace('.', '/') + ".class");
    }

    public List getInstantiations() {
        Collections.sort(instantiations);
        return instantiations;
    }

    public LineNumberReader getSource() throws IOException {
        InputStream sourceStream = source.openStream();
        return new LineNumberReader(new InputStreamReader(sourceStream));
    }

    public Class getInspectedClass() {
        return clazz;
    }
}
