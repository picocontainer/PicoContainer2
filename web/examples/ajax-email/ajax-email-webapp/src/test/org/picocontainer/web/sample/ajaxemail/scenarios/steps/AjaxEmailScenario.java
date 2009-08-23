package org.picocontainer.web.sample.ajaxemail.scenarios.steps;

import org.jbehave.scenario.JUnitScenario;
import org.jbehave.scenario.PropertyBasedConfiguration;
import org.jbehave.scenario.errors.PendingErrorStrategy;
import org.jbehave.scenario.parser.ClasspathScenarioDefiner;
import org.jbehave.scenario.parser.PatternScenarioParser;
import org.jbehave.scenario.parser.UnderscoredCamelCaseResolver;
import org.jbehave.scenario.reporters.PrintStreamScenarioReporter;
import org.jbehave.scenario.reporters.ScenarioReporter;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.DefaultSelenium;

public class AjaxEmailScenario extends JUnitScenario {

    private Selenium selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://localhost:8080");
	private AjaxEmailSteps ajaxEmailSteps;

    public AjaxEmailScenario() {
        this(Thread.currentThread().getContextClassLoader(), new CurrentScenario());
    }

    public AjaxEmailScenario(final ClassLoader classLoader) {
        this(classLoader, new CurrentScenario());
    }

    public AjaxEmailScenario(final ClassLoader classLoader, final CurrentScenario currentScenario) {
        super(new PropertyBasedConfiguration() {
            @Override
            public ClasspathScenarioDefiner forDefiningScenarios() {
                return new ClasspathScenarioDefiner(new UnderscoredCamelCaseResolver(".scenario"),
                        new PatternScenarioParser(this), classLoader);
            }
            @Override
            public PendingErrorStrategy forPendingSteps() {
                return PendingErrorStrategy.FAILING;
            }
            @Override
			public ScenarioReporter forReportingScenarios() {
				return new PrintStreamScenarioReporter() {
                    @Override
                    public void beforeScenario(String title) {
                        currentScenario.setCurrent(title);
                        super.beforeScenario(title);
                    }
                };
			}
        });

         ajaxEmailSteps = new AjaxEmailSteps(selenium, currentScenario) {
            @Override
            protected Selenium createSelenium() {
                return AjaxEmailScenario.this.selenium;
            }
        };
		super.addSteps(ajaxEmailSteps);
    }
    
    public AjaxEmailSteps getAjaxEmailSteps() {
		return ajaxEmailSteps;
	}



	public static class CurrentScenario {
        String currentScenario;

        public String toString() {
            return currentScenario;
        }

        public void setCurrent(String current) {
            this.currentScenario = current;
        }
    }
}