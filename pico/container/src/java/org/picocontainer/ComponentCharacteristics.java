/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer;

import java.util.Properties;
import java.io.Serializable;

public class ComponentCharacteristics implements Serializable, Cloneable {

    private Properties props = new Properties();

    protected void setProperty(String name, String val) {
        props.setProperty(name, val);
    }
    protected void removeProperty(String name) {
        props.remove(name);
    }
    

    public void mergeInto(ComponentCharacteristics characteristics) {
    }

    public boolean setAsProcessedIfSoCharacterized(ComponentCharacteristics characteristics) {
        return false;
    }

    public String getProperty(String name) {
        return props.getProperty(name);
    }

    public ComponentCharacteristics clone() {
        ComponentCharacteristics cc;
        try {
            cc = (ComponentCharacteristics) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.toString());
        }
        cc.props = (Properties) props.clone();
        return cc;
    }

    public String toString() {
        return props.toString();   
    }

    public boolean hasUnProcessedEntries() {
        return !props.isEmpty();
    }
}
