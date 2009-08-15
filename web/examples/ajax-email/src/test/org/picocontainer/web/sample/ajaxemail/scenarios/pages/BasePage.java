package org.picocontainer.web.sample.ajaxemail.scenarios.pages;

import com.thoughtworks.selenium.condition.Condition;
import com.thoughtworks.selenium.condition.ConditionRunner;

import java.util.concurrent.TimeUnit;

public class BasePage {

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


    public static void waitFor(ConditionRunner runner, Condition condition) {
		runner.waitFor(condition);
		waitFor(1);
	}


}
