package org.picocontainer.web.sample.ajaxemail.scenarios.pages;

import com.thoughtworks.selenium.Selenium;

public class Main extends BasePage {

    public static void logout(Selenium selenium) {
        selenium.open("/remoting-ajaxemail-webapp/");
        try {
            selenium.click("link=Log Out");
        } catch (RuntimeException e) {
        }
    }


}
