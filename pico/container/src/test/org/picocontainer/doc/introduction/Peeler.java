package org.picocontainer.doc.introduction;

import org.picocontainer.Startable;


// START SNIPPET: class

public class Peeler implements Startable {
    private final Peelable peelable;

    public Peeler(Peelable peelable) {
        this.peelable = peelable;
    }

    public void start() {
        peelable.peel();
    }

    public void stop() {

    }
}

// END SNIPPET: class
