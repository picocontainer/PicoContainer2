package org.picocontainer.web.sample.jqueryemail;

import org.jbehave.scenario.JUnitScenario;
import org.jbehave.scenario.PropertyBasedConfiguration;
import org.jbehave.scenario.reporters.PrintStreamScenarioReporter;
import org.jbehave.scenario.reporters.ScenarioReporter;
import org.jbehave.scenario.parser.PatternScenarioParser;
import org.jbehave.scenario.parser.ClasspathScenarioDefiner;
import org.jbehave.scenario.parser.UnderscoredCamelCaseResolver;
import org.junit.Before;
import org.junit.After;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.condition.JUnitConditionRunner;
import com.thoughtworks.selenium.condition.ConditionRunner;

public class GilBatesCanLogInScenario extends JUnitScenario {

    private Selenium selenium;

    @Before
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://localhost");
        ConditionRunner runner = new JUnitConditionRunner(selenium, 10, 100, 1000);
        super.addSteps(new JQueryAppSteps(selenium, runner));
        selenium.start();

    }

    @After
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        selenium.close();
        selenium.stop();
    }

    public GilBatesCanLogInScenario() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public GilBatesCanLogInScenario(final ClassLoader classLoader) {
        super(new PropertyBasedConfiguration() {
            @Override
            public ClasspathScenarioDefiner forDefiningScenarios() {
                return new ClasspathScenarioDefiner(new UnderscoredCamelCaseResolver(), 
                        new PatternScenarioParser(this), classLoader);
            }
            @Override
			public ScenarioReporter forReportingScenarios() {
				return new PrintStreamScenarioReporter();
			}
        });
    }
}