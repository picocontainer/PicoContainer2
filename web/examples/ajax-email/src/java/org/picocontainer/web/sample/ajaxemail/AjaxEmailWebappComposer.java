package org.picocontainer.web.sample.ajaxemail;

import static org.picocontainer.Characteristics.USE_NAMES;

import javax.servlet.ServletContext;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.web.WebappComposer;
import org.picocontainer.web.caching.FallbackCacheProvider;
import org.picocontainer.web.remoting.PicoWebRemotingMonitor;

public class AjaxEmailWebappComposer implements WebappComposer {

    public void composeApplication(MutablePicoContainer container, ServletContext context) {
        container.addComponent(PicoWebRemotingMonitor.class, AjaxEmailWebRemotingMonitor.class);
        container.addComponent(UserStore.class);
        container.addComponent(PersistenceManagerWrapper.class, getPersistenceManagerWrapperClass());
        container.addComponent(QueryStore.class);
        container.addAdapter(new FallbackCacheProvider());
    }

    public void composeSession(MutablePicoContainer container) {
        // stateless
    }

    public void composeRequest(MutablePicoContainer container) {

        container.addAdapter(new UserFromCookieProvider());
        container.as(USE_NAMES).addComponent(Auth.class);

        container.as(USE_NAMES).addComponent(Inbox.class);
        container.as(USE_NAMES).addComponent(Sent.class);
        container.addComponent(ReloadData.class);

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
