package org.picocontainer.doc.hotswapping;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
// START SNIPPET: class
public class Wife implements Woman {
    public final Man man;

    public Wife(Man man) {
        this.man = man;
    }

    public Man getMan() {
        return man;
    }
}

// END SNIPPET: class
