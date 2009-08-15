package org.picocontainer.web.sample.ajaxemail.scenarios.pages;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.condition.ConditionRunner;

public class Main extends BasePage {

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


}
