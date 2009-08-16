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
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.fail;

public class AjaxEmailSteps extends SeleniumSteps {

    private Main main;
    private String currentNode;
    private String[] lastFormValues;

    @Given("nobody is logged in")
	public void nobodyLoggedIn() {
        Main.logout(selenium);
    }

    @Given("user is viewing their Inbox")
	public void theyreInTheirInbox() {
        nobodyLoggedIn();
        logIn("Gill Bates", "1234");
        boxIsSelected("Inbox");
    }

    @When("user $userName with password $password attempts to log in")
	public void logIn(String userName, String password) {
        main = new LoginForm(selenium, runner).login(userName, password);
	}

    @When("the mail-form is filled")
	public void mailFormFilled() {
        lastFormValues = main.fillMailForm();
	}

    //the mail-form is filled

    @Then("the $box is selected")
	public void boxIsSelected(String box) {
		main.selectedBox(box);
	}

    @Then("the '$button' button is clicked")
	public void clickButton(String legend) {
		main.clickButton(legend);
	}

    @Then("main page should $beOrNotBe obscured")
	public void mainPageObscured(String beOrNotBe) {
        if ("be".equals(beOrNotBe)) {
		    main.mainPageObscured();
        } else if ("not be".equals(beOrNotBe)) {
		    main.mainPageNotObscured();
        } else {
            fail("'be' or 'not be' are your choices, not: '" + beOrNotBe +"'");
        }
	}

    @Then("a blocking mail-form should be $visibleOrGone")
	public void blockingMailFormPresent(String visibleOrGone) {
        if ("visible".equals(visibleOrGone)) {
		    main.blockingMailFormPresent();
        } else if ("gone".equals(visibleOrGone)) {
		    main.blockingMailFormNotPresent();
        } else {
            fail("'visible' or 'gone' are your choices, not: '" + visibleOrGone +"'");
        }
	}

    @Then("that form should have nothing in it")
	public void withNothingInIt() {
		String[] values = main.formFieldValues(currentNode, false);
        for (int i = 0; i < values.length; i++) {
            assertEquals("", values[i]);
        }
	}

	@Then("the Inbox should not be visible")
	public void inBoxIsNotVisible() {
		main.textIsNotVisible("Instant Millionaire");
	}

	@Then("the text \"$text\" should be visible")
	public void textIsVisible(String text) {
		main.waitFor(new Text(text));
	}

	@Then("there are $qty messages listed")
	public void numMessages(String qty) {
        qty = qty.replace("no","0");
        int ct = main.numberOfMailItemsVisible();
        if ("some".equals(qty)) {
            assertTrue(ct > 0);
        } else {
            assertEquals(qty, ct);
        }
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
