package org.picocontainer.doc.tutorial.simple;

// START SNIPPET: girl

public final class Girl {
    final Boy boy;

    public Girl(Boy boy) {
        this.boy = boy;
    }

    public void kissSomeone() {
        boy.kiss(this);
    }
}

// END SNIPPET: girl
