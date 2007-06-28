package org.picocontainer.doc.tutorial.simple2;

import org.picocontainer.doc.tutorial.interfaces.Kissable;

// START SNIPPET: boy

public class Boy implements Kissable {
    public void kiss(Object kisser) {
        System.out.println("I was kissed by " + kisser);
    }
}

// END SNIPPET: boy
