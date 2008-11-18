package org.picocontainer.web.sample.jqueryemail;

import com.thoughtworks.selenium.DefaultSelenium;
import static junit.framework.Assert.assertEquals;import static junit.framework.Assert.assertTrue;
import org.junit.Test;

public class SeleniumTest {

    protected DefaultSelenium createSeleniumClient(String url) throws Exception {
        return new DefaultSelenium("localhost", 4444, "*firefox", url);
    }
    
    @Test
    public void applicationOpensToCorrectStartPage() throws Exception {
        DefaultSelenium selenium = createSeleniumClient("http://localhost:8080/");

        selenium.start();

        selenium.open("/remoting-jqueryemail-webapp/index.html");

        assertEquals("Pico Web Remoting and jQuery Message Sample App", selenium.getTitle());

        selenium.stop();
    }


}
