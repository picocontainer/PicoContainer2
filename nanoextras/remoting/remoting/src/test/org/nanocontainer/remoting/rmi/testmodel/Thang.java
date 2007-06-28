package org.nanocontainer.remoting.rmi.testmodel;

import java.util.List;

public class Thang {
    private boolean hasHicked;
    private List list;
    private Thing thing;

    // needed for serialization of the serializable subclass
    protected Thang() {
    }

    public Thang(List list) {
        this.list = list;
    }

    public void hickup() {
        hasHicked = true;
    }

    public boolean hasHicked() {
        return hasHicked;
    }

    public List getList() {
        return list;
    }

    public Thing getThing() {
        return thing;
    }

    public void setThing(Thing thing) {
        this.thing = thing;
    }
}