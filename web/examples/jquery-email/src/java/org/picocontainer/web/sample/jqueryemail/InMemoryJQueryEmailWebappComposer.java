package org.picocontainer.web.sample.jqueryemail;

import org.picocontainer.MutablePicoContainer;

public class InMemoryJQueryEmailWebappComposer extends JQueryEmailWebappComposer {
    protected void composeStores(MutablePicoContainer pico) {
        pico.addComponent(MessageStore.class, InMemoryMessageStore.class);
        pico.addComponent(UserStore.class, InMemoryUserStore.class);
    }
}
