package org.picocontainer.web.sample.jqueryemail.scenarios;

import org.jbehave.scenario.annotations.Given;
import org.jbehave.scenario.annotations.Then;
import org.jbehave.scenario.annotations.When;
import org.jbehave.web.selenium.SeleniumSteps;

import com.thoughtworks.selenium.condition.Condition;
import com.thoughtworks.selenium.condition.Not;
import com.thoughtworks.selenium.condition.Presence;
import com.thoughtworks.selenium.condition.Text;

public class JQueryEmailSteps extends SeleniumSteps {

	@Given("nobody is logged in")
	public void nobodyLoggedIn() {
		selenium.open("/remoting-jqueryemail-webapp/");
		try {
			selenium.click("link=Log Out");
		} catch (Exception e) {
		}
    }

    @When("user $userName with password $password attempts to log in")
	public void logIn(String userName, String password) {
		waitFor(new Presence("id=userName"));
		selenium.type("id=userName", userName);
		selenium.type("id=password", password);
		selenium.click("id=submitLogin");
	}

	@Then("the Inbox should be visible")
	public void inBoxIsVisible() {
		textIsVisible("Instant Millionaire");
	}

	@Then("the Inbox should not be visible")
	public void inBoxIsNotVisible() {
		textIsNotVisible("Instant Millionaire");
	}

	@Then("the text \"$text\" should be visible")
	public void textIsVisible(String text) {
		waitFor(new Text(text));
	}

	@Then("the text \"$text\" should not be visible")
	public void textIsNotVisible(String text) {
		waitFor(new Not(new Text(text)));
	}

	private void waitFor(Condition condition) {
		runner.waitFor(condition);
		waitFor(1);
	}

}
