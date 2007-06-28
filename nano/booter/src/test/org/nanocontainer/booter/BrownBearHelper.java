package org.nanocontainer.booter;

import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.ClassName;

import java.util.Map;

public class BrownBearHelper {

    public BrownBearHelper() {
       DefaultNanoContainer nano = new DefaultNanoContainer();
        nano.addComponent(Map.class, new ClassName("java.util.HashMap"));
    }

}
