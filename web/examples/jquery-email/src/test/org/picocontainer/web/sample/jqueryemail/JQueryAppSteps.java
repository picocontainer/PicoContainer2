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
import com.thoughtworks.selenium.condition.Condition;


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
        try {
            selenium.click("link=Log Out");
        } catch (Exception e) {
        }
    }

    @When("user $user with password $password logs in")
    public void logIn(String user, String pw) throws InterruptedException {
        waitFor(new Presence("id=userName"));
        Thread.sleep(500);
        selenium.type("id=userName", user);
        selenium.type("id=password", pw);
        selenium.click("id=submitLogin");
    }

    @Then("the Inbox should be visible")
    public void inBoxIsVisible() {
        waitFor(new Text("Instant Millionaire"));
    }

    private void waitFor(Condition condition) {
        runner.waitFor(condition);
    }

    @Then("the Inbox should not be visible")
    public void inBoxIsNotVisible() throws InterruptedException {
        waitFor(new Not(new Text("Instant Millionaire")));
    }

    @Then("'Invalid Login' should be visible")
    public void invalidLogin() throws InterruptedException {
        waitFor(new Text("Invalid Login"));
    }


}








