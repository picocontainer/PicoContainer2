package org.picocontainer.web.sample.ajaxemail.scenarios.pages;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.condition.Presence;
import com.thoughtworks.selenium.condition.ConditionRunner;

public class LoginForm extends BasePage {

    public static void login(String userName, String password, Selenium selenium, ConditionRunner runner) {
        waitFor(runner, new Presence("id=userName"));
        selenium.type("id=userName", userName);
        selenium.type("id=password", password);
        selenium.click("id=submitLogin");
    }



}
