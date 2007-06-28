package org.picocontainer.doc.tutorial.blocks;

import org.picocontainer.doc.introduction.Peelable;
import org.picocontainer.doc.introduction.Peeler;

// START SNIPPET: class

public class JuicerBean {
    private Peelable peelable;
    private Peeler peeler;
    protected Peelable getPeelable() {
        return this.peelable;
    }
    protected void setPeelable(Peelable peelable) {
        this.peelable = peelable;
    }
    protected void setPeeler(Peeler peeler) {
        this.peeler = peeler;
    }
    protected Peeler getPeeler() {
        return this.peeler;
    }

}

// END SNIPPET: class
