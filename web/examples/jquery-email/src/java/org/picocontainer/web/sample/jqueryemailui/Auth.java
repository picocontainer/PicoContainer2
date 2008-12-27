package org.picocontainer.web.sample.jqueryemailui;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.HashMap;

public class Auth {

    Map<String, String> users = new HashMap<String, String>();

    {
        users.put(InMemoryMessageStore.GIL_BATES, "1234");
        users.put(InMemoryMessageStore.BEEVE_SALMER, "1234");
    }

    public String whoIsLoggedIn(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("userName")) {
                return cookie.getValue();
            }
        }
        return "";
    }

    public void logIn(String userName, String password, HttpSession session, HttpServletResponse resp) {
        String actualPassword = users.get(userName);
        if (actualPassword == null || !actualPassword.equals(password)) {
            writeCookie("", resp);
            throw new RuntimeException("Invalid Login. User name or password incorrect.");
        }
        writeCookie(userName, resp);
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
