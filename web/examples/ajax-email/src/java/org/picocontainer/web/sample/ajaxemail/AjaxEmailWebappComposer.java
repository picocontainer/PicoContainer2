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
        pico.addComponent(PersistenceManagerWrapper.class, getPersistenceManagerWrapperClass());
        pico.addComponent(QueryStore.class);
        pico.addAdapter(new CacheProvider());
    }

    public void composeSession(MutablePicoContainer pico) {
        // stateless
    }

    public void composeRequest(MutablePicoContainer pico) {

        pico.addAdapter(new UserFromCookieProvider());
        pico.as(USE_NAMES).addComponent(Auth.class);

        pico.as(USE_NAMES).addComponent(Inbox.class);
        pico.as(USE_NAMES).addComponent(Sent.class);
        pico.addComponent(ReloadData.class);

    }

    /**
     * Some ugliness to determine if the user is deploying the app via "maven jetty:run-war"
     * @return
     */
    private Class<? extends PersistenceManagerWrapper> getPersistenceManagerWrapperClass() {
        boolean isMaven = false;
        try {
            throw new RuntimeException();
        } catch (RuntimeException re) {
            for (StackTraceElement ste : re.getStackTrace()) {
                if (ste.getClassName().contains("maven")) {
                    isMaven = true;
                }
            }
        }
        if (isMaven) {
            return InMemoryPersistenceManagerWrapper.class;
        } else {
            return JdoPersistenceManagerWrapper.class;
        }

    }

}
