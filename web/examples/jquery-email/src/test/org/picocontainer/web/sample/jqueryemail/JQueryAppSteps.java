package org.picocontainer.web.sample.jqueryemail;

import org.jbehave.scenario.annotations.Given;
import org.jbehave.scenario.annotations.Then;
import org.jbehave.scenario.annotations.When;
import org.jbehave.scenario.steps.PrintStreamStepMonitor;
import org.jbehave.scenario.steps.Steps;
import org.jbehave.scenario.steps.StepsConfiguration;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.condition.ConditionRunner;
import com.thoughtworks.selenium.condition.Not;
import com.thoughtworks.selenium.condition.Presence;
import com.thoughtworks.selenium.condition.Text;


public class JQueryAppSteps extends Steps {

    private final Selenium selenium;
    private final ConditionRunner runner;
    private static final StepsConfiguration configuration = new StepsConfiguration();
    
    public JQueryAppSteps(Selenium selenium, ConditionRunner runner) {
    	super(configuration);
        this.configuration.useMonitor(new PrintStreamStepMonitor());        
    	this.selenium = selenium;
        this.runner = runner;
    }

    @Given("nobody is logged in")
    public void nobodyLoggedIn() {
        selenium.open("/remoting-jqueryemail-webapp/");
        //runner.waitFor(new Presence("Mail"));
        try {
            selenium.click("id=logOut");
        } catch (Exception e) {
        }
    }

    @When("user $user with password $password logs in")
    public void logIn(String user, String pw) {
        runner.waitFor(new Presence("id=userName"));
        selenium.type("userName", user);
        selenium.type("password", pw);
        selenium.click("id=submitLogin");
    }

    @Then("the Inbox should be visible")
    public void inBoxIsVisible() {
        runner.waitFor(new Text("Instant Millionaire"));
    }

    @Then("the Inbox should not be visible")
    public void inBoxIsNotVisible() {
        runner.waitFor(new Not(new Text("Instant Millionaire")));
    }

    @Then("'Invalid User' should be visible")
    public void errInPage() throws InterruptedException {
        runner.waitFor(new Text("Invalid User"));
    }


}








