
/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class MyGroovyAction {

    int year

    public doit() {
        if(year > 2003) {            
            return "success";
        } else {
            return "error";
        }
    }
}