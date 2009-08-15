package org.picocontainer.web.sample.ajaxemail.scenarios;

import org.jbehave.scenario.JUnitScenario;
import org.jbehave.scenario.PropertyBasedConfiguration;
import org.jbehave.scenario.parser.ClasspathScenarioDefiner;
import org.jbehave.scenario.parser.PatternScenarioParser;
import org.jbehave.scenario.parser.UnderscoredCamelCaseResolver;
import org.jbehave.scenario.reporters.PrintStreamScenarioReporter;
import org.jbehave.scenario.reporters.ScenarioReporter;
import org.picocontainer.web.sample.ajaxemail.scenarios.steps.JQueryEmailSteps;

public class GilBatesCanLogIn extends JUnitScenario {

    public GilBatesCanLogIn() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public GilBatesCanLogIn(final ClassLoader classLoader) {
        super(new PropertyBasedConfiguration() {
            @Override
            public ClasspathScenarioDefiner forDefiningScenarios() {
                return new ClasspathScenarioDefiner(new UnderscoredCamelCaseResolver(".scenario"),
                        new PatternScenarioParser(this), classLoader);
            }
            @Override
			public ScenarioReporter forReportingScenarios() {
				return new PrintStreamScenarioReporter();
			}
        });
        super.addSteps(new JQueryEmailSteps());
    }
}