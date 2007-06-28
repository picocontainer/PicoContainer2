package org.picocontainer.doc.tutorial.interfaces;

// START SNIPPET: girl

public final class Girl {
    final Kissable kissable;

    public Girl(Kissable kissable) {
        this.kissable = kissable;
    }

    public void kissSomeone() {
        kissable.kiss(this);
    }
}

// END SNIPPET: girl
