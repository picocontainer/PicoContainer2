package org.picocontainer.web.sample.ajaxemail.scenarios.steps;

import org.jbehave.scenario.annotations.Given;
import org.jbehave.scenario.annotations.Then;
import org.jbehave.scenario.annotations.When;
import org.jbehave.web.selenium.SeleniumSteps;
import org.picocontainer.web.sample.ajaxemail.scenarios.pages.LoginForm;
import org.picocontainer.web.sample.ajaxemail.scenarios.pages.Main;

import com.thoughtworks.selenium.condition.Condition;
import com.thoughtworks.selenium.condition.Not;
import com.thoughtworks.selenium.condition.Text;

public class JQueryEmailSteps extends SeleniumSteps {

    private Main main;

    @Given("nobody is logged in")
	public void nobodyLoggedIn() {
        Main.logout(selenium);
    }

    @When("user $userName with password $password attempts to log in")
	public void logIn(String userName, String password) {
        main = new LoginForm(selenium, runner).login(userName, password);
	}

    @Then("the $box is selected")
	public void boxIsSelected(String box) {
		main.selectedBox(box);
	}

	@Then("the Inbox should not be visible")
	public void inBoxIsNotVisible() {
		main.textIsNotVisible("Instant Millionaire");
	}

	@Then("the text \"$text\" should be visible")
	public void textIsVisible(String text) {
		main.waitFor(new Text(text));
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
