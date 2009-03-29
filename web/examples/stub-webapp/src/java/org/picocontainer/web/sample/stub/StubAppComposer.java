package org.picocontainer.web.sample.stub;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.web.WebappComposer;

import javax.servlet.ServletContext;

public class StubAppComposer implements WebappComposer {

    public void composeApplication(MutablePicoContainer pico, ServletContext context) {
        pico.addComponent(AppScoped.class);
    }

    public void composeSession(MutablePicoContainer pico) {
        pico.addComponent(SessionScoped.class);
    }

    public void composeRequest(MutablePicoContainer pico) {
        pico.addComponent(RequestScoped.class);
    }
}
