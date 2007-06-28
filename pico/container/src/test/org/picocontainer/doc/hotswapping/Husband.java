package org.picocontainer.doc.hotswapping;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
// START SNIPPET: class
public class Husband implements Man {
    public final Woman woman;

    public Husband(Woman woman) {
        this.woman = woman;
    }

    public int getEndurance() {
        return 10;
    }
}

// START SNIPPET: class
