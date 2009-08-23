package org.picocontainer.web.sample.ajaxemail.scenarios.steps;

import org.jbehave.scenario.JUnitScenario;
import org.jbehave.scenario.PropertyBasedConfiguration;
import org.jbehave.scenario.parser.ClasspathScenarioDefiner;
import org.jbehave.scenario.parser.PatternScenarioParser;
import org.jbehave.scenario.parser.UnderscoredCamelCaseResolver;
import org.jbehave.scenario.reporters.PrintStreamScenarioReporter;
import org.jbehave.scenario.reporters.ScenarioReporter;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.DefaultSelenium;

public class AjaxEmailScenario extends JUnitScenario {

    private Selenium selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://localhost:8080");

    public AjaxEmailScenario() {
        this(Thread.currentThread().getContextClassLoader(), new CurrrentScenario());
    }

    public AjaxEmailScenario(final ClassLoader classLoader) {
        this(classLoader, new CurrrentScenario());
    }

    public AjaxEmailScenario(final ClassLoader classLoader, final CurrrentScenario currentScenario) {
        super(new PropertyBasedConfiguration() {
            @Override
            public ClasspathScenarioDefiner forDefiningScenarios() {
                return new ClasspathScenarioDefiner(new UnderscoredCamelCaseResolver(".scenario"),
                        new PatternScenarioParser(this), classLoader);
            }
            @Override
			public ScenarioReporter forReportingScenarios() {
				return new PrintStreamScenarioReporter() {
                    @Override
                    public void beforeScenario(String title) {
                        currentScenario.setCurrentScenario(title);
                        super.beforeScenario(title);
                    }
                };
			}
        });
        AjaxEmailSteps steps = new AjaxEmailSteps(selenium, currentScenario) {
            @Override
            protected Selenium createSelenium() {
                return AjaxEmailScenario.this.selenium;
            }
        };

        super.addSteps(steps);
    }

    public static class CurrrentScenario {
        String currentScenario;

        public String getCurrentScenario() {
            return currentScenario;
        }

        public void setCurrentScenario(String currentScenario) {
            this.currentScenario = currentScenario;
        }
    }
}