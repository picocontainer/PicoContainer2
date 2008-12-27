package org.picocontainer.web.sample.jqueryemail;

import com.thoughtworks.selenium.DefaultSelenium;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import org.junit.Test;

public class SeleniumTest {

    @Test
    public void applicationOpensToCorrectStartPage() throws Exception {

        DefaultSelenium selenium = new DefaultSelenium("localhost", 4444,
                "*firefox", "http://localhost:8080/");

        selenium.start();

        selenium.open("/remoting-jqueryemail-webapp/index.html");

        assertEquals("Pico Web Remoting and jQuery Message Sample App", selenium.getTitle());

        //assertTrue(selenium.isTextPresent("Login Please"));

        selenium.type("userName", "Gil Bates");
        selenium.type("password", "1234");
        selenium.click("submitLogin");

        // unfortunately, the version of Selenium linked to the Maven plugin does
        // mot contain the new waitFor/condition stuff.
        Thread.sleep(1 * 1000);

        assertTrue(selenium.isTextPresent("Nice Example")); // from first message

        selenium.stop();
    }


}
