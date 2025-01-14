/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.script.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.security.*;

/**
 * Implementation of XMLComponentInstanceFactory that uses XStream to unmarshal
 * DOM elements in PureJava mode. In PureJava mode objects are instantiated
 * using standard Java reflection, which is garanteed to be valid for all JVM
 * vendors, but the types of objects that can be constructed are limited. See
 * XStream's <a href="http://xstream.codehaus.org/faq.html">FAQ</a> for details
 * on the differences between PureJava and Advanced mode.
 * 
 * @author Mauro Talevi
 */
public class PureJavaXStreamComponentInstanceFactory extends XStreamComponentInstanceFactory {
    /**
     * Creates a PureJavaXStreamComponentInstanceFactory using an instance of
     * XStream in PureJava mode.
     */
    public PureJavaXStreamComponentInstanceFactory(Class clazz) {
        super(makeXStream(clazz));
    }
    static XStream makeXStream(Class clazz) {
        XStream xs = new XStream(new PureJavaReflectionProvider());
        xs.addPermission(NoTypePermission.NONE); //forbid everything
        xs.addPermission(NullPermission.NULL);   // allow "null"
        xs.addPermission(PrimitiveTypePermission.PRIMITIVES); // allow primitive types
        xs.addPermission(AnyTypePermission.ANY);
        return xs;
    }

}
