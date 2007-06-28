/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package org.nanocontainer.script.groovy;

import groovy.util.NodeBuilder;
import org.picocontainer.MutablePicoContainer;

import java.util.Map;

/**
 * @author Paul Hammant
 * @version $Revision: 3144 $
 */
public final class TestingChildBuilder extends NodeBuilder {

    final MutablePicoContainer toOperateOn;

    public TestingChildBuilder(MutablePicoContainer toOperateOn) {
        this.toOperateOn = toOperateOn;
    }

    protected Object createNode(Object name, Map map) {
        if (name.equals("component")) {
            return toOperateOn.addComponent(map.remove("key"), map.remove("class"));
        } else {
            return null;
        }
    }

}
