package org.nanocontainer.remoting.rmi.testmodel;

import java.io.Serializable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class Thing {
    private boolean dunit;
    private Thang thang;

    // needed for serialization of the serializable subclass
    protected Thing() {
    }

    public Thing(Thang thang) {
        this.thang = thang;
    }

    public boolean didIt() {
        return dunit;
    }

    public void doIt(Serializable s) {
        dunit = true;
    }

    public Thang getThang() {
        return thang;
    }
}