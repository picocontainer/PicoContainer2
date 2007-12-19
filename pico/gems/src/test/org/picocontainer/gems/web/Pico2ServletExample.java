package org.picocontainer.gems.web;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.behaviors.Storing;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;

/** @author Paul Hammant */
public class Pico2ServletExample extends HttpServlet {

    private DefaultPicoContainer requestContainer;

    private HttpSessionStoringAdapter sessionStoringAdapter;
    private HttpSessionStoringAdapter requestStoringAdapter;

    public void init(ServletConfig cfg) throws ServletException {

        PicoContainer appContainer = new DefaultPicoContainer(new Caching());

        Storing sessionStore = new Storing();
        PicoContainer sessionContainer = new DefaultPicoContainer(sessionStore, appContainer);
        sessionStoringAdapter = new HttpSessionStoringAdapter(sessionStore, "sessionStore");

        Storing requestStore = new Storing();
        requestContainer = new DefaultPicoContainer(requestStore, sessionContainer);
        requestStoringAdapter = new HttpSessionStoringAdapter(requestStore, "requestStore");

        // populate app, session and request scoped containers.
        // appContainer.addComponent(HibernateManager.class, MyHibernateManager.class);
        // sessionContainer.addComponent(ShoppingCart.class, FifoCart.class);
        // requestContainer.addComponent("/addToCart.do", AddToCart.class);
        // requestContainer.addComponent("/removeFromCart.do", RemoveFromCart.class);
        // etc

    }

    protected void service(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        sessionStoringAdapter.retrieveOrCreateStore(req.getSession());
        requestStoringAdapter.resetStore();

        Action action = (Action) requestContainer.getComponent(req.getPathTranslated());

        action.execute(req, resp);

        sessionStoringAdapter.invalidateStore();
        requestStoringAdapter.invalidateStore();
    }

    public static class Action {
        public void execute(HttpServletRequest req, HttpServletResponse resp) {
            // whatever
        }
    }

}
