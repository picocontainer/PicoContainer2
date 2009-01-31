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
		public User provide(HttpServletRequest req) {
			try {
				String name = new StringFromCookie("userName").provide(req);
				if (name.equals("")) {
					throw new NotLoggedIn();
				}
				return new User(name);
			} catch (StringFromCookie.CookieNotFound e) {
				throw new NotLoggedIn();
			}
		}
	}

	@SuppressWarnings("serial")
	public static class NotLoggedIn extends JQueryEmailException {
		NotLoggedIn() {
			super("not logged in");
		}
	}
}
