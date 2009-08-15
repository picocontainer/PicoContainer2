package org.picocontainer.web.sample.ajaxemail.scenarios.pages;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.condition.ConditionRunner;
import com.thoughtworks.selenium.condition.Presence;
import com.thoughtworks.selenium.condition.Condition;


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


    public void selectedBox(final String box) {
        
        waitFor(new Condition(box + " selected"){
            @Override
            public boolean isTrue(ConditionRunner.Context context) {
                return box.equals(context.getSelenium().getText("//span[@class=\"mailbox_selected\"]")); 
            }
        });
        this.box = box;
    }
}
