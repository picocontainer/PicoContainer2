/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan                                           *
 *****************************************************************************/

package org.nanocontainer.script.groovy.buildernodes;

import java.util.Map;

import org.nanocontainer.NanoContainer;
import org.nanocontainer.script.ClassPathElementHelper;

import org.nanocontainer.ClassPathElement;

/**
 * @author James Strachan
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Michael Rimov
 * @author Mauro Talevi
 * @version $Revision: 2695 $
 */
public class ClasspathNode extends AbstractBuilderNode {

    public static final String NODE_NAME = "classPathElement";


    private static final String PATH = "path";


    public ClasspathNode() {
        super(NODE_NAME);

        addAttribute(PATH);
    }


    public Object createNewNode(Object current, Map attributes) {
        return createClassPathElementNode(attributes, (NanoContainer) current);
    }

    private ClassPathElement createClassPathElementNode(Map attributes, NanoContainer nanoContainer) {

        final String path = (String) attributes.remove(PATH);
        return ClassPathElementHelper.addClassPathElement(path, nanoContainer);
    }

}
