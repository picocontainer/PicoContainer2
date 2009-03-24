package org.picocontainer.web.sample.jqueryemail;

import org.picocontainer.MutablePicoContainer;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import java.util.logging.Logger;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

public class JdoJQueryEmailWebappComposer extends JQueryEmailWebappComposer {
    protected void composeStores(MutablePicoContainer pico) {
        try {
            pico.addComponent(MessageStore.class, JDOMessageStore.class);
            pico.addComponent(UserStore.class, JDOUserStore.class);
            PersistenceManagerFactory factory = JDOHelper.getPersistenceManagerFactory("transactional");
            pico.addComponent(PersistenceManager.class, factory.getPersistenceManager());
        } catch (Throwable e) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(byteArrayOutputStream));
            StackTraceElement[] stackTraceElements = e.getStackTrace();
            String msg = "oops! " + e.getMessage() + "\n" + e.getClass().getName() + "\n";
            msg = msg + byteArrayOutputStream.toString();
            Logger.getAnonymousLogger().info(msg);
        }

    }

    public void composeRequest(MutablePicoContainer pico) {
        super.composeRequest(pico);
        pico.addComponent(LoadDummyData.class);
    }
}