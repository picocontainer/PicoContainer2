package org.picocontainer.web.sample.ajaxemail.scenarios.pages;

import com.thoughtworks.selenium.condition.Condition;
import com.thoughtworks.selenium.condition.ConditionRunner;
import com.thoughtworks.selenium.condition.Text;
import com.thoughtworks.selenium.condition.Not;
import com.thoughtworks.selenium.Selenium;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class Page {

	protected final Selenium selenium;
	protected final ConditionRunner runner;

	public Page(Selenium selenium, ConditionRunner runner) {
		this.selenium = selenium;
		this.runner = runner;
	}

	/**
	 * Waits for a number of seconds
	 * 
	 * @param seconds
	 *            the number of seconds to sleep
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

    public String[] formFieldValues(String node, boolean fillEm) {
        String fieldsXpath = node + "//*[@class=\"textfield\"]";
        int fields = selenium.getXpathCount(fieldsXpath).intValue() ;
        String[] values = new String[fields];
        for (int field = 1; field <= fields; field++) {
            String locator = "xpath=(" + fieldsXpath + ")[" + field + "]";
            if (fillEm) {
                selenium.type(locator, "Test:" + Math.random());
            }
            values[field-1] = selenium.getText(locator);
        }
        return values;
    }


}
