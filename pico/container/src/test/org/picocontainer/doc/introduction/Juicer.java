package org.picocontainer.doc.introduction;

// START SNIPPET: class

public class Juicer {
    private final Peelable peelable;
    private final Peeler peeler;

    public Juicer(Peelable peelable, Peeler peeler) {
        this.peelable = peelable;
        this.peeler = peeler;
    }
}

// END SNIPPET: class
