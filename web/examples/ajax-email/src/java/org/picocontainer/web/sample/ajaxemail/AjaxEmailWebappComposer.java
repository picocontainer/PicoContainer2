package org.picocontainer.web.sample.ajaxemail;

import static org.picocontainer.Characteristics.USE_NAMES;

import javax.servlet.ServletContext;
import javax.jdo.PersistenceManager;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.web.WebappComposer;
import org.picocontainer.web.remoting.PicoWebRemotingMonitor;

public class AjaxEmailWebappComposer implements WebappComposer {

    public void composeApplication(MutablePicoContainer pico, ServletContext context) {
        pico.addComponent(PicoWebRemotingMonitor.class, AjaxEmailWebRemotingMonitor.class);
        pico.addComponent(UserStore.class);
        PersistenceManagerFactory factory = JDOHelper.getPersistenceManagerFactory("transactional");
        pico.addComponent(PersistenceManager.class, factory.getPersistenceManager());
        pico.addComponent(QueryStore.class);
    }

    public void composeSession(MutablePicoContainer pico) {
        // stateless
    }

    public void composeRequest(MutablePicoContainer pico) {

        pico.addAdapter(new User.FromCookie());
        pico.as(USE_NAMES).addComponent(Auth.class);

        pico.as(USE_NAMES).addComponent(IInbox.class, JdoInbox.class);
        pico.as(USE_NAMES).addComponent(ISent.class, JdoSent.class);
        pico.addComponent(ReloadData.class);

    }

}
