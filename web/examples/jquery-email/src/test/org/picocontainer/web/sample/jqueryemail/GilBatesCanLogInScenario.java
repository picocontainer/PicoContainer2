package org.picocontainer.web.sample.jqueryemail;

import org.jbehave.scenario.JUnitScenario;
import org.jbehave.scenario.PropertyBasedConfiguration;
import org.jbehave.scenario.parser.PatternScenarioParser;
import org.jbehave.scenario.parser.ClasspathScenarioDefiner;
import org.jbehave.scenario.parser.UnderscoredCamelCaseResolver;
import org.junit.Before;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.condition.JUnitConditionRunner;

public class GilBatesCanLogInScenario extends JUnitScenario {

    private Selenium selenium;

    @Before
    protected void setUp() throws Exception {
        super.setUp();
        selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://localhost");
        super.addSteps(new JQueryAppSteps(selenium,
                   new JUnitConditionRunner(selenium, 10, 100, 1000)));

    }

    public GilBatesCanLogInScenario() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public GilBatesCanLogInScenario(final ClassLoader classLoader) {
        super(new PropertyBasedConfiguration() {
            @Override
            public ClasspathScenarioDefiner forDefiningScenarios() {
                return new ClasspathScenarioDefiner(new UnderscoredCamelCaseResolver(), new PatternScenarioParser(this),
                        classLoader);
            }
        });
    }
}