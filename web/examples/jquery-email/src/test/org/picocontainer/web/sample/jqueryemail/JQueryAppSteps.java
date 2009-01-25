package org.picocontainer.web.sample.jqueryemail;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.jbehave.Ensure.ensureThat;

import org.jbehave.scenario.annotations.Given;
import org.jbehave.scenario.annotations.Then;
import org.jbehave.scenario.annotations.When;
import org.jbehave.scenario.steps.Steps;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.condition.JUnitConditionRunner;
import com.thoughtworks.selenium.condition.Presence;
import com.thoughtworks.selenium.condition.Not;
import com.thoughtworks.selenium.condition.ConditionRunner;


public class JQueryAppSteps extends Steps {

    private final Selenium selenium;
    private final ConditionRunner runner;

    public JQueryAppSteps(Selenium selenium, ConditionRunner runner) {
        this.selenium = selenium;
        this.runner = runner;
    }

    @Given("nobody is logged in")
    public void nobodyLoggedIn() {
        System.out.println("-->1 ");
        selenium.open("/remoting-jqueryemail-webapp/");
        runner.waitFor(new Presence("Mail"));
        selenium.click("id=logOut");
    }

    @When("user $user with password $password logs in")
    public void logIn(String user, String pw) {
        System.out.println("-->2 ");
        selenium.click("id=logIn");
        runner.waitFor(new Presence("id=userName"));
        selenium.type("userName", user);
        selenium.type("password", pw);
        selenium.click("id=submitLogin");
    }

    @Then("the Inbox should be visible")
    public void inBoxIsVisible() {
        System.out.println("-->3 ");
        runner.waitFor(new Presence("Instant Millionaire"));
    }

    @Then("the Inbox should not be visible")
    public void inBoxIsNotVisible() {
        System.out.println("-->4 ");
        runner.waitFor(new Not(new Presence("Instant Millionaire")));
    }

    @Then("'Invalid User' should be visible")
    public void errInPage() throws InterruptedException {
        Thread.sleep(100000);
        runner.waitFor(new Presence("Invalid User"));
    }


}








