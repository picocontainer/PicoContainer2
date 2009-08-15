package org.picocontainer.web.sample.ajaxemail.scenarios.pages;

import com.thoughtworks.selenium.condition.Condition;
import com.thoughtworks.selenium.condition.ConditionRunner;
import com.thoughtworks.selenium.condition.Text;
import com.thoughtworks.selenium.condition.Not;
import com.thoughtworks.selenium.Selenium;

import java.util.concurrent.TimeUnit;

public class BasePage {

    protected final Selenium selenium;
    protected final ConditionRunner runner;

    public BasePage(Selenium selenium, ConditionRunner runner) {
        this.selenium = selenium;
        this.runner = runner;
    }


    /**
     * Waits for a number of seconds
     *
     * @param seconds the number of seconds to sleep
     */
    protected static void waitFor(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            // continue
        }
    }


    public void waitFor(Condition condition) {
		runner.waitFor(condition);
		waitFor(1);
	}


    public void textIsVisible(String text) {
        waitFor(new Text(text));
    }

    public void textIsNotVisible(String text) {
        waitFor(new Not(new Text(text)));
    }


}
