package org.nanocontainer.nanowar.nanoweb;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class MyAction {
    private int year;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String doit() {
        if (year > 2003) {
            return "success";
        } else {
            return "error";
        }
    }
}