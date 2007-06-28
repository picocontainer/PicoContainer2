package org.picocontainer.testmodel;

/**
 *
 * @author greg
 * @author $Author: $ (last edit)
 * @version $Revision: $
 */
public final class CoupleBean {
    private final PersonBean personA;
    private final PersonBean personB;

    public CoupleBean(PersonBean a, PersonBean b) {
        this.personA = a;
        this.personB = b;
    }

    public PersonBean getPersonA() {
        return personA;
    }

    public PersonBean getPersonB() {
        return personB;
    }
}
