package org.picocontainer.web.sample.jqueryemail;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.HashMap;

public class Auth {

    private UserStore userStore;

    public Auth(UserStore userStore) {
        this.userStore = userStore;
    }

    public String whoIsLoggedIn(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) {
            return "";
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("userName")) {
                return cookie.getValue();
            }
        }
        return "";
    }

    public void logIn(String userName, String password, HttpSession session, HttpServletResponse resp) {
        User user = userStore.getUser(userName);
        if (user != null) {
            String actualPassword = user.getPassword();
            if (actualPassword.equals(password)) {
                writeCookie(userName, resp);
                return;
            }
        }
        writeCookie("", resp);
        throw new JQueryEmailException("Invalid Login. User name or password incorrect.");
    }

    public void logOut(HttpServletResponse resp) {
        writeCookie("", resp);
    }

    private void writeCookie(String userName, HttpServletResponse resp) {
        Cookie cookie = new Cookie("userName", userName);
        cookie.setPath("/");
        resp.addCookie(cookie);
    }

}
