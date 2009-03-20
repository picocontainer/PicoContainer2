package org.picocontainer.web.sample.jqueryemail;

import org.picocontainer.MutablePicoContainer;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

public class JdoJQueryEmailWebappComposer extends JQueryEmailWebappComposer {
    protected void composeStores(MutablePicoContainer pico) {
        pico.addComponent(MessageStore.class, JDOMessageStore.class);
        pico.addComponent(UserStore.class, JDOUserStore.class);
        pico.addComponent(PersistenceManager.class, JDOHelper.getPersistenceManagerFactory("transactional").getPersistenceManager());

    }

    public void composeRequest(MutablePicoContainer pico) {
        super.composeRequest(pico);
        pico.addComponent(LoadDummyData.class);
    }
}