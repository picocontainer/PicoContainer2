package org.picocontainer.web.sample.ajaxemail.scenarios.pages;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.condition.ConditionRunner;
import com.thoughtworks.selenium.condition.Presence;

public class Main extends BasePage {
    private String box;

    public Main(Selenium selenium, ConditionRunner runner) {
        super(selenium, runner);
    }

    public static void logout(Selenium selenium) {
        selenium.open("/remoting-ajaxemail-webapp/");
        try {
            selenium.click("link=Log Out");
        } catch (RuntimeException e) {
        }
    }


    public void selectedBox(String box) {
        waitFor(new Presence("id('"+box+"')[@class=\"mailbox_selected\"]"));
        this.box = box;
    }
}
