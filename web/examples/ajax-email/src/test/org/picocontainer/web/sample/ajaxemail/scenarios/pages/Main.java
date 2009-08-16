package org.picocontainer.web.sample.ajaxemail.scenarios.pages;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.condition.Condition;
import com.thoughtworks.selenium.condition.ConditionRunner;

public class Main extends Page {

	private String box;

	public Main(Selenium selenium, ConditionRunner runner) {
		super(selenium, runner);
	}

	public static void logout(Selenium selenium) {
		selenium.open("/ajaxemail/");
		try {
			selenium.click("link=Log Out");
		} catch (RuntimeException e) {
		}
	}

	public void selectedBox(final String box) {

		waitFor(new Condition(box + " selected") {
			@Override
			public boolean isTrue(ConditionRunner.Context context) {
				return box.equals(context.getSelenium().getText(
						"//span[@class=\"mailbox_selected\"]"));
			}
		});
		this.box = box;
	}

	public String getSelectedBox() {
		return box;
	}

	public String firstListedEmailSubject() {
		return selenium
				.getText("(//tr[contains(@class,\"messageRow\")])[1]/td[4][text()]");
	}

	public int numberOfMailItemsVisible() {
		return selenium.getXpathCount("//tr[contains(@class,\"messageRow\")]")
				.intValue();
	}

}
