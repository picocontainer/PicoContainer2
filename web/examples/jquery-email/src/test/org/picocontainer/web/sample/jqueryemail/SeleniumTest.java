package org.picocontainer.web.sample.jqueryemail;

import com.thoughtworks.selenium.DefaultSelenium;
import static junit.framework.Assert.assertEquals;import static junit.framework.Assert.assertTrue;
import org.junit.Test;

public class SeleniumTest {

    protected DefaultSelenium createSeleniumClient(String url) throws Exception {
        return new DefaultSelenium("localhost", 4444, "*firefox", url);
    }

    @Test
    public void dummy() {
        assertTrue(true);
    }

    //@Test
    public void testSomethingSimple() throws Exception {
        DefaultSelenium selenium = createSeleniumClient("http://localhost:8080/");

        selenium.start();

        selenium.open("/remoting-jqueryemail-webapp/index.html");

        Thread.sleep(1000 * 1000);

        assertEquals("Geronimo Console", selenium.getTitle());

        selenium.stop();
    }


}
