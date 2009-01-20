package org.picocontainer.web.sample.jqueryemailui;

import org.picocontainer.web.StringFromCookie;
import org.picocontainer.injectors.ProviderAdapter;

import javax.servlet.http.HttpServletRequest;

public class User {

    private String userName;

    public User(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return userName;
    }

    public static class FromCookie extends ProviderAdapter {
        private StringFromCookie stringFromCookie = new StringFromCookie("userName");
        public FromCookie() {
            super();
        }

        @Override
        public Object getComponentKey() {
            return User.class;    
        }

        @Override
        public Class getComponentImplementation() {
            return User.class;    
        }

        public User provide(HttpServletRequest req) {
            try {
                return new User(stringFromCookie.provide(req));
            } catch (StringFromCookie.CookieNotFound e) {
                throw new NotLoggedIn();
            }
        }
    }

    public static class NotLoggedIn extends MailAppException {
         NotLoggedIn() {
            super("not logged in");
        }
    }
}
